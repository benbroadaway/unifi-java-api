package org.benbroadaway.unifi.actions;

import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
public interface UnifiResult {
    boolean ok();
    Optional<String> error();
    Optional<Map<String, Object>> data();

    static ImmutableUnifiResult.Builder builder() {
        return ImmutableUnifiResult.builder();
    }

    static UnifiResult success() {
        return builder().ok(true).build();
    }
}
