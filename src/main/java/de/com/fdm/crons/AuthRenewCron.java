package de.com.fdm.crons;

import de.com.fdm.twitch.AuthService;
import de.com.fdm.twitch.data.Auth;
import de.com.fdm.twitch.TwitchApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AuthRenewCron {
    @Autowired
    private AuthService authService;

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30, initialDelay = 30)
    public void renewAuth() {
        Auth auth = authService.getAuth();
        boolean expire = auth.expiresIn() > 300;
        boolean invalid = twitchApiProvider.isInvalid(auth);

        if (expire || invalid) {
            Auth newAuth = twitchApiProvider.generateAuth();
            authService.saveAuth(newAuth);
        }
    }
}
