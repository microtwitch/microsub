package de.com.fdm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.com.fdm.config.EventsubProps;
import de.com.fdm.eventsub.EventsubConsumer;
import de.com.fdm.twitch.data.BitEvent;
import de.com.fdm.twitch.data.FollowEvent;
import de.com.fdm.twitch.data.SubEvent;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    Logger logger = LoggerFactory.getLogger(Controller.class);
    private static final String CLEANUP_REGEX = "[\n\r\t]";

    private final EventsubConsumer eventsubConsumer;
    private final String secret;
    private final ObjectMapper mapper;

    @Autowired
    public Controller(EventsubConsumer eventsubConsumer, EventsubProps eventsubProps) {
        this.eventsubConsumer = eventsubConsumer;
        this.secret = eventsubProps.secret();
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @PostMapping("/follow")
    public String followEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        FollowEvent followEvent;
        try {
            followEvent = mapper.readValue(body, FollowEvent.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorExpection();
        }

        if (followEvent.challenge() != null) {
            return followEvent.challenge();
        }

        if (!verifySignature(body, headers)) {
            return "";
        }

        body = body.replaceAll(CLEANUP_REGEX, "_");
        logger.info("{}", body);

        eventsubConsumer.consume(followEvent);
        return "";
    }

    @PostMapping("/sub")
    public ResponseEntity<String> subEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        SubEvent subEvent;
        try {
            subEvent = mapper.readValue(body, SubEvent.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorExpection();
        }

        if (subEvent.challenge() != null) {
            return new ResponseEntity<>(subEvent.challenge(), HttpStatus.OK);
        }

        if (!verifySignature(body, headers)) {
            return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
        }

        body = body.replaceAll(CLEANUP_REGEX, "_");
        logger.info("{}", body);

        eventsubConsumer.consume(subEvent);

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/bits")
    public ResponseEntity<String> bitEvents(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        BitEvent bitEvent;
        try {
            bitEvent = mapper.readValue(body, BitEvent.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorExpection();
        }

        if (bitEvent.challenge() != null) {
            return new ResponseEntity<>(bitEvent.challenge(), HttpStatus.OK);
        }

        if (!verifySignature(body, headers)) {
            return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
        }

        body = body.replaceAll(CLEANUP_REGEX, "_");
        logger.info("{}", body);

        eventsubConsumer.consume(bitEvent);

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private boolean verifySignature(String request, Map<String, String> headers) {
        String hmacMessage = this.getHmacMessage(request, headers);
        String hash = "sha256=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(hmacMessage);
        return hash.equals(headers.get("twitch-eventsub-message-signature"));
    }

    private String getHmacMessage(String rawRequest, Map<String, String> headers) {
        return headers.get("twitch-eventsub-message-id") + headers.get("twitch-eventsub-message-timestamp") + rawRequest;
    }
}
