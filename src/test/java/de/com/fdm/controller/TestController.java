package de.com.fdm.controller;

import de.com.fdm.config.SecretStore;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class TestController {
    private static final String TEST_SECRET = "SECRET";

    @Test
    public void testfollowEventsChallenge() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        ResponseEntity<String> response = controller.subEvents(getChallengeBody(), new HashMap<>());
        Assertions.assertEquals("TEST_CHALLENGE", response.getBody());
    }

    @Test
    public void testfollowEventsEmptyHeader() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        ResponseEntity<String> response = controller.subEvents(getBody(), new HashMap<>());
        Assertions.assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    public void testfollowEvents() {
        Controller controller = new Controller(new MockConsumer(), new SecretStore(TEST_SECRET));
        Map<String, String> headers = getHeaders();

        ResponseEntity<String> response = controller.subEvents(getBody(), headers);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    private Map<String, String> getHeaders() {
        HashMap headers = new HashMap();
        headers.put("twitch-eventsub-message-id", "eventsub-message-id");
        headers.put("twitch-eventsub-message-timestamp", "eventsub-message-timestamp");

        String hmacMessage = "eventsub-message-id" + "eventsub-message-timestamp" + getBody();
        String hash = "sha256=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_256, TEST_SECRET).hmacHex(hmacMessage);
        headers.put("twitch-eventsub-message-signature", hash);

        return headers;
    }

    private String getChallengeBody() {
        return "{\n" +
                "   \"subscription\":{\n" +
                "      \"id\":\"113437a5-ab92-4497-8684-612f03246ae6\",\n" +
                "      \"status\":\"webhook_callback_verification_pending\",\n" +
                "      \"type\":\"channel.subscribe\",\n" +
                "      \"version\":\"1\",\n" +
                "      \"condition\":{\n" +
                "         \"broadcaster_user_id\":\"80805824\"\n" +
                "      },\n" +
                "      \"transport\":{\n" +
                "         \"method\":\"webhook\",\n" +
                "         \"callback\":\"https://microsub.fdm.com.de/sub\"\n" +
                "      },\n" +
                "      \"created_at\":\"2022-04-29T20:43:10.352828146Z\",\n" +
                "      \"cost\":0\n" +
                "   },\n" +
                "   \"challenge\":\"TEST_CHALLENGE\"\n" +
                "}";
    }

    private String getBody() {
        return "{\n" +
                "   \"subscription\":{\n" +
                "      \"id\":\"113437a5-ab92-4497-8684-612f03246ae6\",\n" +
                "      \"status\":\"enabled\",\n" +
                "      \"type\":\"channel.subscribe\",\n" +
                "      \"version\":\"1\",\n" +
                "      \"condition\":{\n" +
                "         \"broadcaster_user_id\":\"80805824\"\n" +
                "      },\n" +
                "      \"transport\":{\n" +
                "         \"method\":\"webhook\",\n" +
                "         \"callback\":\"https://microsub.fdm.com.de/sub\"\n" +
                "      },\n" +
                "      \"created_at\":\"2022-04-29T20:43:10.352828146Z\",\n" +
                "      \"cost\":0\n" +
                "   },\n" +
                "   \"event\":{\n" +
                "      \"user_id\":\"491039216\",\n" +
                "      \"user_login\":\"zoweeh\",\n" +
                "      \"user_name\":\"zoweeh\",\n" +
                "      \"broadcaster_user_id\":\"80805824\",\n" +
                "      \"broadcaster_user_login\":\"turtoise\",\n" +
                "      \"broadcaster_user_name\":\"Turtoise\",\n" +
                "      \"tier\":\"1000\",\n" +
                "      \"is_gift\":true\n" +
                "   }\n" +
                "}";
    }
}
