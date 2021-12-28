package de.com.fdm.auth;

// TODO: share this with microauth to avoid communication issues (grpc)
public class AppDetails {
    private String clientId;
    private String[] scopes;

    public AppDetails() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }
}
