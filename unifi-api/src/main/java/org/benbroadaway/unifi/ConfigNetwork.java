package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableConfigNetwork.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ConfigNetwork {
    @JsonProperty("type")
    String type();

    @JsonProperty("bonding_enabled")
    Optional<Boolean> bondingEnabled();

    @JsonProperty("ip")
    Optional<String> ip();
}
