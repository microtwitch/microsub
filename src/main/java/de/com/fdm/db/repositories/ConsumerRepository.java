package de.com.fdm.db.repositories;


import de.com.fdm.db.data.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Consumer findByCallback(String callback);
}
