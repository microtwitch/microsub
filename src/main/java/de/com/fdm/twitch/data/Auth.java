package de.com.fdm.twitch.data;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {
    private final String accessToken;
    private final long expiresIn;

    public Auth(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            long expiresIn
    ) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
