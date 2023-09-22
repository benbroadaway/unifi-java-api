package org.benbroadaway.unifi.cli;

import org.benbroadaway.unifi.actions.ActionResult;
import org.benbroadaway.unifi.actions.usp.GetRelayState;
import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UspSateTest extends AbstractTest {

    @Test
    void testHelp() {
        var exitCode = run(List.of("--help"), Map.of());
        assertExitCode(0, exitCode);
        assertLog(".*Perform state actions on USP device.*");
    }

    @Test
    void testNoDevice() {
        var exitCode = run(List.of(), Map.of());
        assertExitCode(2, exitCode);
        assertLogErr(".*Missing required option: '--device-name=<deviceName>'.*");
    }

    @Test
    void testNoSubCommand() {
        var exitCode = run(List.of("-d", "my-device"), Map.of());
        assertExitCode(1, exitCode);
        assertLogErr(".*sub-command is missing.*");
    }

    @Test
    void testGet() {
        var exitCode = run(List.of("get",
                "--username=test",
                "--password=test",
                "-c", "http://localhost",
                "-d", "my-usp-device"), Map.of(UspState.class, new MockUspState("test", "test")));

        assertExitCode(0, exitCode);
    }

    @Test
    void testSet() {
        var exitCode = run(List.of(
                "set",
                "--username=test",
                "--password=test",
                "-c", "http://localhost",
                "-d", "my-usp-device",
                "--relay-state", "true"), Map.of(UspState.class, new MockUspState("test", "test")));

        assertExitCode(0, exitCode);
    }

    @Test
    void testSetError() {
        var exitCode = run(List.of(
                "-vvv",
                "set",
                "--username=test",
                "--password=test",
                "-c", "http://localhost",
                "-d", "my-usp-device",
                "--relay-state", "true"), Map.of(UspState.class, new MockUspState(true)));

        assertExitCode(1, exitCode);
        assertLogErr(".*Call error: forced exception.*");
        assertLogErr(".*\\[main\\] \\[TRACE\\].*");
        assertLogErr(".*java.lang.IllegalStateException: forced exception.*");
    }

    @Test
    void testNoAuth() {
        var exitCode = run(List.of(
                "get",
                "-c", "http://localhost",
                "-d", "my-usp-device"), Map.of(UspState.class, new MockUspState()));

        assertExitCode(1, exitCode);
        assertLogErr(".*Credentials must be supplied via cli, env, or file. See help for info.*");
    }

    @Test
    void testFileAuth() {
        write(tempDir.resolve("home/.unifi/auth/default.json"),
                "{\"username\": \"fileUser\", \"password\": \"filePass\"}");
        System.setProperty("user.home", tempDir.resolve("home").toAbsolutePath().toString());

        var exitCode = run(List.of("get",
                "-c", "http://localhost",
                "-d", "my-usp-device"), Map.of(UspState.class, new MockUspState("fileUser", "filePass")));

        assertExitCode(0, exitCode);
    }

    private Path write(Path dest, String contents) {
        try {
            Files.createDirectories(dest.getParent());
            return Files.writeString(dest, contents);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write file: " + e.getMessage(), e);
        }
    }

    static class MockUspState extends UspState {

        private final String expectedUsername;
        private final String expectedPassword;
        private final boolean throwError;

        public MockUspState() {
            this("test", "test", false);
        }

        public MockUspState(boolean throwError) {
            this("test", "test", throwError);
        }

        public MockUspState(String expectedUsername, String expectedPassword) {
            this(expectedUsername, expectedPassword, false);
        }

        public MockUspState(String expectedUsername, String expectedPassword, boolean throwError) {
            this.expectedUsername = expectedUsername;
            this.expectedPassword = expectedPassword;
            this.throwError = throwError;
        }

        private void assertCommonParams(String deviceName, ApiCredentials credentials, Device parent) {
            assertEquals("my-usp-device", deviceName);
            assertEquals("http://localhost", parent.unifiHost);
            assertEquals(expectedUsername, credentials.username());
            assertArrayEquals(expectedPassword.toCharArray(), credentials.password());
        }

        @Override
        GetRelayState actionForGet(ApiCredentials credentials) {
            assertCommonParams(device.deviceName, credentials, device);

            var relayState = mock(GetRelayState.class);
            when(relayState.call())
                    .thenReturn(ActionResult.<Boolean>builder()
                            .ok(true)
                            .data(true)
                            .build());
            return relayState;
        }

        @Override
        SetRelayState actionForSet(ApiCredentials credentials, boolean relayState) {
            assertCommonParams(device.deviceName, credentials, device);
            assertTrue(relayState);


            var toggle = mock(SetRelayState.class);

            if (throwError) {
                when(toggle.call())
                        .thenThrow(new IllegalStateException("forced exception"));
            } else {
                when(toggle.call())
                        .thenReturn(ActionResult.success());
            }

            return toggle;
        }
    }

    @Override
    protected int run(List<String> args, Map<Class<?>, Object> mockedClasses) {
        List<String> effectiveArgs = new ArrayList<>(args.size() + 2);
        effectiveArgs.add("device");
        effectiveArgs.add("usp-state");
        effectiveArgs.addAll(args);

        return super.run(effectiveArgs, mockedClasses);
    }
}
