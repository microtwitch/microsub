package de.com.fdm.mongo;

import org.bson.types.ObjectId;

public class Consumer {
    private ObjectId _id;
    private String callback;
    private String broadcasterUserId;

    public Consumer(String callback, String broadcasterUserId) {
        this.callback = callback;
        this.broadcasterUserId = broadcasterUserId;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getBroadcasterUserId() {
        return broadcasterUserId;
    }

    public void setBroadcasterUserId(String broadcasterUserId) {
        this.broadcasterUserId = broadcasterUserId;
    }
}
