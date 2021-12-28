package de.com.fdm.mongo;

import de.com.fdm.twitch.EventsubEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventsubRepository extends MongoRepository<EventsubEntity, String> {
}
