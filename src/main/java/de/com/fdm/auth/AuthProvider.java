package de.com.fdm.auth;

import de.com.fdm.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthProvider {
    private final RestTemplate restTemplate;

    @Autowired
    private ConfigProperties config;

    public AuthProvider() {
        this.restTemplate = new RestTemplate();
    }

    public String getAuthUrl() {
        AppDetails appDetails = new AppDetails();
        appDetails.setClientId(config.getClientId());
        appDetails.setScopes(new String[] {"channel:read:subscriptions"});

        String url = this.config.getAuthUrl() + "url";

        return this.restTemplate.postForObject(url, appDetails, String.class);
    }
}
