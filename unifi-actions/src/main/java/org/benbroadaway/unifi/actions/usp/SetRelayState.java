package org.benbroadaway.unifi.actions.usp;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.benbroadaway.unifi.Device;
import org.benbroadaway.unifi.ImmutableDevice;
import org.benbroadaway.unifi.ImmutableOutletOverride;
import org.benbroadaway.unifi.OutletOverride;
import org.benbroadaway.unifi.actions.AbstractAction;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.UnifiHttpClient;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.benbroadaway.unifi.exception.UnifiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SetRelayState extends AbstractAction implements Callable<ActionResult<Void>> {
    private static final Logger log = LoggerFactory.getLogger(SetRelayState.class);

    private final String plugName;
    private final int index;
    private final boolean relayState;

    public static SetRelayState getInstance(String unifiHost,
                                            String plugName,
                                            int index,
                                            ApiCredentials unifiCreds,
                                            boolean validateCerts,
                                            boolean relayState) {
        return new SetRelayState(unifiHost, plugName, index, unifiCreds, validateCerts, relayState);
    }

    private SetRelayState(String unifiHost, String plugName, int index, ApiCredentials unifiCreds, boolean validateCerts, boolean relayState) {
        this(plugName, index, relayState, new UnifiHttpClient(unifiHost, unifiCreds, validateCerts));
    }

    SetRelayState(String plugName, int index, boolean relayState, UnifiHttpClient unifiClient) {
        super(unifiClient);
        this.plugName = plugName;
        this.index = index;
        this.relayState = relayState;
    }

    @Override
    public ActionResult<Void> call() {
        var currentDevice = getCurrentDevice("UP1", plugName);
        var currentState = getOutletRelayState(currentDevice, index);
        if (currentState == relayState) {
            log.info("Current state matches desired state.");
            return ActionResult.success();
        }

        var deviceId = currentDevice.deviceId()
                .orElseThrow(() -> new IllegalStateException("no device id found"));
        var outletOverrides = currentDevice.outletOverrides().stream()
                // outlet indices are 1-based
                .map(oo -> oo.index() != index ? oo : ImmutableOutletOverride.builder()
                        .from(oo)
                        .relayState(relayState)
                        .build())
                .toList();

        var desiredState = ImmutableDevice.builder()
                .from(currentDevice)
                .outletOverrides(outletOverrides)
                .deviceId(Optional.empty())
                .build();

        updateDevice(desiredState, deviceId);

        return ActionResult.success();
    }

    private boolean getOutletRelayState(Device device, int outlet) {
        return device.outletOverrides().stream()
                .filter(oo -> oo.index() == outlet)
                .map(OutletOverride::relayState)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No outlet number: " + outlet));
    }

    private void updateDevice(Device device, String deviceId) {
        var unifiClient = getUnifiClient();
        var uri = unifiClient.resolve("/proxy/network/api/s/default/rest/device/" + deviceId);

        var req = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(serializeBody(device), StandardCharsets.UTF_8))
                .uri(uri)
                .header("Content-Type", UnifiHttpClient.APPLICATION_JSON)
                .header("Accept", UnifiHttpClient.APPLICATION_JSON)
                .headers(unifiClient.getCsrfHeader())
                .build();

        try {
            var resp = unifiClient.send(req, HttpResponse.BodyHandlers.discarding());

            if (resp.statusCode() != 200) {
                throw new UnifiException("Invalid response code  '" + resp.statusCode() + " received while updating device");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnifiException(e);
        } catch (Exception e) {
            throw new UnifiException("Error sending device state: " + e.getMessage());
        }
    }

    private static String serializeBody(Object o) {
        try {
            return Util.getMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new UnifiException("Error serializing JSON body: " + e.getMessage());
        }
    }
}
