package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.usp.RelayState;
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
    private Device parent;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display the command's help message")
    @SuppressWarnings("unused")
    boolean helpRequested = false;

    @Option(names = {"-d", "--device-name"}, description = "USP device name to act upon")
    String deviceName;

    @Override
    public Integer call() {
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

        return 0;
    }
}
