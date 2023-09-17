package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class AppTest extends AbstractTest {

    @Test
    void testVersion() {
        var expectedVersion = VersionInfo.getVersion();

        int exitCode = run(List.of("--version"), Map.of());

        assertExitCode(0, exitCode);
        assertLog(".*" +  expectedVersion + ".*");
    }

    @Test
    void testUsage() {
        int exitCode = run(List.of(), Map.of());

        assertExitCode(0, exitCode);
        assertLog(".*Usage: unifi .*");
    }

    @Override
    protected int run(List<String> args, Map<Class<?>, Object> mockedClasses) {
        return super.run(args, mockedClasses);
    }
}
