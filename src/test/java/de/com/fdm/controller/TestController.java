package de.com.fdm.controller;

import de.com.fdm.config.SecretStore;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

class TestController {
    private static final String TEST_SECRET = "SECRET";

    @Test
    void testfollowEventsChallenge() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        ResponseEntity<String> response = controller.subEvents(getChallengeBody(), new HashMap<>());
        Assertions.assertEquals("TEST_CHALLENGE", response.getBody());
    }

    @Test
    void testfollowEventsEmptyHeader() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        ResponseEntity<String> response = controller.subEvents(getBody(), new HashMap<>());
        Assertions.assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testfollowEvents() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        Map<String, String> headers = getHeaders();

        ResponseEntity<String> response = controller.subEvents(getBody(), headers);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("twitch-eventsub-message-id", "eventsub-message-id");
        headers.put("twitch-eventsub-message-timestamp", "eventsub-message-timestamp");

        String hmacMessage = "eventsub-message-id" + "eventsub-message-timestamp" + getBody();
        String hash = "sha256=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_256, TEST_SECRET).hmacHex(hmacMessage);
        headers.put("twitch-eventsub-message-signature", hash);

        return headers;
    }

    private String getChallengeBody() {
        return """
                {
                   "subscription":{
                      "id":"113437a5-ab92-4497-8684-612f03246ae6",
                      "status":"webhook_callback_verification_pending",
                      "type":"channel.subscribe",
                      "version":"1",
                      "condition":{
                         "broadcaster_user_id":"80805824"
                      },
                      "transport":{
                         "method":"webhook",
                         "callback":"https://microsub.fdm.com.de/sub"
                      },
                      "created_at":"2022-04-29T20:43:10.352828146Z",
                      "cost":0
                   },
                   "challenge":"TEST_CHALLENGE"
                }""";
    }

    private String getBody() {
        return """
                {
                   "subscription":{
                      "id":"113437a5-ab92-4497-8684-612f03246ae6",
                      "status":"enabled",
                      "type":"channel.subscribe",
                      "version":"1",
                      "condition":{
                         "broadcaster_user_id":"80805824"
                      },
                      "transport":{
                         "method":"webhook",
                         "callback":"https://microsub.fdm.com.de/sub"
                      },
                      "created_at":"2022-04-29T20:43:10.352828146Z",
                      "cost":0
                   },
                   "event":{
                      "user_id":"491039216",
                      "user_login":"zoweeh",
                      "user_name":"zoweeh",
                      "broadcaster_user_id":"80805824",
                      "broadcaster_user_login":"turtoise",
                      "broadcaster_user_name":"Turtoise",
                      "tier":"1000",
                      "is_gift":true
                   }
                }""";
    }
}
