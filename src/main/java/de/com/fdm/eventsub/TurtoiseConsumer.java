package de.com.fdm.eventsub;

import de.com.fdm.twitch.TmiService;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurtoiseConsumer implements EventsubConsumer {
    Logger logger = LoggerFactory.getLogger(TurtoiseConsumer.class);

    private final List<String> pastFollows;
    private final TmiService tmiService;

    public TurtoiseConsumer(@Autowired TmiService tmiService) {
        this.pastFollows = new ArrayList<>(10);
        this.tmiService = tmiService;
    }

    @Override
    public void consume(FollowEvent followEvent) {
        logger.info("{}", followEvent);
        if (pastFollows.contains(followEvent.getEvent().getUser_id())) {
            return;
        }

        if (pastFollows.size() == 10) {
            pastFollows.remove(0);
        }

        pastFollows.add(followEvent.getEvent().getUser_id());

        String msg = "PagChomp OH SHIT %s THANKS FOR THE FOLLOW!";
        msg = String.format(msg, followEvent.getEvent().getUser_name());
        tmiService.send("turtoise", msg);
    }

    @Override
    public void consume(SubEvent subEvent) {
        logger.info("{}", subEvent);
        String msg = getAlertMessage(subEvent);

        msg = String.format(msg, subEvent.getEvent().getUser_name());
        tmiService.send("turtoise", msg);
    }

    private String getAlertMessage(SubEvent subEvent) {
        if (subEvent.getEvent().isGift()) {
            return "heCrazy YOOO %s YOU JUST GOT GIFTED!";
        }

        return "heCrazy YOOO %s THANKS FOR SUBBING!";
    }
}
