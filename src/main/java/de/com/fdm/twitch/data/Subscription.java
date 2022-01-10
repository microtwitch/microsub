package de.com.fdm.twitch.data;

import de.com.fdm.twitch.data.EventsubRegistration;

public class Subscription {
    private String id;
    private String status;
    private String type;
    private String version;
    private EventsubRegistration.Condition condition;
    private EventsubRegistration.Transport transport;
    private String created_at;
    private int cost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public EventsubRegistration.Condition getCondition() {
        return condition;
    }

    public void setCondition(EventsubRegistration.Condition condition) {
        this.condition = condition;
    }

    public EventsubRegistration.Transport getTransport() {
        return transport;
    }

    public void setTransport(EventsubRegistration.Transport transport) {
        this.transport = transport;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", condition=" + condition +
                ", transport=" + transport +
                ", created_at='" + created_at + '\'' +
                ", cost=" + cost +
                '}';
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
