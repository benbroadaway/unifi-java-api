package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
              property = "model",
              visible = true,
              defaultImpl = UnknownDevice.class)
@JsonSubTypes({@JsonSubTypes.Type(Usp.class)})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface UnifiDevice {
    @JsonProperty("device_id")
    Optional<String> deviceId();

    @JsonProperty("model")
    String model();

    @JsonProperty("name")
    String name();
}

/*
known fields

required_version
hw_caps
state
device_id
guest-wlan-num_sta
version
unsupported_reason
anon_id
connect_request_port
site_id
provisioned_at
inform_url
ethernet_table
user-num_sta
last_uplink
disconnected_at
x_aes_gcm
has_fan
stat
has_eth1
has_temperature
_uptime
connection_network_name
adopted
last_seen
connect_request_ip
guest-num_sta
sys_stats
upgradable
syslog_key
manufacturer_id
startup_timestamp
wifi_caps
name
locating
unsupported
rx_bytes
x_has_ssh_hostkey
x_authkey
lldp_table
model_in_lts
has_speaker
type
config_network
uplink
model
safe_for_autoupgrade
num_sta
ip
adoption_completed
rollupgrade
connected_at
mac
start_disconnected_millis
tx_bytes
model_incompatible
downlink_table
start_connected_millis
vap_table
port_table
prev_non_busy_state
board_rev
setup_id
reboot_duration
adoptable_when_upgraded
system-stats
is_access_point
fw2_caps
last_connection_network_name
user-wlan-num_sta
fw_caps
_id
two_phase_adopt
inform_ip
cfgversion
known_cfgversion
upgrade_duration
next_interval
dot1x_portctrl_enabled
displayable_version
model_in_eol
switch_caps
uptime
serial
bytes
 */
