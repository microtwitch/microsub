package de.com.fdm.controller;

import de.com.fdm.eventsub.EventsubConsumer;
import de.com.fdm.twitch.data.BitEvent;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockConsumer implements EventsubConsumer {
    Logger logger = LoggerFactory.getLogger(MockConsumer.class);

    @Override
    public void consume(FollowEvent followEvent) {
        logger.info(followEvent.toString());

    }

    @Override
    public void consume(SubEvent subEvent) {
        logger.info(subEvent.toString());
    }

    @Override
    public void consume(BitEvent bitEvent) {

    }
}
