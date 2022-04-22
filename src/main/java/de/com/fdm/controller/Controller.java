package de.com.fdm.controller;

import com.google.gson.Gson;
import de.com.fdm.auth.AuthProvider;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.eventsub.EventsubConsumer;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private EventsubConsumer eventsubConsumer;

    @Autowired
    private ConfigProperties config;

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

        if (!verifySignature(body, headers)) {
            return "";
        }

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

        if (!verifySignature(body, headers)) {
            return "";
        }

        logger.info("Eventsub payload received: {}", body);

        eventsubConsumer.consume(subEvent);

        return "";
    }

    private boolean verifySignature(String request, Map<String, String> headers) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String secret = config.getSecret();
        String hash = "sha256=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(hmacMessage);
        return hash.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }
}
