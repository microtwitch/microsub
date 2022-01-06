package de.com.fdm.db.data;

import de.com.fdm.twitch.TwitchApiProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "auth")
public class Auth {
    private static final long EXPIRE_BUFFER = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expires_in")
    private Long expiresIn;

    @Column(name = "created_at")
    private Long createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isValid() {
        TwitchApiProvider twitchApiProvider = new TwitchApiProvider();
        boolean isInvalid = twitchApiProvider.isInvalid(this);
        boolean isExpired = (this.createdAt + this.expiresIn + EXPIRE_BUFFER) > Instant.now().getEpochSecond();

        return !isInvalid && !isExpired;
    }
}
