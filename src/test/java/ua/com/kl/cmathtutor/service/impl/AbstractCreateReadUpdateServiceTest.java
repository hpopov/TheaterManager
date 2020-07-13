package ua.com.kl.cmathtutor.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import ua.com.kl.cmathtutor.domain.entity.IdContainer;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;

abstract class AbstractCreateReadUpdateServiceTest<T extends IdContainer> {

    private AbstractCreateReadUpdateService<T> service;
    private CrudRepository<T> repository;

    @BeforeEach
    void setUpAbstract() {
        service = getServiceForTest();
        repository = getMockedRepository();
    }

    protected abstract AbstractCreateReadUpdateService<T> getServiceForTest();

    protected abstract CrudRepository<T> getMockedRepository();

    @Test
    void create_ShouldSetNullIdForEntityAndSaveItUsingRepository() {
        T entity = getDummyEntity();
        entity.setId(100);
        when(repository.save(any())).thenAnswer(new ReturnsArgumentAt(0));

        T returnedEntity = service.create(entity);

        assertAll(() -> assertThat(returnedEntity, is(sameInstance(entity))),
                () -> assertThat(returnedEntity.getId(), is(nullValue())));

        verify(repository).save(entity);
    }

    protected abstract T getDummyEntity();

    @Test
    void getAll_ShouldReturnRepositoryFindAllInvokationResult() {
        List<T> entities = getAllEntities();
        when(repository.findAll()).thenReturn(entities);

        List<T> returnedEntities = service.getAll();

        assertThat(returnedEntities, containsInAnyOrder(entities.toArray()));
        verify(repository).findAll();
    }

    protected abstract List<T> getAllEntities();

    @Test
    void whenEntityExists_Then_getById_ShouldReturnTheSameEntity() throws NotFoundException {
        int id = 12;
        T existedEntity = getDummyEntity();
        existedEntity.setId(id);
        when(repository.findById(any())).thenReturn(Optional.of(existedEntity));

        T returnedEntity = service.getById(id);

        assertAll(() -> assertThat(returnedEntity, is(sameInstance(existedEntity))),
                () -> assertThat(returnedEntity.getId(), is(equalTo(id))));
        verify(repository).findById(id);
    }

    @Test
    void whenEntityNotExist_Then_getById_ShouldReturnTheSameEntity() throws NotFoundException {
        int id = 12;
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(id));
        verify(repository).findById(id);
    }

    @Test
    void whenEntityExists_Then_updateById_ShouldReturnSameEntity() throws NotFoundException {
        int id = 12;
        T existedEntity = getDummyEntity();
        existedEntity.setId(id);
        T modifiedEntity = getDummyEntity();
        modifyNotIdFields(modifiedEntity);
        when(repository.findById(any())).thenReturn(Optional.of(existedEntity));
        when(repository.save(any())).thenReturn(modifiedEntity);

        T returnedEntity = service.updateById(id, modifiedEntity);

        assertAll(() -> assertThat(returnedEntity, is(sameInstance(modifiedEntity))),
                () -> assertThat(returnedEntity.getId(), is(equalTo(id))));
        verify(repository).findById(id);
        verify(repository).save(modifiedEntity);
    }

    protected abstract void modifyNotIdFields(T modifiedEntity);

    @Test
    void whenEntityNotExist_Then_updateById_ShouldThrowException() throws NotFoundException {
        int id = 12;
        T modifiedEntity = getDummyEntity();
        modifyNotIdFields(modifiedEntity);
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateById(id, modifiedEntity));
        verify(repository).findById(id);
        verify(repository, never()).save(modifiedEntity);
    }
}
