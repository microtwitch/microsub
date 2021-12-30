package de.com.fdm.db.data;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "consumer")
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumer_id")
    private Long id;

    private String callback;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "consumer_eventsub",
            joinColumns = @JoinColumn(name = "consumer_id"),
            inverseJoinColumns = @JoinColumn(name = "eventsub_id")
    )
    private Set<Eventsub> eventsubs;

    public Consumer() {
        this.eventsubs = new HashSet<>();
    }

    public Consumer(String callback) {
        this.callback = callback;
        this.eventsubs = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Set<Eventsub> getEventsubs() {
        return eventsubs;
    }

    public void setEventsubs(Set<Eventsub> eventsubs) {
        this.eventsubs = eventsubs;
    }

    public void addEventsub(Eventsub eventsub) {
        this.eventsubs.add(eventsub);
    }
}
