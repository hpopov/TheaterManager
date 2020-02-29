package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Objects;
import java.util.Optional;

import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.EventCounter;
import ua.com.kl.cmathtutor.exception.DuplicateKeyException;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventCounterRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventCounterRepository extends AbstractCrudInMemoryRepository<EventCounter>
	implements EventCounterRepository {

    private static final String EVENT_SHOULD_EXIST_MSG = "Event for specified eventId should be persisted already";

    private static final String ATTRIBUTE_IS_MANDATORY_MSG = "Attribute [%s] is mandatory for entity EventCounter";

    @Setter
    private EventRepository eventRepository;

    @Override
    protected void checkMandatoryAttributes(EventCounter eventCounter) {
	if (Objects.isNull(eventCounter.getEventId())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "eventId"));
	}
	if (!eventRepository.findById(eventCounter.getEventId()).isPresent()) {
	    throw new MandatoryAttributeException(EVENT_SHOULD_EXIST_MSG);
	}
	long eventCountersWithSameEventId = findAll().stream()
		.filter(ec -> ec.getEventId().equals(eventCounter.getEventId()))
		.filter(ec -> !ec.getId().equals(eventCounter.getId()))
		.count();
	if (eventCountersWithSameEventId != 0) {
	    throw new DuplicateKeyException(String.format(
		    "Event id has to be unique, but found %s other eventCounters with same event id",
		    eventCountersWithSameEventId));
	}
    }

    @Override
    public Optional<EventCounter> findByEventId(Integer eventId) {
	return findAll().stream().filter(ec -> ec.getEventId().equals(eventId)).findFirst();
    }
}
