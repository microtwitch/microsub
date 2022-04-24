package de.com.fdm.eventsub;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class EventsubConsumer {
    private final TwitchClient client;

    private final List<String> pastFollows;

    public EventsubConsumer(@Autowired ConfigProperties config) {
        this.pastFollows = new ArrayList<>(10);

        OAuth2Credential credentials = new OAuth2Credential("twitch", config.getTurtoiseAuth());
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credentials)
                .build();
    }

    public void consume(FollowEvent followEvent) {
        if (pastFollows.contains(followEvent.getEvent().getUser_id())) {
            return;
        }

        if (pastFollows.size() == 10) {
            pastFollows.remove(0);
        }

        pastFollows.add(followEvent.getEvent().getUser_id());

        String msg = "PagChomp OH SHIT %s THANKS FOR THE FOLLOW!";
        msg = String.format(msg, followEvent.getEvent().getUser_name());
        client.getChat().sendMessage("turtoise", msg);
    }

    public void consume(SubEvent subEvent) {
        String msg = "heCrazy YOOO %s THANKS FOR SUBBING!";
        if (subEvent.getEvent().isGift()) {
            msg = "heCrazy YOOO %s YOU JUST GOT GIFTED!";
        }

        msg = String.format(msg, subEvent.getEvent().getUser_name());
        client.getChat().sendMessage("turtoise", msg);
    }
}
