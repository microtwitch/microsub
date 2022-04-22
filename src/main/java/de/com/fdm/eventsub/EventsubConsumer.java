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
    private TwitchClient client;

    private List<String> pastFollows;

    public EventsubConsumer(@Autowired ConfigProperties config) {
        this.pastFollows = new ArrayList<>();

        OAuth2Credential credentials = new OAuth2Credential("twitch", config.getTurtoiseAuth());
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credentials)
                .build();
    }

    public void consume(FollowEvent followEvent) {
        // TODO: ratelimiting

        String msg = "PagChomp OH SHIT %s THANKS FOR THE FOLLOW!";
        msg = String.format(msg, followEvent.getEvent().getUser_name());
        client.getChat().sendMessage("matthewde", msg);
    }

    public void consume(SubEvent subEvent) {
        String msg = "heCrazy YOOO %s THANKS FOR SUBBING!";
        msg = String.format(msg, subEvent.getEvent().getUser_name());
        client.getChat().sendMessage("matthewde", msg);
    }
}
