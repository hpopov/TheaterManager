package ua.com.kl.cmathtutor.repository.inmemory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.kl.cmathtutor.domain.entity.IdContainer;

abstract public class AbstractCrudInMemoryRepositoryTest<T extends Serializable & IdContainer> {

    protected AbstractCrudInMemoryRepository<T> repository;

    @BeforeEach
    private void setUpBeforeEachAbstract() throws Exception {
	repository = getRepositoryForTesting();
    }

    protected abstract AbstractCrudInMemoryRepository<T> getRepositoryForTesting();

    protected abstract T getDummyEntity();

    @Test
    void save_ShouldReturnSameEntity() {
	T entity = getDummyEntity();

	T savedEntity = repository.save(entity);

	assertSame(entity, savedEntity);
    }

    @Test
    void save_ShouldCreateNewEntity() {
	T savedEntity = repository.save(getDummyEntity());

	assertThat("The first created entity should has id of 1", savedEntity.getId(), is(equalTo(1)));
    }

    @Test
    void findById_withIdIsNull_ShouldReturnEmptyOptional() {
	assertFalse(repository.findById(null).isPresent());
    }

    @Test
    void whenEntityIsCreated_Then_findById_withThisEntityId_ShouldReturnEqualButNotSameEntity() {
	T savedEntity = repository.save(getDummyEntity());

	Optional<T> foundEntity = repository.findById(savedEntity.getId());

	assertAll(() -> assertTrue(foundEntity.isPresent()),
		() -> assertThat(foundEntity.get(), is(equalTo(savedEntity))),
		() -> assertThat(foundEntity.get(), not(sameInstance(savedEntity))));
    }

    @Test
    void whenEntityIsCreated_AndThenIsModifiedAndSaved_Then_findById_withThisEntityId_ShouldReturnSavedEntity() {
	T savedEntity = repository.save(getDummyEntity());
	int savedEntityId = savedEntity.getId();
	modifyNotIdFields(savedEntity);
	repository.save(savedEntity);

	Optional<T> foundEntity = repository.findById(savedEntityId);

	assertThat(foundEntity.get(), is(equalTo(savedEntity)));
    }

    protected abstract void modifyNotIdFields(T savedEntity);

    @Test
    void whenEntityIsCreated_AndThenIsModified_Then_findById_withThisEntityId_ShouldReturnOldEntity() {
	T savedEntity = repository.save(getDummyEntity());
	int savedEntityId = savedEntity.getId();
	modifyNotIdFields(savedEntity);

	Optional<T> foundEntity = repository.findById(savedEntityId);

	assertThat(foundEntity.get(), not(equalTo(savedEntity)));
    }

    @Test
    void whenEntityIsCreated_AndThenItsIdModified_Then_findById_withUpdatedEntityId_ShouldReturnNewCreatedEntity() {
	T savedEntity = repository.save(getDummyEntity());
	Integer savedOldEntityId = savedEntity.getId();
	savedEntity.setId(2 * savedOldEntityId);
	repository.save(savedEntity);

	Optional<T> foundEntity = repository.findById(savedEntity.getId());
	Optional<T> foundOldEntity = repository.findById(savedOldEntityId);

	assertAll(() -> assertNotNull(foundEntity.get()),
		() -> assertThat(foundEntity.get(), is(equalTo(savedEntity))),
		() -> assertThat(foundEntity.get(), not(sameInstance(savedEntity))),
		() -> assertThat(foundOldEntity.get(), not(equalTo(savedEntity))));
    }

    @Test
    void whenSeveralEntitiesAreCreated_Then_findAll_ShouldReturnAllSavedEntities() {
	Stream<T> entities = getAllEntities();
	List<T> expectedEntities = entities.map(repository::save).collect(Collectors.toList());

	List<T> foundEntities = repository.findAll();

	assertThat(foundEntities, containsInAnyOrder(expectedEntities.toArray()));
    }

    public abstract Stream<T> getAllEntities();

    @Test
    void whenEntityExists_Then_deleteById_ShouldReturnTrueAndDeleteEntity() {
	T entity = repository.save(getDummyEntity());

	assertAll(() -> assertTrue(repository.deleteById(entity.getId())),
		() -> assertFalse(repository.findById(entity.getId()).isPresent()));
    }

    @Test
    void whenEntityNotExists_Then_deleteById_ShouldReturnFalse() {
	assertFalse(repository.deleteById(123));
    }

    @Test
    void whenEntityExists_Then_delete_ShouldReturnTrueAndDeleteEntity() {
	T entity = repository.save(getDummyEntity());

	assertAll(() -> assertTrue(repository.delete(entity)),
		() -> assertFalse(repository.findById(entity.getId()).isPresent()));
    }

    @Test
    void whenEntityNotExists_Then_delete_ShouldReturnFalse() {
	assertFalse(repository.delete(getDummyEntity()));
    }

    @ParameterizedTest(name = "Id of created entity among {0} created ones must be equal to index+1")
    @ValueSource(ints = { 5, 4, 27 })
    void whenSeveralEntitiesAreCreated_Then_TheyShouldContainsSequentialId(int n) {
	List<T> createdEntities = new LinkedList<>();

	for (int i = 0; i < n; ++i) {
	    createdEntities.add(repository.save(getDummyEntity()));
	}

	assertThat(createdEntities.stream().map(e -> new ImmutablePair<>(createdEntities.indexOf(e), e))
		.collect(Collectors.toSet()), everyItem(hasCorrectId()));
    }

    private Matcher<ImmutablePair<Integer, T>> hasCorrectId() {
	return new EntitiesIdMatcher();
    }

    private class EntitiesIdMatcher extends CustomTypeSafeMatcher<ImmutablePair<Integer, T>> {

	public EntitiesIdMatcher() {
	    super("The entity index should be less by 1 then the entity id");
	}

	@Override
	protected boolean matchesSafely(ImmutablePair<Integer, T> item) {
	    return item.getValue().getId().equals(item.getKey() + 1);
	}

    }

}
