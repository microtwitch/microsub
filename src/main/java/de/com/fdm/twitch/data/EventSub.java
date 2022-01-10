package de.com.fdm.twitch.data;

import java.util.List;

public class EventSub {
    private int total;
    private List<Data> data;
    private int total_cost;
    private int max_total_cost;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public int getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(int total_cost) {
        this.total_cost = total_cost;
    }

    public int getMax_total_cost() {
        return max_total_cost;
    }

    public void setMax_total_cost(int max_total_cost) {
        this.max_total_cost = max_total_cost;
    }

    public static class Data {
        private String id;
        private String status;
        private String type;
        private EventsubRegistration.Condition condition;
        private String created_at;
        private Transport transport;
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

        public EventsubRegistration.Condition getCondition() {
            return condition;
        }

        public void setCondition(EventsubRegistration.Condition condition) {
            this.condition = condition;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public Transport getTransport() {
            return transport;
        }

        public void setTransport(Transport transport) {
            this.transport = transport;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public static class Transport {
            private String method;
            private String callback;

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public String getCallback() {
                return callback;
            }

            public void setCallback(String callback) {
                this.callback = callback;
            }
        }
    }
}
