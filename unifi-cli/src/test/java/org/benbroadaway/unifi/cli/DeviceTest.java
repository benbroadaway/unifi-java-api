package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.UnifiDevice;
import org.benbroadaway.unifi.UnknownDevice;
import org.benbroadaway.unifi.Usp;
import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.actions.device.GetDevices;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeviceTest extends AbstractTest {

    @Test
    void testHelp() {
        var exitCode = run(List.of("--help"), Map.of());
        assertExitCode(0, exitCode);
        assertLog(".*Interact with Unifi devices.*");
    }

    @Test
    void testNoSubCommand() {
        var exitCode = run(List.of(), Map.of());
        assertExitCode(1, exitCode);
        assertLogErr(".*sub-command is missing.*");
    }

    @Test
    void testGetDevices() {
        var exitCode = run(List.of(
                "get",
                "--username=test",
                "--password=test",
                "-c", "http://localhost"), Map.of(Device.class, new MockDevice("test", "test")));

        assertExitCode(0, exitCode);
        assertLog(".*Model\\s+Device ID\\s+Name\\s+IP Addr.*");
        assertLog(".*UP1\\s+device_0\\s+test-usp-1.*");
        assertLog(".*unknown\\s+device_1\\s+test-unknown-device-1.*");
    }

    @Override
    protected int run(List<String> args, Map<Class<?>, Object> mockedClasses) {
        List<String> effectiveArgs = new ArrayList<>(args.size() + 1);
        effectiveArgs.add("device");
        effectiveArgs.addAll(args);

        return super.run(effectiveArgs, mockedClasses);
    }

    private static class MockDevice extends Device {
        private final String expectedUsername;
        private final String expectedPassword;
        private final boolean throwError;

        public MockDevice() {
            this("test", "test", false);
        }

        public MockDevice(boolean throwError) {
            this("test", "test", throwError);
        }

        public MockDevice(String expectedUsername, String expectedPassword) {
            this(expectedUsername, expectedPassword, false);
        }

        public MockDevice(String expectedUsername, String expectedPassword, boolean throwError) {
            this.expectedUsername = expectedUsername;
            this.expectedPassword = expectedPassword;
            this.throwError = throwError;
        }

        private void assertCommonParams(ApiCredentials credentials) {
            assertEquals("http://localhost", unifiHost);
            assertEquals(expectedUsername, credentials.username());
            assertArrayEquals(expectedPassword.toCharArray(), credentials.password());
        }

        @Override
        GetDevices actionForGet(ApiCredentials credentials) {
            assertCommonParams(credentials);

            var devices = new LinkedList<UnifiDevice>();
            Usp baseUsp = resourceToObject("device_usp.json", Util.getMapper().constructType(Usp.class));

            devices.add(Usp.builder()
                    .from(baseUsp)
                    .deviceId("device_0")
                    .name("test-usp-1")
                    .build());
            devices.add(UnknownDevice.copyOf(baseUsp)
                    .deviceId("device_1")
                    .name("test-unknown-device-1")
                    .model("unknown")
                    .build());

            var getDevices = mock(GetDevices.class);
            var result = ActionResult.<List<UnifiDevice>>builder()
                    .ok(true)
                    .data(devices)
                    .build();

            when(getDevices.call())
                    .thenReturn(result);
            return getDevices;
        }
    }
}
