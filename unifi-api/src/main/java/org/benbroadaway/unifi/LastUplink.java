package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableLastUplink.class)
public interface LastUplink {
    @JsonProperty("uplink_mac")
    String uplinkMac();

    @JsonProperty("uplink_device_name")
    String uplinkDeviceName();

    @JsonProperty("type")
    String type();
}
