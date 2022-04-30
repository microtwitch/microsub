package de.com.fdm.eventsub;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurtoiseConsumer implements EventsubConsumer {
    Logger logger = LoggerFactory.getLogger(TurtoiseConsumer.class);

    private final TwitchClient client;

    private final List<String> pastFollows;

    public TurtoiseConsumer(@Value("${turtoise.auth}") String turtoiseAuth) {
        this.pastFollows = new ArrayList<>(10);

        OAuth2Credential credentials = new OAuth2Credential("twitch", turtoiseAuth);
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credentials)
                .build();
    }

    @Override
    public void consume(FollowEvent followEvent) {
        logger.info(followEvent.toString());
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

    @Override
    public void consume(SubEvent subEvent) {
        logger.info(subEvent.toString());
        String msg = getAlertMessage(subEvent);

        msg = String.format(msg, subEvent.getEvent().getUser_name());
        client.getChat().sendMessage("turtoise", msg);
    }

    private String getAlertMessage(SubEvent subEvent) {
        if (subEvent.getEvent().isGift()) {
            return "heCrazy YOOO %s YOU JUST GOT GIFTED!";
        }

        return "heCrazy YOOO %s THANKS FOR SUBBING!";
    }
}
