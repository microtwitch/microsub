package de.com.fdm.twitch;

import de.com.fdm.twitch.data.Auth;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private Auth auth;

    public boolean hasAuth() {
        return auth != null;
    }

    public void saveAuth(Auth auth) {
        this.auth = auth;
    }

    public Auth getAuth() {
        return auth;
    }
}
