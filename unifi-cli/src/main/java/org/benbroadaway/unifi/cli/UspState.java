package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.usp.RelayState;
import org.benbroadaway.unifi.actions.usp.RelayStateToggle;
import org.benbroadaway.unifi.cli.completion.BooleanCandidates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;
import static picocli.CommandLine.Model.CommandSpec;

@Command(name = "usp-state", description = "Get USP relay state")
public class UspState implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(UspState.class);

    @Spec
    @SuppressWarnings("unused")
    private CommandSpec spec;

    @ParentCommand
    @SuppressWarnings("unused")
    private Device parent;

    @Option(names = {"-d", "--device-name"}, description = "USP device name to act upon")
    String deviceName;

    @Override
    public Integer call() {
        log.warn("sub-command is missing");

        return 1;
    }

    @Command(name = "get", description = "Get current USP relay state")
    void getState() {
        var credentials = parent.getCredentials();
        var relayStateToggle = RelayState.get(parent.unifiHost, deviceName, credentials, parent.validateCerts);

        try {
            var currentState = relayStateToggle.call()
                    .data()
                    .orElseThrow(() -> new IllegalStateException("No response!"));
            log.info("Current state: {}", currentState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Command(name = "set", description = "Set USP relay state")
    void setState(@Option(names = {"--relay-state"},
                          arity = "1",
                          description = "USP device plug state. Candidates: ${COMPLETION-CANDIDATES}",
                          completionCandidates = BooleanCandidates.class)
                  boolean relayState) {
        log.info("unifi-host: {}", parent.unifiHost);
        log.info("toggling device: {}", deviceName);
        log.info("rawRelayState: {}", relayState);

        var credentials = parent.getCredentials();
        var relayStateToggle = RelayStateToggle.get(parent.unifiHost, deviceName, credentials, parent.validateCerts, relayState);

        try {
            relayStateToggle.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
