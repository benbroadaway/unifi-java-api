package org.benbroadaway.unifi.cli;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int code = 0;
        var cli = new CommandLine(new App());
        try {
            code = cli.execute(args);
        } catch (Exception e) {
            cli.usage(System.out);
            code = 99;
        }

        if (code != 0) {
            System.exit(code);
        }
    }
}
