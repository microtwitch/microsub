package de.com.fdm.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventsubRegistration(
        String type,
        String version,
        Condition condition,
        Transport transport
) {
    public EventsubRegistration(String type, String broadcasterId, String callback, String secret) {
        this(type, "1", new Condition(broadcasterId), new Transport(callback, secret));
    }

    public record Condition(
            @JsonProperty("broadcaster_user_id")
            String broadcasterId
    ) {}

    public record Transport(
            String method,
            String callback,
            String secret
    ) {
        public Transport(String callback, String secret) {
            this("webhook", callback, secret);
        }

    }
}
