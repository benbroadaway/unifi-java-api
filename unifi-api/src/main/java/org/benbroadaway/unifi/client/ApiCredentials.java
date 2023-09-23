package org.benbroadaway.unifi.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Arrays;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableApiCredentials.class)
@JsonDeserialize(as = ImmutableApiCredentials.class)
public interface ApiCredentials {
    String username();
    char[] password();

    static ApiCredentials from(char[] raw) {
        int separatorIndex = findSeparator(raw);
        String username = new String(Arrays.copyOfRange(raw, 0, separatorIndex));
        char[] password = Arrays.copyOfRange(raw, separatorIndex + 1, raw.length);

        return getInstance(username, password);
    }

    static ApiCredentials getInstance(String username, char[] password) {
        return ImmutableApiCredentials.builder()
                .username(username)
                .password(password)
                .build();
    }

    static int findSeparator(char[] chars) {
        for (int i=0; i<chars.length; i++) {
            if (chars[i] == ':') {
                return i;
            }
        }

        throw new IllegalArgumentException("Cannot find separator ':' in credentials");
    }
}
