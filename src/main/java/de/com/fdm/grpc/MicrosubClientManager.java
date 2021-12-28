package de.com.fdm.grpc;


import de.com.fdm.grpc.microsub.lib.EventsubMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MicrosubClientManager {
    private final HashMap<String, MicrosubClient> clients;

    public MicrosubClientManager() {
        this.clients = new HashMap<>();
    }

    public void sendMessage(EventsubMessage msg, String callback) {
        if (!this.clients.containsKey(callback)) {
            this.clients.put(callback, new MicrosubClient(callback));
        }

        MicrosubClient client = this.clients.get(callback);

        client.send(msg);
    }
}
