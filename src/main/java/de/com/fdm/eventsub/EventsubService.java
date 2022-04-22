package de.com.fdm.eventsub;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class EventsubService {
    private HashMap<String, String> subscriptions;

    public EventsubService() {
        this.subscriptions = new HashMap<>();
    }

    public String getSecret(String id) {
        return subscriptions.getOrDefault(id, "");
    }

    public void setSecret(String userId, String secret) {
        subscriptions.put(userId, secret);
    }
}
