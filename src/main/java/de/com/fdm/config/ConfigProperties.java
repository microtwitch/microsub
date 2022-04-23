package de.com.fdm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigProperties {
    @Value("${eventsub.clientId}")
    private String clientId;

    @Value("${eventsub.secret}")
    private String secret;

    @Value("${eventsub.clientSecret}")
    private String clientSecret;

    @Value("${eventsub.url}")
    private String url;

    @Value("${turtoise.auth}")
    private String turtoiseAuth;

    public String getTurtoiseAuth() {
        return turtoiseAuth;
    }

    public void setTurtoiseAuth(String turtoiseAuth) {
        this.turtoiseAuth = turtoiseAuth;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
