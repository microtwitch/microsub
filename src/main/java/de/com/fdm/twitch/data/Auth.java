package de.com.fdm.twitch.data;


import com.fasterxml.jackson.annotation.JsonProperty;

public record Auth(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        long expiresIn
) {}
