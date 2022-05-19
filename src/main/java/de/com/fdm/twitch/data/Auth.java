package de.com.fdm.twitch.data;


public class Auth {
    private String access_token;
    private long expires_in;

    public Auth() {
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpiresIn() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
