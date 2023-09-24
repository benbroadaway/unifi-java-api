package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableOutlet.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Outlet {
    @JsonProperty("index")
    int index();

    @JsonProperty("has_relay")
    Optional<Boolean> hasRelay();

    @JsonProperty("relay_state")
    boolean relayState();

    @JsonProperty("cycle_enabled")
    boolean cycleEnabled();

    @JsonProperty("has_metering")
    Optional<Boolean> hasMetering();

    @JsonProperty("name")
    String name();

    static ImmutableOutlet.Builder builder() {
        return ImmutableOutlet.builder();
    }
}
