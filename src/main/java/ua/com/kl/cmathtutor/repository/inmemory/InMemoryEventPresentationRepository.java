package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventPresentationRepository extends AbstractCrudInMemoryRepository<EventPresentation>
	implements EventPresentationRepository {

    private static final String ATTRIBUTE_IS_MANDATORY_MSG = "Attribute [%s] is mandatory for entity EventPresentation";

    @Setter
    private EventRepository eventRepository;

    @Override
    public EventPresentation save(EventPresentation eventPresentation) {
	if (Objects.nonNull(eventPresentation.getEvent())) {
	    eventPresentation.setEvent(eventRepository.save(eventPresentation.getEvent()));
	}
	return super.save(eventPresentation);
    }

    @Override
    protected void checkMandatoryAttributes(EventPresentation eventPresentation) {
	if (Objects.isNull(eventPresentation.getAirDate())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "airDate"));
	}
	if (Objects.isNull(eventPresentation.getAuditorium())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "auditorium"));
	}
	if (Objects.isNull(eventPresentation.getDurationInMilliseconds())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "durationInMilliseconds"));
	}
	if (Objects.isNull(eventPresentation.getEvent())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "event"));
	}
    }

    @Override
    public List<EventPresentation> findAll() {
	return super.findAll().stream().map(this::refreshEntityReferences).collect(Collectors.toList());
    }

    @Override
    public Optional<EventPresentation> findById(Integer id) {
	return super.findById(id).map(this::refreshEntityReferences);
    }

    private EventPresentation refreshEntityReferences(EventPresentation eventPresentation) {
	Event event = eventRepository.findById(eventPresentation.getEvent().getId()).get();
	eventPresentation.setEvent(event);
	return eventPresentation;
    }
}
