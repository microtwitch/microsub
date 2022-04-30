package de.com.fdm.controller;

import de.com.fdm.eventsub.EventsubConsumer;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;

public class MockConsumer implements EventsubConsumer {
    @Override
    public void consume(FollowEvent followEvent) {

    }

    @Override
    public void consume(SubEvent subEvent) {

    }
}
