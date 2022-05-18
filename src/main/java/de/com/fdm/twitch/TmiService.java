package de.com.fdm.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TmiService {
    private final TwitchClient client;

    public TmiService(@Value("${turtoise.auth}") String auth) {
        OAuth2Credential credentials = new OAuth2Credential("twitch", auth);
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credentials)
                .build();
    }

    public void send(String channel, String message) {
        client.getChat().sendMessage(channel, message);
    }
}
