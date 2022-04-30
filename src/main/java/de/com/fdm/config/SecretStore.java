package de.com.fdm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretStore {
    private final String secret;

    public SecretStore(
            @Value("${eventsub.clientSecret}") String secret
    ) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}
