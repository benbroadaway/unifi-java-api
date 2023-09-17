package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDevice.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Device {
    @JsonProperty("device_id")
    Optional<String> deviceId();
    @JsonProperty("model")
    String model();
    @JsonProperty("name")
    String name();
    @JsonProperty("outlet_overrides")
    List<OutletOverride> outletOverrides();
    @JsonProperty("led_override")
    Optional<String> ledOverride();
    @JsonProperty("led_override_color_brightness")
    Optional<Integer> ledOverrideColorBrightness();
    @JsonProperty("led_override_color")
    Optional<String> ledOverrideColor();
    @JsonProperty("config_network")
    ConfigNetwork configNetwork();
    @JsonProperty("mgmt_network_id")
    Optional<String> mgmtNetworkId();

    static ImmutableDevice.Builder builder() {
        return ImmutableDevice.builder();
    }
}
