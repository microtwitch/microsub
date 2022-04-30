package de.com.fdm.main;

import de.com.fdm.main.init.SetupService;
import de.com.fdm.twitch.TwitchApiProvider;
import de.com.fdm.twitch.data.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockSetupService implements SetupService {
    @Autowired
    private TwitchApiProvider twitchApiProvider;

    public void init() {
        twitchApiProvider.deleteAllEventsubs();
        twitchApiProvider.registerEventsub(Type.FOLLOW, "116672490");
    }
}
