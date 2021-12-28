package de.com.fdm.controller;

import de.com.fdm.auth.AuthProvider;
import de.com.fdm.grpc.MicrosubClientManager;
import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import de.com.fdm.mongo.Consumer;
import de.com.fdm.mongo.ConsumerRepository;
import de.com.fdm.twitch.EventsubEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class Controller {

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private MicrosubClientManager clientManager;

    @Autowired
    private ConsumerRepository consumerRepository;

    @GetMapping("/auth/url")
    public String getAuthUrl() {
        return this.authProvider.getAuthUrl();
    }

    @PostMapping("/follow")
    public String followEvents(@RequestBody EventsubEvent eventsubEvent) {
        if (eventsubEvent.getChallenge() != null) {
            return eventsubEvent.getChallenge();
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
}
