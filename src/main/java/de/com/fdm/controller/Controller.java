package de.com.fdm.controller;

import com.google.gson.Gson;
import de.com.fdm.auth.AuthProvider;
import de.com.fdm.grpc.MicrosubClientManager;
import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import de.com.fdm.db.data.Consumer;
import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.repositories.ConsumerRepository;
import de.com.fdm.db.services.EventsubService;
import de.com.fdm.twitch.EventsubEvent;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private MicrosubClientManager clientManager;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private EventsubService eventsubService;

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

        Eventsub eventsub = this.eventsubService.findByTwitchId(eventsubEvent.getSubscription().getId());

        for (Consumer consumer : eventsub.getConsumers()) {
            this.clientManager.sendMessage(msg, consumer.getCallback());
        }

        return "";
    }

    private boolean verifySignature(String request, Map<String, String> headers, EventsubEvent eventsubEvent) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String secret = this.eventsubService.findSecret(eventsubEvent.getSubscription().getId());
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(hmacMessage);
        hmac = "sha256=" + hmac;

        return hmac.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }
}
