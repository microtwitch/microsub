package de.com.fdm.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowEvent(
        @JsonProperty("challenge")
        String challenge,
        @JsonProperty("event")
        Event event
) {
    public record Event(
            @JsonProperty("user_id")
            String userId,
            @JsonProperty("user_name")
            String userName
    ) {}
}
