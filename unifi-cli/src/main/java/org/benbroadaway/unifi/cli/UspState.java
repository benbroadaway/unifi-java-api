package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.OutletOverride;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.usp.GetRelayState;
import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.benbroadaway.unifi.cli.mixins.CLIAuth;
import org.benbroadaway.unifi.cli.mixins.Log;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.util.List;
import java.util.Optional;
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
    int getState(@Mixin CLIAuth cliAuth, @Mixin Log log,
                 @Option(names = { "-i", "--index" }, arity = "0..1", description = "1-based outlet index to get", scope = ScopeType.INHERIT) Optional<Integer> index) {
        log.debug("     unifi-host: {}", device.unifiHost);
        log.debug("toggling device: {}", device.deviceName);

        var relayState = tryAction(() -> {
            var credentials = device.getCredentials(cliAuth, () -> spec);
            return actionForGet(credentials).call();
        });

        if (!relayState.ok()) {
            return 1;
        }

        var currentState = relayState.data()
                .orElseThrow(() -> new IllegalStateException("No response!"));

        index.ifPresentOrElse(i -> printIndex(currentState, i), () -> printAll(currentState));

        return 0;
    }

    /**
     * Prints the state of a given index. Only the state (true or false) is printed
     * @param outletOverrides current state from a device
     * @param index 1-based outlet index to print
     */
    private void printIndex(List<OutletOverride> outletOverrides, int index) {
        var state = outletOverrides.stream()
                .filter(e -> e.index() == index)
                .map(OutletOverride::relayState)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No outlet with index: " + index));
        log.info("{}", state);
    }

    /**
     * Prints the state of all outlets in a table.
     * @param outletOverrides current state from a device
     */
    private void printAll(List<OutletOverride> outletOverrides) {
        String fmt = "%-10s%s";
        log.info("{}", String.format(fmt, "Index", "Relay State"));

        outletOverrides.forEach(o ->
                log.info("{}", String.format(fmt, o.index(), o.relayState())));
    }

    @Command(name = "set", description = "Set USP relay state")
    int setState(@Mixin CLIAuth cliAuth,
                 @Mixin Log log,
                 @Option(names = { "-i", "--index" },
                         required = true,
                         description = "1-based outlet index to get",
                         scope = ScopeType.INHERIT)
                 int index,
                 @Option(names = {"--relay-state"},
                         arity = "1",
                         description = "USP device plug state. Candidates: ${COMPLETION-CANDIDATES}",
                         completionCandidates = BooleanCandidates.class)
                 boolean relayState) {
        log.debug("     unifi-host: {}", device.unifiHost);
        log.debug("toggling device: {}", device.deviceName);
        log.debug("     relayState: {}", relayState);

        var result = tryAction(() -> {
            var credentials = device.getCredentials(cliAuth, () -> spec);
            return actionForSet(credentials, index, relayState).call();
        });

        return result.ok() ? 0 : 1;
    }

    GetRelayState actionForGet(ApiCredentials credentials) {
        return GetRelayState.getInstance(device.unifiHost, device.deviceName, credentials, device.validateCerts);
    }

    SetRelayState actionForSet(ApiCredentials credentials, int index, boolean relayState) {
        return SetRelayState.getInstance(device.unifiHost, device.deviceName, index, credentials, device.validateCerts, relayState);
    }

    private <T> ActionResult<T> tryAction(Callable<ActionResult<T>> c) {
        try {
            return c.call();
        } catch (Exception e) {
            log.error("Call error: {}", e.getMessage());
            log.trace("", e);
        }

        return ActionResult.<T>builder().ok(false).build();
    }
}
