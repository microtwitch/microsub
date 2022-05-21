package de.com.fdm.eventsub;

import de.com.fdm.twitch.data.BitEvent;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;

public interface EventsubConsumer {
    void consume(FollowEvent followEvent);
    void consume(SubEvent subEvent);
    void consume(BitEvent bitEvent);
}
