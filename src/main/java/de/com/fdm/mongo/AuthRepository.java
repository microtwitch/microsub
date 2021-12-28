package de.com.fdm.mongo;

import de.com.fdm.twitch.AppToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthRepository extends MongoRepository<AppToken, String> {
}
