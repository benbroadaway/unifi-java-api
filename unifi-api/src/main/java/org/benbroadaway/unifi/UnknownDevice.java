package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUnknownDevice.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface UnknownDevice extends UnifiDevice {

    static ImmutableUnknownDevice.Builder copyOf(UnifiDevice device) {
        return ImmutableUnknownDevice.builder()
                .from(device);
    }
}
