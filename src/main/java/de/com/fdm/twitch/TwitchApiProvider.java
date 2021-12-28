package de.com.fdm.twitch;

import com.google.gson.Gson;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.mongo.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class TwitchApiProvider {
    private static final String TOKEN_URL_TEMPLATE =
            "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials";

    private static final String TWITCH_EVENTSUB_URL = "https://api.twitch.tv/helix/eventsub/subscriptions";

    private final RestTemplate restTemplate;

    @Autowired
    private ConfigProperties config;

    @Autowired
    private AuthRepository authRepository;

    public TwitchApiProvider() {
        this.restTemplate = new RestTemplate();
    }

    public AppToken generateAuth() {
        String url = String.format(TOKEN_URL_TEMPLATE, config.getClientId(), config.getSecret());

        ResponseEntity<AppToken> response = this.restTemplate.postForEntity(url, null, AppToken.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public void registerEventsub(String type, String userId, String callback) {
        EventsubRegistration registration = new EventsubRegistration(type, "1", userId, callback, config.getSecret());

        List<AppToken> tokens = this.authRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", this.config.getClientId());
        headers.set("Authorization", "Bearer " + tokens.get(0).getAccess_token());

        Gson gson = new Gson();
        String registrationJson = gson.toJson(registration);
        HttpEntity<String> entity = new HttpEntity<>(registrationJson, headers);

        this.restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.POST, entity, String.class);
    }

    public void deleteEventsub(String broadcasterUserId) {
        String deletionId = "";
        for (EventSub.Data data: getEventsubs().getData()) {
            if (data.getCondition().getBroadcaster_user_id().equals(broadcasterUserId)) {
                deletionId = data.getId();
            }
        }

        List<AppToken> tokens = this.authRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", this.config.getClientId());
        headers.set("Authorization", "Bearer " + tokens.get(0).getAccess_token());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String deletionUrl = TWITCH_EVENTSUB_URL + "?id=" + deletionId;

        this.restTemplate.exchange(deletionUrl, HttpMethod.DELETE, entity, String.class);
    }

    private EventSub getEventsubs() {
        List<AppToken> tokens = this.authRepository.findAll();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", this.config.getClientId());
        headers.set("Authorization", "Bearer " + tokens.get(0).getAccess_token());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<EventSub> eventSubs = this.restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.GET, entity, EventSub.class);

        return eventSubs.getBody();
    }
}
