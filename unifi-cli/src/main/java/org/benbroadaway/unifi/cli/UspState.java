package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.usp.GetRelayState;
import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.benbroadaway.unifi.cli.mixins.CLIAuth;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;
import static picocli.CommandLine.Model.CommandSpec;

@Command(name = "usp-state", description = "Perform state actions on USP device")
public class UspState implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(UspState.class);

    @Spec
    @SuppressWarnings("unused")
    private CommandSpec spec;

    @ParentCommand
    @SuppressWarnings("unused")
    Device parent;

    @Mixin
    @SuppressWarnings("unused")
    CLIAuth cliAuth;

    @Option(names = {"-d", "--device-name"}, scope = ScopeType.INHERIT, required = true,
            description = "Target USP device")
    protected String deviceName;

    UspState() {
    }

    @Override
    public Integer call() {
        log.warn("sub-command is missing");

        return 1;
    }

    @Command(name = "get", description = "Get current USP relay state")
    void getState(@Mixin CLIAuth cliAuth) {
        var credentials = parent.getCredentials(cliAuth, () -> spec);
        var relayState = actionForGet(credentials);

        var currentState = relayState.call()
                .data()
                .orElseThrow(() -> new IllegalStateException("No response!"));
        log.info("Current state: {}", currentState);
    }

    @Command(name = "set", description = "Set USP relay state")
    void setState(@Mixin CLIAuth cliAuth,
                  @Option(names = {"--relay-state"},
                          arity = "1",
                          description = "USP device plug state. Candidates: ${COMPLETION-CANDIDATES}",
                          completionCandidates = BooleanCandidates.class)
                  boolean relayState) {
        log.info("unifi-host: {}", parent.unifiHost);
        log.info("toggling device: {}", deviceName);
        log.info("rawRelayState: {}", relayState);

        var credentials = parent.getCredentials(cliAuth, () -> spec);
        var relayStateToggle = actionForSet(credentials, relayState);

        relayStateToggle.call();
    }

    GetRelayState actionForGet(ApiCredentials credentials) {
        return GetRelayState.getInstance(parent.unifiHost, deviceName, credentials, parent.validateCerts);
    }

    SetRelayState actionForSet(ApiCredentials credentials, boolean relayState) {
        return SetRelayState.getInstance(parent.unifiHost, deviceName, credentials, parent.validateCerts, relayState);
    }
}
