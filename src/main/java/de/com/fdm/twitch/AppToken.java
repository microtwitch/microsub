package de.com.fdm.twitch;

import org.bson.types.ObjectId;

import java.time.Instant;

public class AppToken {
    private ObjectId _id;
    private String access_token;
    private String refresh_token;
    private long expires_in;
    private String scope;
    private String token_type;

    public AppToken() {
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public boolean isValid() {
        long timeAlive = Instant.now().getEpochSecond() - this.get_id().getTimestamp();
        System.out.println(timeAlive);

        // add 5min buffer to be safe
        return (timeAlive + 60) < this.expires_in;
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", scope='" + scope + '\'' +
                ", token_type='" + token_type + '\'' +
                '}';
    }
}
