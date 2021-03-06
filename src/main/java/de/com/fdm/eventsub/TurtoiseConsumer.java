package de.com.fdm.eventsub;

import de.com.fdm.twitch.TmiService;
import de.com.fdm.twitch.data.BitEvent;
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
    private static final String CHANNEL = "turtoise";

    private final List<String> pastFollows;
    private final TmiService tmiService;

    @Autowired
    public TurtoiseConsumer(TmiService tmiService) {
        this.pastFollows = new ArrayList<>(10);
        this.tmiService = tmiService;
    }

    @Override
    public void consume(FollowEvent followEvent) {
        logger.info("{}", followEvent);
        if (pastFollows.contains(followEvent.event().userId())) {
            return;
        }

        if (pastFollows.size() == 10) {
            pastFollows.remove(0);
        }

        pastFollows.add(followEvent.event().userId());

        String msg = "PagChomp OH SHIT %s THANKS FOR THE FOLLOW!";
        msg = String.format(msg, followEvent.event().userName());
        tmiService.send(CHANNEL, msg);
    }

    @Override
    public void consume(SubEvent subEvent) {
        logger.info("{}", subEvent);
        String msg = getSubAlertMessage(subEvent);

        msg = String.format(msg, subEvent.event().userName());
        tmiService.send(CHANNEL, msg);
    }

    @Override
    public void consume(BitEvent bitEvent) {
        logger.info("{}", bitEvent);

        if (bitEvent.event().isAnon()) {
            String msg = "PagMan YOOO ANON THANKS FOR %s BITTIES";
            msg = String.format(msg, bitEvent.event().bits());
            tmiService.send(CHANNEL, msg);
            return;
        }

        String msg = "PagMan YOOO %s THANKS FOR %s BITTIES";
        msg = String.format(msg, bitEvent.event().userName(), bitEvent.event().bits());
        tmiService.send(CHANNEL, msg);
    }

    private String getSubAlertMessage(SubEvent subEvent) {
        if (subEvent.event().isGift()) {
            return "heCrazy YOOO %s YOU JUST GOT GIFTED!";
        }
        return "heCrazy YOOO %s THANKS FOR SUBBING!";
    }
}
