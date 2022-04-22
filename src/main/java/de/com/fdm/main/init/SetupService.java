package de.com.fdm.main.init;

import de.com.fdm.twitch.TwitchApiProvider;
import de.com.fdm.twitch.data.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetupService {
    private static final String TURTOISE_ID = "80805824";

    @Autowired
    private TwitchApiProvider twitchApiProvider;

    public void init() {
        // TODO: change to correct ID
        twitchApiProvider.deleteAllEventsubs();
        twitchApiProvider.registerEventsub(Type.FOLLOW, "116672490");
    }
}
