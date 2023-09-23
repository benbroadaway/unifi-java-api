package org.benbroadaway.unifi.actions;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
public interface ActionResult<T> {
    boolean ok();

    Optional<String> error();

    Optional<T> data();

    static <T> ImmutableActionResult.Builder<T> builder() {
        return ImmutableActionResult.builder();
    }

    static <T> ActionResult<T> success() {
        return ImmutableActionResult.<T>builder().ok(true).build();
    }
}
