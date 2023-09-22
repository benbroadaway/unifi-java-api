package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.usp.GetRelayState;
import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.benbroadaway.unifi.cli.mixins.CLIAuth;
import org.benbroadaway.unifi.cli.mixins.Log;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;
import static picocli.CommandLine.Model.CommandSpec;

@Command(name = "usp-state", description = "Perform state actions on USP device")
public class UspState implements Callable<Integer> {
    @Spec
    @SuppressWarnings("unused")
    private CommandSpec spec;

    @ParentCommand
    Device device;

    @Mixin
    Log log;

    @Mixin
    @SuppressWarnings("unused")
    CLIAuth cliAuth;

    @Override
    public Integer call() {
        log.warn("sub-command is missing");

        return 1;
    }

    @Command(name = "get", description = "Get current USP relay state")
    int getState(@Mixin CLIAuth cliAuth, @Mixin Log log) {
        log.debug("     unifi-host: {}", device.unifiHost);
        log.debug("toggling device: {}", device.deviceName);

        var relayState = tryIt(() -> {
            var credentials = device.getCredentials(cliAuth, () -> spec);
            var getRelayState = actionForGet(credentials);
            return getRelayState.call();
        });

        if (!relayState.ok()) {
            return 1;
        }

        var currentState = relayState.data()
                .orElseThrow(() -> new IllegalStateException("No response!"));
        log.info("{}", currentState);

        return 0;
    }

    private <T> ActionResult<T> tryIt(Callable<ActionResult<T>> c) {
        try {
            return c.call();
        } catch (Exception e) {
            log.error("Call error: {}", e.getMessage());
            log.trace("", e);
        }

        return ActionResult.<T>builder().ok(false).build();
    }

    @Command(name = "set", description = "Set USP relay state")
    int setState(@Mixin CLIAuth cliAuth,
                 @Mixin Log log,
                 @Option(names = {"--relay-state"},
                         arity = "1",
                         description = "USP device plug state. Candidates: ${COMPLETION-CANDIDATES}",
                         completionCandidates = BooleanCandidates.class)
                 boolean relayState) {
        log.debug("     unifi-host: {}", device.unifiHost);
        log.debug("toggling device: {}", device.deviceName);
        log.debug("     relayState: {}", relayState);

        var result = tryIt(() -> {
            var credentials = device.getCredentials(cliAuth, () -> spec);
            var relayStateToggle = actionForSet(credentials, relayState);
            return relayStateToggle.call();
        });

        return result.ok() ? 0 : 1;
    }

    GetRelayState actionForGet(ApiCredentials credentials) {
        return GetRelayState.getInstance(device.unifiHost, device.deviceName, credentials, device.validateCerts);
    }

    SetRelayState actionForSet(ApiCredentials credentials, boolean relayState) {
        return SetRelayState.getInstance(device.unifiHost, device.deviceName, credentials, device.validateCerts, relayState);
    }
}
