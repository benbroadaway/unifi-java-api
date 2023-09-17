package org.benbroadaway.unifi.actions.usp;

import org.benbroadaway.unifi.Device;
import org.benbroadaway.unifi.OutletOverride;
import org.benbroadaway.unifi.actions.AbstractAction;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.UnifiHttpClient;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.util.concurrent.Callable;

public class GetRelayState extends AbstractAction implements Callable<ActionResult<Boolean>> {
    private final String plugName;

    public static GetRelayState getInstance(String unifiHost,
                                            String plugName,
                                            ApiCredentials unifiCreds,
                                            boolean validateCerts) {
        return new GetRelayState(unifiHost, plugName, unifiCreds, validateCerts);
    }

    private GetRelayState(String unifiHost, String plugName, ApiCredentials unifiCreds, boolean validateCerts) {
        this(plugName, new UnifiHttpClient(unifiHost, unifiCreds, validateCerts));
    }

    GetRelayState(String plugName, UnifiHttpClient unifiClient) {
        super(unifiClient);
        this.plugName = plugName;
    }

    @Override
    public ActionResult<Boolean> call() {
        var currentDevice = getCurrentDevice("UP1", plugName);
        var relayIndex = 1;
        var currentState = getOutletRelayState(currentDevice, relayIndex); // TODO handle multi-outlet devices

        return ActionResult.<Boolean>builder()
                .ok(true)
                .data(currentState)
                .build();
    }

    private boolean getOutletRelayState(Device device, int outlet) {
        return device.outletOverrides().stream()
                .filter(oo -> oo.index() == outlet)
                .map(OutletOverride::relayState)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No outlet at index: " + outlet));
    }

}
