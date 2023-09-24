package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUsp.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName("UP1")
public interface Usp extends UnifiDevice {
    @JsonProperty("port_stats")
    List<Object> portStats();

    @JsonProperty("uplink_bssid")
    String uplinkBssid();

    @JsonProperty("element_ap_serial")
    String elementApSerial();

    @JsonProperty("outlet_overrides")
    List<Outlet> outletOverrides();

    @JsonProperty("outlet_table")
    List<Outlet> outletTable();

    @JsonProperty("outlet_enabled")
    boolean outletEnabled();

    static ImmutableUsp.Builder builder() {
        return ImmutableUsp.builder();
    }

    static ImmutableUsp.Builder copyWithoutId(Usp source) {
        return builder().from(source).deviceId(Optional.empty());
    }
}
