package ua.com.kl.cmathtutor.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;
import ua.com.kl.cmathtutor.service.EventPresentationService;

@Service
public class DefaultEventPresentationService extends AbstractCreateReadUpdateService<EventPresentation>
	implements EventPresentationService {

    private EventPresentationRepository eventPresentationRepository;

    @Autowired
    public DefaultEventPresentationService(EventPresentationRepository eventPresentationRepository) {
	this.eventPresentationRepository = eventPresentationRepository;
    }

    @Override
    public EventPresentation create(EventPresentation eventPresentation) {
	assertEventPresentationIsNotConnectedToExistingOnes(eventPresentation);
	return super.create(eventPresentation);
    }

    private void assertEventPresentationIsNotConnectedToExistingOnes(EventPresentation eventPresentation)
	    throws IllegalArgumentException {
	if (isEventPresentationConnectedToExisting(eventPresentation)) {
	    throw new IllegalArgumentException(
		    "Air time range in this eventPresentation intersects with existing presentations!");
	}
    }

    private boolean isEventPresentationConnectedToExisting(EventPresentation eventPresentation) {
	Auditorium auditorium = eventPresentation.getAuditorium();
	Range<Date> currentEventPresentationTimeRange = getTimeRangeFromEventPresentation(eventPresentation);
	return eventPresentationRepository.findAll().stream()
		.filter(presentation -> auditorium.equals(presentation.getAuditorium()))
		.filter(presentation -> !presentation.getId().equals(eventPresentation.getId()))
		.map(this::getTimeRangeFromEventPresentation)
		.anyMatch(tRange -> currentEventPresentationTimeRange.isConnected(tRange));
    }

    private Range<Date> getTimeRangeFromEventPresentation(EventPresentation eventPresentation) {
	return Range.open(eventPresentation.getAirDate(),
		new Date(eventPresentation.getAirDate().getTime() + eventPresentation.getDurationInMilliseconds()));
    }

    @Override
    public EventPresentation updateById(Integer id, EventPresentation entity) throws NotFoundException {
	entity.setId(id);
	assertEventPresentationIsNotConnectedToExistingOnes(entity);
	return super.updateById(id, entity);
    }

    @Override
    protected CrudRepository<EventPresentation> getRepository() {
	return eventPresentationRepository;
    }

    @Override
    protected String makeNotFoundExceptionMessage(Integer id) {
	return String.format("EventPresentation with id %s was not found", id);
    }

}
