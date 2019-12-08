package ua.com.kl.cmathtutor.repository.inmemory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import ua.com.kl.cmathtutor.domain.entity.Bug;

class InMemoryBugRepositoryTest extends AbstractCrudInMemoryRepositoryTest<Bug> {

    @Test
    void getInstance_ShouldReturnTheSameInstance() {
	InMemoryBugRepository firstInstance = InMemoryBugRepository.getInstance();

	assertThat(InMemoryBugRepository.getInstance(), is(sameInstance(firstInstance)));
    }

    @Override
    protected InMemoryBugRepository getRepositoryForTesting() {
	return ReflectionUtils.newInstance(InMemoryBugRepository.class);
    }

    @Override
    protected Bug getDummyEntity() {
	return new Bug();
    }

    @Override
    protected void modifyNotIdFields(Bug savedEntity) {
	savedEntity.setDescription("Some another very big description");
    }

    @Override
    public Stream<Bug> getAllEntities() {
	return Stream.of(
		new Bug(),
		new Bug(),
		Bug.builder().id(-5342).description("Very old bug").build(),
		Bug.builder().description("Super urgent one").id(Integer.MAX_VALUE).build(),
		Bug.builder().description("Just empty description").id(Integer.MAX_VALUE).build(),
		Bug.builder().id(Integer.MIN_VALUE).build());
    }

}
