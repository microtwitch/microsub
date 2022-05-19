package de.com.fdm.twitch;

import com.google.gson.Gson;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.config.SecretStore;
import de.com.fdm.twitch.data.Auth;
import de.com.fdm.twitch.data.EventSub;
import de.com.fdm.twitch.data.EventsubRegistration;
import de.com.fdm.twitch.data.EventsubRegistrationResponse;
import de.com.fdm.twitch.data.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class TwitchApiProvider {
    private static final String TOKEN_URL_TEMPLATE =
            "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials";
    private static final String TWITCH_EVENTSUB_URL = "https://api.twitch.tv/helix/eventsub/subscriptions";
    private static final String VALIDATE_URL = "https://id.twitch.tv/oauth2/validate";

    private final RestTemplate restTemplate;
    @Autowired
    private ConfigProperties config;

    private final String secret;

    @Autowired
    private AuthService authService;

    public TwitchApiProvider(
            @Autowired SecretStore secretStore
    ) {
        this.secret = secretStore.getSecret();
        this.restTemplate = new RestTemplate();
    }

    public Auth generateAuth() {
        String url = String.format(TOKEN_URL_TEMPLATE, config.getClientId(), config.getClientSecret());

        ResponseEntity<Auth> response = this.restTemplate.postForEntity(url, null, Auth.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public void registerEventsub(Type type, String userId) {
        String callbackUrl = config.getUrl();
        if (type == Type.FOLLOW) {
            callbackUrl = callbackUrl + "follow";
        }

        if (type == Type.SUB) {
            callbackUrl = callbackUrl + "sub";
        }

        EventsubRegistration registration = new EventsubRegistration(getTwitchType(type), "1", userId, callbackUrl, secret);

        Auth auth = authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getAccessToken());

        Gson gson = new Gson();
        String registrationJson = gson.toJson(registration);
        HttpEntity<String> entity = new HttpEntity<>(registrationJson, headers);

        restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.POST, entity, EventsubRegistrationResponse.class);
    }

    private EventSub getEventsubs() {
        Auth auth = authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<EventSub> response = restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.GET, entity, EventSub.class);
        return response.getBody();
    }

    public void deleteEventsub(String id) {
        Auth auth = authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String deletionUrl = TWITCH_EVENTSUB_URL + "?id=" + id;

        restTemplate.exchange(deletionUrl, HttpMethod.DELETE, entity, String.class);
    }

    public boolean isInvalid(Auth auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + auth.getAccessToken());

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

        return "channel.subscribe";
    }

    public void deleteAllEventsubs() {
        EventSub eventSubs = getEventsubs();
        if (eventSubs == null) {
            return;
        }

        for (EventSub.Data sub: eventSubs.getData()) {
            deleteEventsub(sub.getId());
        }
    }
}
