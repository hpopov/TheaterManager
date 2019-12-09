package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventPresentationRepository extends AbstractCrudInMemoryRepository<EventPresentation>
	implements EventPresentationRepository {

    private EventRepository eventRepository;

    private InMemoryEventPresentationRepository(EventRepository eventRepository) {
	this.eventRepository = eventRepository;
    }

    @Override
    public EventPresentation save(EventPresentation entity) {
	if (Objects.isNull(entity.getEvent())) {
	    throw new MandatoryAttributeException("EventPresentation must have reference to Event!");
	}
	if (Objects.isNull(entity.getAuditorium())) {
	    throw new MandatoryAttributeException("EventPresentation must have reference to Auditorium!");
	}
	entity.setEvent(eventRepository.save(entity.getEvent()));
	return super.save(entity);
    }

    @Override
    public Optional<EventPresentation> findById(Integer id) {
	return super.findById(id).map(this::refreshEvent);
    }

    private EventPresentation refreshEvent(EventPresentation eventPresentation) {
	Event event = eventRepository.findById(eventPresentation.getEvent().getId()).get();
	eventPresentation.setEvent(event);
	return eventPresentation;
    }

    @Override
    public List<EventPresentation> findAll() {
	return super.findAll().stream().map(this::refreshEvent).collect(Collectors.toList());
    }
}
