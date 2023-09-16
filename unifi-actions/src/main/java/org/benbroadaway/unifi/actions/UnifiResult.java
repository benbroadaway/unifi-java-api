package org.benbroadaway.unifi.actions;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
public interface UnifiResult<T> {
    boolean ok();
    Optional<String> error();
    Optional<T> data();

    static <T> ImmutableUnifiResult.Builder<T> builder() {
        return ImmutableUnifiResult.builder();
    }

    static <T> UnifiResult<T> success() {
        return ImmutableUnifiResult.<T>builder().ok(true).build();
    }
}
