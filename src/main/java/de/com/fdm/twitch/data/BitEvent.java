package de.com.fdm.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BitEvent(
    @JsonProperty("challenge")
    String challenge,
    @JsonProperty("event")
    BitEvent.Event event
) {
        public record Event(
                @JsonProperty("is_anonymous")
                boolean isAnon,
                @JsonProperty("user_name")
                String userName,
                @JsonProperty("bits")
                int bits
        ) {}
}
