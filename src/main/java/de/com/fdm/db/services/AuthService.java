package de.com.fdm.db.services;

import de.com.fdm.db.data.Auth;
import de.com.fdm.db.repositories.AuthRepository;
import de.com.fdm.twitch.AppToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public void saveApptoken(AppToken token) {
        Auth auth = new Auth();
        auth.setToken(token.getAccess_token());
        auth.setRefreshToken(token.getRefresh_token());
        auth.setExpiresIn(token.getExpires_in());
        auth.setCreatedAt(Instant.now().getEpochSecond());

        this.authRepository.save(auth);
    }

    public void clear() {
        this.authRepository.deleteAll();
    }

    public Auth getAuth() {
        List<Auth> auths = this.authRepository.findAll();

        if (auths.size() == 0) {
            return null;
        }

        return auths.get(0);
    }

    public boolean hasAuth() {
        Auth auth = this.getAuth();

        if (auth == null) {
            return false;
        }

        return auth.isExpired();
    }
}
