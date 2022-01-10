package de.com.fdm.twitch.data;

public class EventsubRegistration {
    private final String type;
    private final String version;
    private final Condition condition;
    private final Transport transport;

    public EventsubRegistration(String type, String version, String userId, String callback, String secret) {
        this.type = type;
        this.version = version;
        this.condition = new Condition(userId);
        this.transport = new Transport(callback, secret);
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public Condition getCondition() {
        return condition;
    }

    public Transport getTransport() {
        return transport;
    }

    @Override
    public String toString() {
        return "EventsubRegistration{" +
                "type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", condition='" + condition + '\'' +
                ", transport='" + transport + '\'' +
                '}';
    }

    public static class Condition {
        private String broadcaster_user_id;

        public Condition(){}

        public Condition(String userId) {
            this.broadcaster_user_id = userId;
        }

        public String getBroadcaster_user_id() {
            return broadcaster_user_id;
        }

        public void setBroadcaster_user_id(String broadcaster_user_id) {
            this.broadcaster_user_id = broadcaster_user_id;
        }

        @Override
        public String toString() {
            return "Condition{" +
                    "user_id='" + broadcaster_user_id + '\'' +
                    '}';
        }
    }

    public static class Transport {
        private String method;
        private String callback;
        private String secret;

        public Transport(String callback, String secret) {
            this.method = "webhook";
            this.callback = callback;
            this.secret = secret;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getMethod() {
            return method;
        }

        public String getCallback() {
            return callback;
        }

        @Override
        public String toString() {
            return "Transport{" +
                    "method='" + method + '\'' +
                    ", callback='" + callback + '\'' +
                    ", secret='" + secret + '\'' +
                    '}';
        }

        public String getSecret() {
            return secret;
        }
    }
}
