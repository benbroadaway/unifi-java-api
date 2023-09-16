package org.benbroadaway.unifi.cli;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine cli = new CommandLine(new App());
        int code = cli.execute(args);
        System.exit(code);
    }
}
