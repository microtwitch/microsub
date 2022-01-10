package de.com.fdm.db.data;

import de.com.fdm.grpc.microsub.lib.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "eventsub")
public class Eventsub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventsub_id")
    private Long id;

    @Column(name = "secret")
    private String secret;

    @Column(name = "twitch_id")
    private String twitchId;

    @Column(name = "broadcaster_user_id")
    private String broadcasterUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "consumer_eventsub",
            joinColumns = @JoinColumn(name = "eventsub_id"),
            inverseJoinColumns = @JoinColumn(name = "consumer_id")
    )
    private Set<Consumer> consumers;

    public Eventsub() {
        this.consumers = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(String twitchId) {
        this.twitchId = twitchId;
    }

    public String getBroadcasterUserId() {
        return broadcasterUserId;
    }

    public void setBroadcasterUserId(String broadcasterUserId) {
        this.broadcasterUserId = broadcasterUserId;
    }

    public Set<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(Set<Consumer> consumers) {
        this.consumers = consumers;
    }

    public void deleteConsumer(String callback) {
        this.consumers.removeIf(consumer -> consumer.getCallback().equals(callback));
    }

    public boolean hasConsumers() {
        return this.consumers.size() != 0;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
