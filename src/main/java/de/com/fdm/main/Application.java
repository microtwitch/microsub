package de.com.fdm.main;

import de.com.fdm.main.init.SetupService;
import de.com.fdm.twitch.AuthService;
import de.com.fdm.twitch.data.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import de.com.fdm.twitch.TwitchApiProvider;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "de.com.fdm.*")
@EntityScan(basePackages = "de.com.fdm.db.data")
@EnableScheduling
public class Application {

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private SetupService setupService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateToken() {
        if (!this.authService.hasAuth()) {
            Auth auth = this.twitchApiProvider.generateAuth();
            this.authService.saveAuth(auth);
        }

        setupService.init();
    }
}
