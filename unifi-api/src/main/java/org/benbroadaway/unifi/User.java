package org.benbroadaway.unifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
    @JsonProperty("site_id")
    String siteId();

    @JsonProperty("o")
    String o();

    @JsonProperty("oid")
    String oid();

    @JsonProperty("user")
    String user();

    @JsonProperty("time")
    long time();

    @JsonProperty("datetime")
    String datetime();

    @JsonProperty("duration")
    double duration();
}
