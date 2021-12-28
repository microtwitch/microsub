package de.com.fdm.main;

import de.com.fdm.mongo.AuthRepository;
import de.com.fdm.twitch.AppToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import de.com.fdm.twitch.TwitchApiProvider;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@SpringBootApplication(scanBasePackages = {
        "de.com.fdm.twitch",
        "de.com.fdm.config",
        "de.com.fdm.mongo",
        "de.com.fdm.auth",
        "de.com.fdm.grpc",
        "de.com.fdm.controller"})
@EnableMongoRepositories(basePackageClasses = AuthRepository.class)
public class Application {

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    @Autowired
    private AuthRepository authRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateToken() {
        if (!this.hasAuth()) {
            this.authRepository.deleteAll();
            AppToken auth = this.twitchApiProvider.generateAuth();
            this.authRepository.save(auth);
        }
    }

    private boolean hasAuth() {
        List<AppToken> auths = this.authRepository.findAll();

        if (auths.size() == 0) {
            return false;
        }

        return auths.get(0).isValid();
    }
}
