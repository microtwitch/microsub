package de.com.fdm.controller;

import com.google.gson.Gson;
import de.com.fdm.auth.AuthProvider;
import de.com.fdm.grpc.MicrosubClientManager;
import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import de.com.fdm.mongo.Consumer;
import de.com.fdm.mongo.ConsumerRepository;
import de.com.fdm.mongo.EventsubRepository;
import de.com.fdm.twitch.EventsubEntity;
import de.com.fdm.twitch.EventsubEvent;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class Controller {

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private MicrosubClientManager clientManager;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private EventsubRepository eventsubRepository;

    @GetMapping("/auth/url")
    public String getAuthUrl() {
        return this.authProvider.getAuthUrl();
    }

    @PostMapping("/follow")
    public String followEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        Gson gson = new Gson();
        EventsubEvent eventsubEvent = gson.fromJson(body, EventsubEvent.class);

        if (eventsubEvent.getChallenge() != null) {
            return eventsubEvent.getChallenge();
        }

        if (!verifySignature(body, headers, eventsubEvent)) {
            return "";
        }

        EventsubMessage msg = EventsubMessage
                .newBuilder()
                .setBroadcasterUserId(eventsubEvent.getEvent().getBroadcaster_user_id())
                .setBroadcasterUserName(eventsubEvent.getEvent().getBroadcaster_user_name())
                .setEventType(eventsubEvent.getSubscription().getType())
                .setEventUserId(eventsubEvent.getEvent().getUser_id())
                .setEventUserName(eventsubEvent.getEvent().getUser_name())
                .build();

        for (Consumer consumer : this.consumerRepository.findAll()) {
            if (Objects.equals(consumer.getBroadcasterUserId(), eventsubEvent.getEvent().getBroadcaster_user_id())) {
                this.clientManager.sendMessage(msg, consumer.getCallback());
            }
        }
        return "";
    }

    private boolean verifySignature(String request, Map<String, String> headers, EventsubEvent eventsubEvent) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, this.findSecret(eventsubEvent)).hmacHex(hmacMessage);
        hmac = "sha256=" + hmac;

        return hmac.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }

    private String findSecret(EventsubEvent eventsubEvent) {
        List<EventsubEntity> eventsubEntityList = this.eventsubRepository.findAll();

        for (EventsubEntity eventsub : eventsubEntityList) {
            if (eventsub.getData().get(0).getId().equals(eventsubEvent.getSubscription().getId())) {
                return eventsub.getSecret();
            }
        }
        // if databse entry gets deleted and event arrives afterwards
        // throws exception if it's empty
        return "racecondition xd";
    }
}
