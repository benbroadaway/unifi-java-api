package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUplink.class)
public interface Uplink {
    @JsonProperty("uplink_mac")
    String uplinkMac();

    @JsonProperty("uplink_device_name")
    String uplinkDeviceName();

    @JsonProperty("type")
    String type();

    @JsonProperty("tx_bytes")
    long txBytes();

    @JsonProperty("rx_bytes")
    long rxBytes();

    @JsonProperty("tx_packets")
    long txPackets();

    @JsonProperty("rx_packets")
    long rxPackets();

    @JsonProperty("tx_bytes-r")
    long txBytesR();

    @JsonProperty("rx_bytes-r")
    long rxBytesR();

    @JsonProperty("tx_rate")
    long txRate();

    @JsonProperty("rx_rate")
    long rxRate();

    @JsonProperty("rssi")
    int rssi();

    @JsonProperty("is_11ax")
    boolean is11ax();

    @JsonProperty("is_11ac")
    boolean is11ac();

    @JsonProperty("is_11n")
    boolean is11n();

    @JsonProperty("is_11b")
    boolean is11b();

    @JsonProperty("radio")
    String radio();

    @JsonProperty("essid")
    String essid();

    @JsonProperty("channel")
    int channel();

    @JsonProperty("uplink_source")
    String uplinkSource();

    @JsonProperty("up")
    boolean up();

    @JsonProperty("ap_mac")
    String apMac();
}
