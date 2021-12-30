package de.com.fdm.twitch;

public class EventsubEvent {
    private Subscription subscription;

    public EventsubEvent() {}

    private Event event;


    private String challenge;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getChallenge() {
        return challenge;
    }

    @Override
    public String toString() {
        return "EventsubEvent{" +
                "subscription=" + subscription +
                ", event=" + event +
                ", challenge='" + challenge + '\'' +
                '}';
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public static class Subscription {
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

    public static class Event {
        private String user_id;
        private String user_login;
        private String user_name;
        private String broadcaster_user_id;
        private String broadcaster_user_login;
        private String broadcaster_user_name;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_login() {
            return user_login;
        }

        public void setUser_login(String user_login) {
            this.user_login = user_login;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getBroadcaster_user_id() {
            return broadcaster_user_id;
        }

        public void setBroadcaster_user_id(String broadcaster_user_id) {
            this.broadcaster_user_id = broadcaster_user_id;
        }

        public String getBroadcaster_user_login() {
            return broadcaster_user_login;
        }

        public void setBroadcaster_user_login(String broadcaster_user_login) {
            this.broadcaster_user_login = broadcaster_user_login;
        }

        public String getBroadcaster_user_name() {
            return broadcaster_user_name;
        }

        public void setBroadcaster_user_name(String broadcaster_user_name) {
            this.broadcaster_user_name = broadcaster_user_name;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "user_id='" + user_id + '\'' +
                    ", user_login='" + user_login + '\'' +
                    ", user_name='" + user_name + '\'' +
                    ", broadcaster_user_id='" + broadcaster_user_id + '\'' +
                    ", broadcaster_user_login='" + broadcaster_user_login + '\'' +
                    ", broadcaster_user_name='" + broadcaster_user_name + '\'' +
                    '}';
        }
    }
}
