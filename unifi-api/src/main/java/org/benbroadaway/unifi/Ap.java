package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAp.class)
public interface Ap {
    @JsonProperty("site_id")
    String siteId();

    @JsonProperty("o")
    String o();

    @JsonProperty("oid")
    String oid();

    @JsonProperty("ap")
    String ap();

    @JsonProperty("time")
    long time();

    @JsonProperty("datetime")
    String datetime();

    @JsonProperty("user-rx_packets")
    double userRxPackets();

    @JsonProperty("guest-rx_packets")
    double guestRxPackets();

    @JsonProperty("rx_packets")
    double rxPackets();

    @JsonProperty("user-rx_bytes")
    double userRxBytes();

    @JsonProperty("guest-rx_bytes")
    double guestRxBytes();

    @JsonProperty("rx_bytes")
    double rxBytes();

    @JsonProperty("user-rx_errors")
    double userRxErrors();

    @JsonProperty("guest-rx_errors")
    double guestRxErrors();

    @JsonProperty("rx_errors")
    double rxErrors();

    @JsonProperty("user-rx_dropped")
    double userRxDropped();

    @JsonProperty("guest-rx_dropped")
    double guestRxDropped();

    @JsonProperty("rx_dropped")
    double rxDropped();

    @JsonProperty("user-rx_crypts")
    double userRxCrypts();

    @JsonProperty("guest-rx_crypts")
    double guestRxCrypts();

    @JsonProperty("rx_crypts")
    double rxCrypts();

    @JsonProperty("user-rx_frags")
    double userRxFrags();

    @JsonProperty("guest-rx_frags")
    double guestRxFrags();

    @JsonProperty("rx_frags")
    double rxFrags();

    @JsonProperty("user-tx_packets")
    double userTxPackets();

    @JsonProperty("guest-tx_packets")
    double guestTxPackets();

    @JsonProperty("tx_packets")
    double txPackets();

    @JsonProperty("user-tx_bytes")
    double userTxBytes();

    @JsonProperty("guest-tx_bytes")
    double guestTxBytes();

    @JsonProperty("tx_bytes")
    double txBytes();

    @JsonProperty("user-tx_errors")
    double userTxErrors();

    @JsonProperty("guest-tx_errors")
    double guestTxErrors();

    @JsonProperty("tx_errors")
    double txErrors();

    @JsonProperty("user-tx_dropped")
    double userTxDropped();

    @JsonProperty("guest-tx_dropped")
    double guestTxDropped();

    @JsonProperty("tx_dropped")
    double txDropped();

    @JsonProperty("user-tx_retries")
    double userTxRetries();

    @JsonProperty("guest-tx_retries")
    double guestTxRetries();

    @JsonProperty("tx_retries")
    double txRetries();

    @JsonProperty("user-mac_filter_rejections")
    double userMacFilterRejections();

    @JsonProperty("guest-mac_filter_rejections")
    double guestMacFilterRejections();

    @JsonProperty("mac_filter_rejections")
    double macFilterRejections();

    @JsonProperty("user-wifi_tx_attempts")
    double userWifiTxAttempts();

    @JsonProperty("guest-wifi_tx_attempts")
    double guestWifiTxAttempts();

    @JsonProperty("wifi_tx_attempts")
    double wifiTxAttempts();

    @JsonProperty("user-wifi_tx_dropped")
    double userWifiTxDropped();

    @JsonProperty("guest-wifi_tx_dropped")
    double guestWifiTxDropped();

    @JsonProperty("wifi_tx_dropped")
    double wifiTxDropped();

    @JsonProperty("bytes")
    double bytes();

    @JsonProperty("duration")
    double duration();
}
