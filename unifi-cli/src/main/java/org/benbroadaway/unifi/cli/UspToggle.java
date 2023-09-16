package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.usp.RelayStateToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;
import static picocli.CommandLine.Model.CommandSpec;

@Command(name = "usp-toggle", description = "Set USP relay state")
public class UspToggle implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(UspToggle.class);

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

    @Option(names = {"--relay-state"}, description = "USP device plug state")
    Boolean relayState;

    @Override
    public Integer call() {
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

        return 0;
    }
}
