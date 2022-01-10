package de.com.fdm.twitch;

import com.google.gson.Gson;
import de.com.fdm.config.ConfigProperties;
import de.com.fdm.db.data.Auth;
import de.com.fdm.db.data.Consumer;
import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.services.AuthService;
import de.com.fdm.db.services.ConsumerService;
import de.com.fdm.db.services.EventsubService;
import de.com.fdm.grpc.microsub.lib.Type;
import de.com.fdm.twitch.data.AppToken;
import de.com.fdm.twitch.data.EventsubRegistration;
import de.com.fdm.twitch.data.EventsubRegistrationResponse;
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

    public Eventsub registerEventsub(Type type, String userId, Consumer consumer) {
        String callbackUrl = config.getUrl();
        if (type == Type.FOLLOW) {
            callbackUrl = callbackUrl + "follow";
        }

        if (type == Type.SUB) {
            callbackUrl = callbackUrl + "sub";
        }

        EventsubRegistration registration = new EventsubRegistration(getTwitchType(type), "1", userId, callbackUrl, config.getSecret());

        Auth auth = authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getToken());

        Gson gson = new Gson();
        String registrationJson = gson.toJson(registration);
        HttpEntity<String> entity = new HttpEntity<>(registrationJson, headers);

        ResponseEntity<EventsubRegistrationResponse> result = restTemplate.exchange(TWITCH_EVENTSUB_URL, HttpMethod.POST, entity, EventsubRegistrationResponse.class);

        EventsubRegistrationResponse eventsubRegistrationResponse = result.getBody();

        if (eventsubRegistrationResponse != null) {
            eventsubRegistrationResponse.setSecret(config.getSecret());

            Eventsub eventsub = new Eventsub();
            eventsub.setSecret(config.getSecret());
            eventsub.setTwitchId(eventsubRegistrationResponse.getData().get(0).getId());
            eventsub.setBroadcasterUserId(eventsubRegistrationResponse.getData().get(0).getCondition().getBroadcaster_user_id());
            eventsub.setType(type);

            return eventsub;
        }

        return null;
    }

    public void deleteEventsub(String twitchId) {
        Auth auth = authService.getAuth();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", config.getClientId());
        headers.set("Authorization", "Bearer " + auth.getToken());

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String deletionUrl = TWITCH_EVENTSUB_URL + "?id=" + twitchId;

        restTemplate.exchange(deletionUrl, HttpMethod.DELETE, entity, String.class);
    }

    public boolean isInvalid(Auth auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + auth.getToken());

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
}
