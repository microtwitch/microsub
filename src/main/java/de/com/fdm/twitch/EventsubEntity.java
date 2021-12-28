package de.com.fdm.twitch;

import org.bson.types.ObjectId;

import java.util.List;

public class EventsubEntity {
    private ObjectId _id;
    private List<EventSub.Data> data;
    private int total;
    private int max_total_cost;
    private int total_cost;
    private String secret;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public List<EventSub.Data> getData() {
        return data;
    }

    public void setData(List<EventSub.Data> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMax_total_cost() {
        return max_total_cost;
    }

    public void setMax_total_cost(int max_total_cost) {
        this.max_total_cost = max_total_cost;
    }

    public int getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(int total_cost) {
        this.total_cost = total_cost;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
