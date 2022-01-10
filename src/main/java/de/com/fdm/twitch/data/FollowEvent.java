package de.com.fdm.twitch.data;

public class FollowEvent {
    private Subscription subscription;

    public FollowEvent() {}

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
