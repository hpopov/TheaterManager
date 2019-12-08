package ua.com.kl.cmathtutor.repository.inmemory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import ua.com.kl.cmathtutor.domain.entity.Department;

class InMemoryDepartmentRepositoryTest extends AbstractCrudInMemoryRepositoryTest<Department> {

    @Test
    void getInstance_ShouldReturnTheSameInstance() {
	InMemoryDepartmentRepository firstInstance = InMemoryDepartmentRepository.getInstance();

	assertThat(InMemoryDepartmentRepository.getInstance(), is(sameInstance(firstInstance)));
    }

    @Override
    protected InMemoryDepartmentRepository getRepositoryForTesting() {
	return ReflectionUtils.newInstance(InMemoryDepartmentRepository.class);
    }

    @Override
    protected Department getDummyEntity() {
	return new Department();
    }

    @Override
    protected void modifyNotIdFields(Department savedEntity) {
	savedEntity.setName("The last dep");
    }

    @Override
    public Stream<Department> getAllEntities() {
	return Stream.of(
		new Department(),
		new Department(),
		Department.builder().id(-5342).name("Meriaddoc").build(),
		Department.builder().name("Jack").id(Integer.MAX_VALUE).build(),
		Department.builder().name("Jack").id(Integer.MAX_VALUE).build(),
		Department.builder().id(Integer.MIN_VALUE).build());
    }
}
