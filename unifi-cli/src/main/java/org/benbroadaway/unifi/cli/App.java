package org.benbroadaway.unifi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Command(name = "unifi", subcommands = {Device.class})
public class App implements Runnable {
    @Spec
    @SuppressWarnings("unused")
    private CommandSpec spec;

    @Option(names = {"-h", "--help"}, usageHelp = true, scope = CommandLine.ScopeType.INHERIT,
            description = "display the command's help message")
    @SuppressWarnings("unused")
    boolean helpRequested = false;

    @Option(names = {"--version"}, description = "display version")
    boolean versionRequested = false;

    @Override
    public void run() {
        if (versionRequested) {
            System.out.println(VersionInfo.getVersion());
            return;
        }

        spec.commandLine().usage(System.out);
    }
}
