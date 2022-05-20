package de.com.fdm.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubEvent (
    @JsonProperty("challenge")
    String challenge,
    @JsonProperty("event")
    SubEvent.Event event
) {
    public record Event(
            @JsonProperty("user_name")
            String userName,
            @JsonProperty("is_gift")
            boolean isGift
    ) {}
}
