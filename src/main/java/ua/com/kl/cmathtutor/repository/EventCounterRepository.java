package ua.com.kl.cmathtutor.repository;

import java.util.Optional;

import ua.com.kl.cmathtutor.domain.entity.EventCounter;

public interface EventCounterRepository extends CrudRepository<EventCounter> {

    Optional<EventCounter> findByEventId(Integer eventId);
}
