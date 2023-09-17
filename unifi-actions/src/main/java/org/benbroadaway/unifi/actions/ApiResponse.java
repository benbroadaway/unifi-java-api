package org.benbroadaway.unifi.actions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableApiResponse.class)
public interface ApiResponse<T> {
    T data();
    Map<String, Object> meta(); // TODO refine

    static <T> ImmutableApiResponse.Builder<T> builder() {
        return ImmutableApiResponse.builder();
    }
}
