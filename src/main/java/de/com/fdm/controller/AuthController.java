package de.com.fdm.controller;

import de.com.fdm.config.EventsubProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class AuthController {
    private static final String EVENTSUB_TWITCH_URL =
            "https://id.twitch.tv/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&force_verify=true&scope=channel:read:subscriptions bits:read";

    @Autowired
    private EventsubProps eventsubProps;

    @GetMapping("/auth")
    public ResponseEntity<Void> getAuthUrl() {
        String url = String.format(
                EVENTSUB_TWITCH_URL,
                eventsubProps.clientId(),
                eventsubProps.url() + "auth/redirect");

        url = url.replace(" ", "%20");

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("/auth/redirect")
    public String getRedirect() {
        return "Thanks!";
    }
}
