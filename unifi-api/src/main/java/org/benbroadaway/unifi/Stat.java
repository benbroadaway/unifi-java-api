package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableStat.class)
public interface Stat {
    @JsonProperty("ap")

    Map<String, Object> ap();

    @JsonProperty("user")
    Optional<User> user();
}
