package ua.com.kl.cmathtutor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;
import ua.com.kl.cmathtutor.service.EventService;

@Service
public class DefaultEventService extends AbstractCreateReadUpdateService<Event> implements EventService {

    private EventRepository eventRepository;

    @Autowired
    public DefaultEventService(EventRepository eventRepository) {
	this.eventRepository = eventRepository;
    }

    @Override
    protected CrudRepository<Event> getRepository() {
	return eventRepository;
    }

    @Override
    protected String makeNotFoundExceptionMessage(Integer id) {
	return String.format("Event with id %s was not found", id);
    }

    @Override
    public Event getById(Integer id) throws NotFoundException {
	return super.getById(id);
    }

}
