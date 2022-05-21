package de.com.fdm.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.com.fdm.config.EventsubProps;
import de.com.fdm.twitch.data.Auth;
import de.com.fdm.twitch.data.EventSub;
import de.com.fdm.twitch.data.EventsubRegistration;
import de.com.fdm.twitch.data.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class TwitchApiProvider {
    private static final String TOKEN_URL_TEMPLATE =
            "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials";
    private static final String TWITCH_EVENTSUB_URL = "https://api.twitch.tv/helix/eventsub/subscriptions";
    private static final String VALIDATE_URL = "https://id.twitch.tv/oauth2/validate";

    Logger logger = LoggerFactory.getLogger(TwitchApiProvider.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final AuthService authService;
    private final EventsubProps eventsubProps;

    @Autowired
    public TwitchApiProvider(EventsubProps eventsubProps, AuthService authService) {
        this.authService = authService;
        this.eventsubProps = eventsubProps;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public Auth generateAuth() {
        String url = String.format(TOKEN_URL_TEMPLATE, eventsubProps.clientId(), eventsubProps.clientSecret());

        ResponseEntity<Auth> response = this.restTemplate.postForEntity(url, null, Auth.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public void registerEventsub(Type type, String userId) {
        String callbackUrl = eventsubProps.url();
        if (type == Type.FOLLOW) {
            callbackUrl = callbackUrl + "follow";
        }

        if (type == Type.SUB) {
            callbackUrl = callbackUrl + "sub";
        }

        if (type == Type.BITS) {
            callbackUrl = callbackUrl + "bits";
        }

        EventsubRegistration registration = new EventsubRegistration(
                getTwitchType(type), userId, callbackUrl, eventsubProps.secret()
        );

        String registrationJson = null;
        try {
            registrationJson = mapper.writeValueAsString(registration);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        HttpHeaders headers = getHeaders();
        HttpEntity<String> entity = new HttpEntity<>(registrationJson, headers);

        try {
            restTemplate.postForLocation(TWITCH_EVENTSUB_URL, entity);
        } catch (RestClientException e) {
            logger.error(e.getMessage());
        }
    }

    private EventSub getEventsubs() {
        HttpHeaders headers = getHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<EventSub> response = restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.GET, entity, EventSub.class);
        return response.getBody();
    }

    public void deleteEventsub(String id) {
        HttpHeaders headers = getHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        String deletionUrl = TWITCH_EVENTSUB_URL + "?id=" + id;
        restTemplate.exchange(deletionUrl, HttpMethod.DELETE, entity, String.class);
    }

    public boolean isInvalid(Auth auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + auth.accessToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(VALIDATE_URL, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            return true;
        }
        return false;
    }

    private String getTwitchType(Type type) {
        if (type == Type.FOLLOW) {
            return "channel.follow";
        }

        if (type == Type.BITS) {
            return "channel.cheer";
        }

        return "channel.subscribe";
    }

    public void deleteAllEventsubs() {
        EventSub eventSubs = getEventsubs();
        if (eventSubs == null) {
            return;
        }

        for (EventSub.Data sub: eventSubs.data()) {
            deleteEventsub(sub.id());
        }
    }

    private HttpHeaders getHeaders() {
        Auth auth = authService.getAuth();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", eventsubProps.clientId());
        headers.set("Authorization", "Bearer " + auth.accessToken());
        return headers;
    }
}
