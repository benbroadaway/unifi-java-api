package org.benbroadaway.unifi.cli;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DeviceTest extends AbstractTest {

    @Test
    void testHelp() {
        var exitCode = run(List.of("--help"), Map.of());
        assertExitCode(0, exitCode);
        assertLog(".*Interact with Unifi devices.*");
    }

    @Test
    void testNoSubCommand() {
        var exitCode = run(List.of("-d", "my-device"), Map.of());
        assertExitCode(1, exitCode);
        assertLogErr(".*sub-command is missing.*");
    }

    @Override
    protected int run(List<String> args, Map<Class<?>, Object> mockedClasses) {
        List<String> effectiveArgs = new ArrayList<>(args.size() + 1);
        effectiveArgs.add("device");
        effectiveArgs.addAll(args);

        return super.run(effectiveArgs, mockedClasses);
    }
}
