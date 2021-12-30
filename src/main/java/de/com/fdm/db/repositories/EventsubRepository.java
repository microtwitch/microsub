package de.com.fdm.db.repositories;


import de.com.fdm.db.data.Eventsub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventsubRepository extends JpaRepository<Eventsub, Long> {
    Eventsub findByBroadcasterUserId(String broadcasterUserId);
    Eventsub findByTwitchId(String twitchId);
}
