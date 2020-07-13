package ua.com.kl.cmathtutor.repository.inmemory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;

@ExtendWith(MockitoExtension.class)
class InMemoryEventPresentationRepositoryTest extends AbstractCrudInMemoryRepositoryTest<EventPresentation> {

    @Mock
    InMemoryEventRepository eventRepository;

    @Test
    @Override
    void save_ShouldCreateNewEntity() {
        stubSaveMethodForEventRepository();
        super.save_ShouldCreateNewEntity();
        verify(eventRepository).save(getDummyEntity().getEvent());
    }

    private void stubSaveMethodForEventRepository() {
        when(eventRepository.save(any())).thenAnswer(new ReturnsArgumentAt(0));
    }

    @Test
    @Override
    void save_ShouldReturnSameEntity() {
        stubSaveMethodForEventRepository();
        super.save_ShouldReturnSameEntity();
        verify(eventRepository).save(getDummyEntity().getEvent());
    }

    @Test
    @Override
    void whenEntityIsCreated_Then_findById_withThisEntityId_ShouldReturnEqualButNotSameEntity() {
        stubSaveMethodForEventRepository();
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(new Event()));
        super.whenEntityIsCreated_Then_findById_withThisEntityId_ShouldReturnEqualButNotSameEntity();
        verify(eventRepository).findById(getDummyEntity().getId());
    }

    @Test
    @Override
    void whenEntityExists_Then_delete_ShouldReturnTrueAndDeleteEntity() {
        stubSaveMethodForEventRepository();
        super.whenEntityExists_Then_delete_ShouldReturnTrueAndDeleteEntity();
    }

    // @Test
    // @Override
    // void whenEntityExists_Then_deleteById_ShouldReturnTrueAndDeleteEntity() {
    // stubSaveMethodForEventRepository();
    // super.whenEntityExists_Then_deleteById_ShouldReturnTrueAndDeleteEntity();
    // }

    @Test
    @Override
    void whenEntityIsCreated_AndThenIsModified_Then_findById_withThisEntityId_ShouldReturnOldEntity() {
        stubSaveMethodForEventRepository();
        when(eventRepository.findById(any())).thenReturn(Optional.of(new Event()));
        super.whenEntityIsCreated_AndThenIsModified_Then_findById_withThisEntityId_ShouldReturnOldEntity();
    }

    @Test
    @Override
    void whenEntityIsCreated_AndThenIsModifiedAndSaved_Then_findById_withThisEntityId_ShouldReturnSavedEntity() {
        stubSaveMethodForEventRepository();
        when(eventRepository.findById(any())).thenReturn(Optional.of(new Event()));
        super.whenEntityIsCreated_AndThenIsModifiedAndSaved_Then_findById_withThisEntityId_ShouldReturnSavedEntity();
    }

    @Test
    @Override
    void whenEntityIsCreated_AndThenItsIdModified_Then_findById_withUpdatedEntityId_ShouldReturnNewCreatedEntity() {
        stubSaveMethodForEventRepository();
        when(eventRepository.findById(any())).thenReturn(Optional.of(new Event()));
        super.whenEntityIsCreated_AndThenItsIdModified_Then_findById_withUpdatedEntityId_ShouldReturnNewCreatedEntity();
    }

    @Test
    @Override
    void whenSeveralEntitiesAreCreated_Then_findAll_ShouldReturnAllSavedEntities() {
        stubSaveMethodForEventRepository();
        when(eventRepository.findById(any())).thenReturn(Optional.of(new Event()));
        super.whenSeveralEntitiesAreCreated_Then_findAll_ShouldReturnAllSavedEntities();
        verify(eventRepository, atLeast(getAllEntities().size())).findById(null);
    }

    @Test
    @Override
    void whenSeveralEntitiesAreCreated_Then_TheyShouldContainsSequentialId() {
        stubSaveMethodForEventRepository();
        super.whenSeveralEntitiesAreCreated_Then_TheyShouldContainsSequentialId();
    }

    @Override
    protected AbstractCrudInMemoryRepository<EventPresentation> getRepositoryForTesting() {
        InMemoryEventPresentationRepository repository = new InMemoryEventPresentationRepository();
        repository.setEventRepository(eventRepository);
        return repository;
    }

    @Override
    protected EventPresentation getDummyEntity() {
        return EventPresentation.builder()
                .airDate(new Date(12312312))
                .auditorium(new Auditorium())
                .durationInMilliseconds(123123L)
                .event(new Event())
                .build();
    }

    @Override
    protected void modifyNotIdFields(EventPresentation savedEntity) {
        savedEntity.setDurationInMilliseconds(2100000L);
    }

    @Override
    protected void modifyUniqueAttributes(EventPresentation savedEntity) {
    }

    @Override
    public List<EventPresentation> getAllEntities() {
        return Lists.newArrayList(
                EventPresentation.builder()
                        .airDate(new Date())
                        .auditorium(new Auditorium())
                        .durationInMilliseconds(36000000L)
                        .event(new Event())
                        .build(),
                getDummyEntity());
    }
}
