package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableOutletTable.class)
public interface OutletTable {
    @JsonProperty("index")
    int getIndex();

    @JsonProperty("has_relay")
    boolean getHasRelay();

    @JsonProperty("has_metering")
    boolean getHasMetering();

    @JsonProperty("relay_state")
    boolean getRelayState();

    @JsonProperty("cycle_enabled")
    boolean getCycleEnabled();

    @JsonProperty("name")
    String getName();
}
