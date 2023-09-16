package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

class AppTest extends AbstractTest {

    @Test
    void testVersion() {
        var expectedVersion = VersionInfo.getVersion();

        int exitCode = run(List.of("--version"));

        assertExitCode(0, exitCode);
        assertLog(".*" +  expectedVersion + ".*");
    }

    @Test
    void testUsage() {
        int exitCode = run(List.of());

        assertExitCode(0, exitCode);
        assertLog(".*Usage: unifi .*");

    }

    private static int run(List<String> args) {
        var app = new App();
        var cmd = new CommandLine(app);

        var effectiveArgs = new ArrayList<>(args);

        return cmd.execute(effectiveArgs.toArray(new String[0]));
    }
}
