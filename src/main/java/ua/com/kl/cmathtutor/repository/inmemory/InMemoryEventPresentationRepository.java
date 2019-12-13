package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Objects;

import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventPresentationRepository extends AbstractRefreshableCrudInMemoryRepository<EventPresentation>
	implements EventPresentationRepository {

    @Setter
    private EventRepository eventRepository;

    @Override
    public EventPresentation save(EventPresentation eventPresentation) {
	if (Objects.isNull(eventPresentation.getEvent())) {
	    throw new MandatoryAttributeException("EventPresentation must have reference to Event!");
	}
	if (Objects.isNull(eventPresentation.getAuditorium())) {
	    throw new MandatoryAttributeException("EventPresentation must have reference to Auditorium!");
	}
	eventPresentation.setEvent(eventRepository.save(eventPresentation.getEvent()));
	return super.save(eventPresentation);
    }

    @Override
    protected EventPresentation refresh(EventPresentation eventPresentation) {
	Event event = eventRepository.findById(eventPresentation.getEvent().getId()).get();
	eventPresentation.setEvent(event);
	return eventPresentation;
    }
}
