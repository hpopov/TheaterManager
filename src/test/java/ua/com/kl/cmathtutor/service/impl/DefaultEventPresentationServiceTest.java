package ua.com.kl.cmathtutor.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;

@ExtendWith(MockitoExtension.class)
class DefaultEventPresentationServiceTest extends AbstractCreateReadUpdateServiceTest<EventPresentation> {

    private static final long DURATION_IN_MS = 12334L;
    @Mock
    private EventPresentationRepository eventPresentationRepository;
    private DefaultEventPresentationService service;

    @Override
    protected AbstractCreateReadUpdateService<EventPresentation> getServiceForTest() {
	return new DefaultEventPresentationService(eventPresentationRepository);
    }

    @Override
    protected CrudRepository<EventPresentation> getMockedRepository() {
	return eventPresentationRepository;
    }

    @Override
    protected EventPresentation getDummyEntity() {
	return EventPresentation.builder().airDate(new Date()).durationInMilliseconds(DURATION_IN_MS).build();
    }

    @Override
    protected List<EventPresentation> getAllEntities() {
	return Lists.newArrayList(
		EventPresentation.builder().airDate(new Date()).build(),
		new EventPresentation());
    }

    @Override
    protected void modifyNotIdFields(EventPresentation modifiedEntity) {
	modifiedEntity.setAuditorium(new Auditorium());
    }

    @BeforeEach
    void setUp() {
	service = new DefaultEventPresentationService(eventPresentationRepository);
    }

    @Test
    void whenNewEventPresentationTimeIntersectsWithExistingOne_Then_create_ShouldThrowAnException() {
	EventPresentation existedPresentation = getDummyEntity();
	existedPresentation.setId(1);
	EventPresentation newPresentation = getDummyEntity();
	newPresentation.setAirDate(new Date(newPresentation.getAirDate().getTime() + DURATION_IN_MS / 2));
	when(eventPresentationRepository.findAll()).thenReturn(Lists.newArrayList(existedPresentation));

	assertThrows(RuntimeException.class, () -> service.create(newPresentation));

	verify(eventPresentationRepository, only()).findAll();
    }
}
