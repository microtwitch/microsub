package de.com.fdm.twitch;

import com.google.gson.Gson;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.db.data.Auth;
import de.com.fdm.db.data.Consumer;
import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.services.AuthService;
import de.com.fdm.db.services.ConsumerService;
import de.com.fdm.db.services.EventsubService;
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

    @Autowired
    private AuthService authService;

    @Autowired
    private EventsubService eventsubService;

    @Autowired
    private ConsumerService consumerService;

    public TwitchApiProvider() {
        this.restTemplate = new RestTemplate();
    }

    public AppToken generateAuth() {
        String url = String.format(TOKEN_URL_TEMPLATE, config.getClientId(), config.getClientSecret());

        ResponseEntity<AppToken> response = this.restTemplate.postForEntity(url, null, AppToken.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public void registerEventsub(String type, String userId, Consumer consumer) {
        EventsubRegistration registration = new EventsubRegistration(type, "1", userId, config.getUrl() + "follow", config.getSecret());

        Auth auth = this.authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", this.config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getToken());

        Gson gson = new Gson();
        String registrationJson = gson.toJson(registration);
        HttpEntity<String> entity = new HttpEntity<>(registrationJson, headers);

        ResponseEntity<EventsubRegistrationResponse> result = this.restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.POST, entity, EventsubRegistrationResponse.class);

        EventsubRegistrationResponse eventsubRegistrationResponse = result.getBody();

        if (eventsubRegistrationResponse != null) {
            eventsubRegistrationResponse.setSecret(config.getSecret());

            Eventsub eventsub = new Eventsub();
            eventsub.setSecret(config.getSecret());
            eventsub.setTwitchId(eventsubRegistrationResponse.getData().get(0).getId());
            eventsub.setBroadcasterUserId(eventsubRegistrationResponse.getData().get(0).getCondition().getBroadcaster_user_id());

            // TODO: this should not save here, rather return
            eventsubService.save(eventsub);
            consumer.addEventsub(eventsub);
            this.consumerService.save(consumer);
        }
    }

    public void deleteEventsub(String twitchId) {
        Auth auth = this.authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", this.config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String deletionUrl = TWITCH_EVENTSUB_URL + "?id=" + twitchId;

        this.restTemplate.exchange(deletionUrl, HttpMethod.DELETE, entity, String.class);
    }

    public boolean isInvalid(Auth auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + auth.getToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            this.restTemplate.exchange(VALIDATE_URL, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            return true;
        }
        return false;
    }
}
