package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSysStats.class)
public interface SysStats {
    @JsonProperty("mem_total")
    long memTotal();

    @JsonProperty("mem_used")
    long memUsed();
}
