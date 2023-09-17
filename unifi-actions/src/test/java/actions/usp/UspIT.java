package actions.usp;

import org.benbroadaway.unifi.actions.usp.SetRelayState;
import org.benbroadaway.unifi.client.ImmutableApiCredentials;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@Disabled
class UspIT {

    @Test
    void testSetState() {

        // --- prepare

        var creds = ImmutableApiCredentials.builder()
                .username(assertEnv("TEST_UNIFI_USERNAME"))
                .password(assertEnv("TEST_UNIFI_PASSWORD").toCharArray())
                .build();

        var setState = SetRelayState.getInstance(assertEnv("TEST_UNIFI_HOST"), assertEnv("TEST_UNIFI_DEVICE"), creds, true, true);

        // --- execute

        var result = setState.call();

        // --- validate

        Assertions.assertTrue(result.ok());
    }

    private static String assertEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
                .orElseThrow(() -> new IllegalStateException("Required test env var '${name}' not found or null"));
    }
}
