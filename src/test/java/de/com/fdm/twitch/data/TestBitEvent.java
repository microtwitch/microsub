package de.com.fdm.twitch.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestBitEvent {
    @Test
    void testParsing() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        BitEvent bitEvent = mapper.readValue(getBitsBody(), BitEvent.class);
        Assertions.assertEquals(1, bitEvent.event().bits());
    }

    private String getBitsBody() {
        return """
                {
                   "subscription":{
                      "id":"93d8abb2-9251-489e-aed9-5f73fc85398d",
                      "status":"enabled",
                      "type":"channel.cheer",
                      "version":"1",
                      "condition":{
                         "broadcaster_user_id":"80805824"
                      },
                      "transport":{
                         "method":"webhook",
                         "callback":"https://microsub.fdm.com.de/bits"
                      },
                      "created_at":"2022-05-21T12:22:45.765923284Z",
                      "cost":0
                   },
                   "event":{
                      "broadcaster_user_id":"80805824",
                      "broadcaster_user_login":"turtoise",
                      "broadcaster_user_name":"Turtoise",
                      "is_anonymous":false,
                      "user_id":"230853735",
                      "user_login":"paauulli",
                      "user_name":"Paauulli",
                      "message":"DoodleCheer1",
                      "bits":1
                   }
                }
                """;
    }
}
