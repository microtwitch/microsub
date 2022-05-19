package de.com.fdm.eventsub;

import com.google.gson.Gson;
import de.com.fdm.twitch.data.SubEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestTurtoiseConsumer {
    @Test
    void testIsGiftParsing() {
        Gson gson = new Gson();
        SubEvent event = gson.fromJson("{\"subscription\":{\"id\":\"21ce7eb3-f516-4adf-b311-933c5cdcab0e\",\"status\":\"enabled\",\"type\":\"channel.subscribe\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"80805824\"},\"transport\":{\"method\":\"webhook\",\"callback\":\"https://microsub.fdm.com.de/sub\"},\"created_at\":\"2022-05-05T14:42:14.143010388Z\",\"cost\":0},\"event\":{\"user_id\":\"121925792\",\"user_login\":\"drhorn\",\"user_name\":\"drhorn\",\"broadcaster_user_id\":\"80805824\",\"broadcaster_user_login\":\"turtoise\",\"broadcaster_user_name\":\"Turtoise\",\"tier\":\"1000\",\"is_gift\":true}}", SubEvent.class);
        Assertions.assertTrue(event.getEvent().isGift());

        SubEvent event2 = gson.fromJson("{\"subscription\":{\"id\":\"21ce7eb3-f516-4adf-b311-933c5cdcab0e\",\"status\":\"enabled\",\"type\":\"channel.subscribe\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"80805824\"},\"transport\":{\"method\":\"webhook\",\"callback\":\"https://microsub.fdm.com.de/sub\"},\"created_at\":\"2022-05-05T14:42:14.143010388Z\",\"cost\":0},\"event\":{\"user_id\":\"121925792\",\"user_login\":\"drhorn\",\"user_name\":\"drhorn\",\"broadcaster_user_id\":\"80805824\",\"broadcaster_user_login\":\"turtoise\",\"broadcaster_user_name\":\"Turtoise\",\"tier\":\"1000\",\"is_gift\":false}}", SubEvent.class);
        Assertions.assertFalse(event2.getEvent().isGift());

    }
}
