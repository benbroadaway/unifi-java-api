package org.benbroadaway.unifi.actions.usp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.benbroadaway.unifi.Device;
import org.benbroadaway.unifi.ImmutableDevice;
import org.benbroadaway.unifi.ImmutableOutletOverride;
import org.benbroadaway.unifi.OutletOverride;
import org.benbroadaway.unifi.actions.ApiResponse;
import org.benbroadaway.unifi.actions.UnifiHttpClient;
import org.benbroadaway.unifi.actions.UnifiResult;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class RelayStateToggle implements Callable<UnifiResult> {
    private static final Logger log = LoggerFactory.getLogger(RelayStateToggle.class);

    private final String plugName;
    private final boolean relayState;
    private final UnifiHttpClient unifiClient;

    public static RelayStateToggle get(String unifiHost, String plugName, ApiCredentials unifiCreds, boolean validateCerts, boolean relayState) {
        return new RelayStateToggle(unifiHost, plugName, unifiCreds, validateCerts, relayState);
    }

    public RelayStateToggle(String unifiHost, String plugName, ApiCredentials unifiCreds, boolean validateCerts, boolean relayState) {
        this.plugName = plugName;
        this.relayState = relayState;
        this.unifiClient = new UnifiHttpClient(unifiHost, unifiCreds, validateCerts);
    }

    @Override
    public UnifiResult call() {
        var currentDevice = getCurrentDevice();
        var relayIndex = 1;
        var currentState = getOutletRelayState(currentDevice, relayIndex); // TODO handle multi-outlet devices
        if (currentState == relayState) {
            log.info("Current state matches desired state.");
            return UnifiResult.builder().ok(true).build();
        }

        var deviceId = currentDevice.deviceId()
                .orElseThrow(() -> new IllegalStateException("no device_id found"));
        var outletOverrides = currentDevice.outletOverrides().stream()
                // outlet indices are 1-based
                .map(oo -> oo.index() != relayIndex ? oo : ImmutableOutletOverride.builder()
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

        return UnifiResult.builder()
                .ok(true)
                .build();
    }

    private boolean getOutletRelayState(Device device, int outlet) {
        return device.outletOverrides().stream()
                .filter(oo -> oo.index() == outlet)
                .map(OutletOverride::relayState)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No outlet number: " + outlet));
    }

    private void updateDevice(Device device, String deviceId) {
        var uri = unifiClient.resolve("/proxy/network/api/s/default/rest/device/" + deviceId);

        unifiClient.withClient((client, csrfHeader) -> {
            var req = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", UnifiHttpClient.APPLICATION_JSON)
                    .header("Accept", UnifiHttpClient.APPLICATION_JSON)
                    .header("x-csrf-token", csrfHeader)
                    .PUT(HttpRequest.BodyPublishers.ofString(serializeBody(device), StandardCharsets.UTF_8))
                    .build();

            try {
                client.send(req, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException("Error sending device state: " + e.getMessage());
            }

            return null;
        });
    }

    private static String serializeBody(Object o) {
        try {
            return Util.getMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing JSON body: " + e.getMessage());
        }
    }

    private Device getCurrentDevice() {
        var uri = unifiClient.resolve("/proxy/network/api/s/default/stat/device");

        return unifiClient.withClient((client, csrfToken) -> {
            var req = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", UnifiHttpClient.APPLICATION_JSON)
                    .header("x-csrf-token", csrfToken)
                    .GET()
                    .build();

            try {
                var resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());

                if (resp.statusCode() != 200) {
                    throw new IllegalStateException("invalid response code: ${resp.statusCode()}");
                }

                JavaType listOfDevicesType = Util.getMapper().getTypeFactory().constructCollectionType(List.class, Device.class);

                ApiResponse<List<Device>> apiResponse = readBody(resp, listOfDevicesType);
                return apiResponse.data().stream()
                        .filter(d -> d.model().equals("UP1"))
                        .filter(d -> d.name().equals(plugName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No UP1 device found with name '" + plugName + "'"));
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving current device state: " + e.getMessage());
            }
        });
    }

    private <T> ApiResponse<T> readBody(HttpResponse<InputStream> resp, JavaType returnParam) {
        try (var input = resp.body()) {
            return Util.withMapper(mapper -> {
                var t = mapper.getTypeFactory().constructParametricType(ApiResponse.class, returnParam);
                return mapper.readValue(input, t);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
