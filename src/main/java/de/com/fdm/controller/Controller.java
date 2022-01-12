package de.com.fdm.controller;

import com.google.gson.Gson;
import de.com.fdm.auth.AuthProvider;
import de.com.fdm.grpc.MicrosubClientManager;
import de.com.fdm.db.data.Consumer;
import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.repositories.ConsumerRepository;
import de.com.fdm.db.services.EventsubService;
import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import de.com.fdm.grpc.microsub.lib.Type;
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
    private MicrosubClientManager clientManager;

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


        if (!verifySignature(body, headers, followEvent.getSubscription().getId())) {
            return "";
        }

        logger.info("Eventsub payload received: {}", body);

        EventsubMessage msg = EventsubMessage
                .newBuilder()
                .setBroadcasterUserId(followEvent.getEvent().getBroadcaster_user_id())
                .setBroadcasterUserName(followEvent.getEvent().getBroadcaster_user_name())
                .setEventUserId(followEvent.getEvent().getUser_id())
                .setEventType(Type.FOLLOW)
                .setEventUserName(followEvent.getEvent().getUser_name())
                .build();

        Eventsub eventsub = this.eventsubService.findByTwitchId(followEvent.getSubscription().getId());

        for (Consumer consumer : eventsub.getConsumers()) {
            this.clientManager.sendMessage(msg, consumer.getCallback());
        }

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

        EventsubMessage msg = EventsubMessage
                .newBuilder()
                .setBroadcasterUserId(subEvent.getEvent().getBroadcaster_user_id())
                .setBroadcasterUserName(subEvent.getEvent().getBroadcaster_user_name())
                .setEventUserId(subEvent.getEvent().getUser_id())
                .setEventType(Type.SUB)
                .setEventUserName(subEvent.getEvent().getUser_name())
                .setIsGift(subEvent.getEvent().isGift())
                .build();

        Eventsub eventsub = this.eventsubService.findByTwitchId(subEvent.getSubscription().getId());

        for (Consumer consumer : eventsub.getConsumers()) {
            this.clientManager.sendMessage(msg, consumer.getCallback());
        }

        return "";
    }

    private boolean verifySignature(String request, Map<String, String> headers, String id) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String secret = this.eventsubService.findSecret(id);
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(hmacMessage);
        hmac = "sha256=" + hmac;

        return hmac.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }
}
