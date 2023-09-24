package org.benbroadaway.unifi.actions.usp;

import org.benbroadaway.unifi.Outlet;
import org.benbroadaway.unifi.actions.AbstractAction;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.UnifiHttpClient;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.util.List;
import java.util.concurrent.Callable;

public class GetRelayState extends AbstractAction implements Callable<ActionResult<List<Outlet>>> {
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
    public ActionResult<List<Outlet>> call() {
        var currentDevice = getCurrentDevice("UP1", plugName);

        return ActionResult.<List<Outlet>>builder()
                .ok(true)
                .data(currentDevice.outletOverrides())
                .build();
    }
}
