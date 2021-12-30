package de.com.fdm.main;

import de.com.fdm.db.services.AuthService;
import de.com.fdm.twitch.AppToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import de.com.fdm.twitch.TwitchApiProvider;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "de.com.fdm.*")
@EnableJpaRepositories(basePackages = "de.com.fdm.db.repositories")
@EntityScan(basePackages = "de.com.fdm.db.data")
public class Application {

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private AuthService authService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateToken() {
        if (!this.authService.hasAuth()) {
            this.authService.clear();
            AppToken auth = this.twitchApiProvider.generateAuth();
            this.authService.saveApptoken(auth);
        }
    }
}
