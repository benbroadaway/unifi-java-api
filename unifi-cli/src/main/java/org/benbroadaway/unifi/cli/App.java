package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.cli.mixins.Log;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import static picocli.CommandLine.Mixin;
import static picocli.CommandLine.ScopeType;

@Command(name = "unifi", subcommands = { Device.class })
public class App implements Runnable {
    @Spec
    @SuppressWarnings("unused")
    private CommandSpec spec;

    @Mixin
    Log log;

    @Option(names = {"-h", "--help"}, usageHelp = true, scope = ScopeType.INHERIT,
            description = "display the command's help message")
    @SuppressWarnings("unused")
    boolean helpRequested = false;

    @Option(names = {"--version"}, description = "display version")
    boolean versionRequested = false;

    @Override
    public void run() {
        if (versionRequested) {
            log.info(VersionInfo.getVersion());
            return;
        }

        log.info(spec.commandLine().getUsageMessage());
    }
}
