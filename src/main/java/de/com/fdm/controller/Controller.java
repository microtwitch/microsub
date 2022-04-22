package de.com.fdm.controller;

import com.google.gson.Gson;
import de.com.fdm.auth.AuthProvider;
import de.com.fdm.eventsub.EventsubConsumer;
import de.com.fdm.eventsub.EventsubService;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
public class Controller {
    Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private EventsubConsumer eventsubConsumer;

    @Autowired
    private EventsubService eventsubService;

    @GetMapping("/auth/url")
    public String getAuthUrl() {
        return this.authProvider.getAuthUrl();
    }

    @PostMapping("/follow")
    public String followEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        Gson gson = new Gson();
        FollowEvent followEvent = gson.fromJson(body, FollowEvent.class);

        if (followEvent.getChallenge() != null) {
            return followEvent.getChallenge();
        }

        System.out.println("test1");

        if (!verifySignature(body, headers, followEvent.getSubscription().getId())) {
            return "";
        }

        System.out.println("test2");

        logger.info("Eventsub payload received: {}", body);

        eventsubConsumer.consume(followEvent);

        return "";
    }

    @PostMapping("/sub")
    public String subEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        Gson gson = new Gson();
        SubEvent subEvent = gson.fromJson(body, SubEvent.class);

        if (subEvent.getChallenge() != null) {
            return subEvent.getChallenge();
        }

        if (!verifySignature(body, headers, subEvent.getSubscription().getId())) {
            return "";
        }

        logger.info("Eventsub payload received: {}", body);

        eventsubConsumer.consume(subEvent);

        return "";
    }

    private boolean verifySignature(String request, Map<String, String> headers, String id) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String secret = this.eventsubService.getSecret(id);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
        digest.update(secret.getBytes());
        String hmac = "sha256=" + digest.digest(hmacMessage.getBytes()).toString();
        return hmac.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }
}
