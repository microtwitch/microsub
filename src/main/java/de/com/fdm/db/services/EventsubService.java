package de.com.fdm.db.services;

import de.com.fdm.db.data.Eventsub;
import de.com.fdm.db.repositories.EventsubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsubService {

    @Autowired
    private EventsubRepository eventsubRepository;

    public void save(Eventsub eventsub) {
        this.eventsubRepository.save(eventsub);
    }

    public List<Eventsub> getAll() {
        return this.eventsubRepository.findAll();
    }

    public String findSecret(String twitchID) {
        Eventsub eventsub = this.eventsubRepository.findByTwitchId(twitchID);

        if (eventsub == null) {
            return "not found";
        }

        return eventsub.getSecret();
    }

    public void deleteConsumer(String conditionId, String callback) {
        Eventsub eventsub = this.eventsubRepository.findByBroadcasterUserId(conditionId);
        if (eventsub == null) {
            return;
        }
        eventsub.deleteConsumer(callback);
        this.eventsubRepository.save(eventsub);
    }

    public Eventsub findByConditionId(String conditionId) {
        return this.eventsubRepository.findByBroadcasterUserId(conditionId);
    }

    public void delete(Eventsub eventsub) {
        this.eventsubRepository.delete(eventsub);
    }

    public Eventsub findByTwitchId(String twitchId) {
        return this.eventsubRepository.findByTwitchId(twitchId);
    }
}
