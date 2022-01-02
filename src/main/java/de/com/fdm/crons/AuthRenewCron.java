package de.com.fdm.crons;

import de.com.fdm.db.services.AuthService;
import de.com.fdm.twitch.AppToken;
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

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30, initialDelay = 60)
    public void renewAuth() {
        if (!authService.hasAuth()) {
            AppToken appToken = twitchApiProvider.generateAuth();
            authService.clear();
            authService.saveApptoken(appToken);
        }
    }
}
