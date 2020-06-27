package ua.com.kl.cmathtutor.service.impl;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;

@ExtendWith(MockitoExtension.class)
class DefaultEventServiceTest extends AbstractCreateReadUpdateServiceTest<Event> {

    @Mock
    private EventRepository eventRepository;

    @Override
    protected AbstractCreateReadUpdateService<Event> getServiceForTest() {
	return new DefaultEventService(eventRepository);
    }

    @Override
    protected CrudRepository<Event> getMockedRepository() {
	return eventRepository;
    }

    @Override
    protected Event getDummyEntity() {
	return new Event();
    }

    @Override
    protected List<Event> getAllEntities() {
	return Lists.newArrayList(
		new Event(),
		Event.builder().baseTicketPriceInCents(12345L).build());
    }

    @Override
    protected void modifyNotIdFields(Event modifiedEntity) {
	modifiedEntity.setName("New event name");
    }

}
