package de.com.fdm.controller;

import de.com.fdm.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private static final String EVENTSUB_TWITCH_URL =
            "https://id.twitch.tv/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&force_verify=true&scope=";

    @Autowired
    private ConfigProperties config;

    @GetMapping("/auth/url")
    public String getAuthUrl() {
        String url = String.format(
                EVENTSUB_TWITCH_URL,
                config.getClientId(),
                this.config.getUrl() + "auth/redirect");

        url = url.concat("channel:read:subscriptions");

        return url;
    }

    @GetMapping("/auth/redirect")
    public String getRedirect() {
        return "Thanks!";
    }
}
