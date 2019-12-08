package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryEmployeeRepository;

class InMemoryEmployeeRepositoryTest extends AbstractCrudInMemoryRepositoryTest<Employee> {

    @Test
    void getInstance_ShouldReturnTheSameInstance() {
	InMemoryEmployeeRepository firstInstance = InMemoryEmployeeRepository.getInstance();

	assertThat(InMemoryEmployeeRepository.getInstance(), is(sameInstance(firstInstance)));
    }

    @Override
    protected InMemoryEmployeeRepository getRepositoryForTesting() {
	return ReflectionUtils.newInstance(InMemoryEmployeeRepository.class);
    }

    @Override
    protected Employee getDummyEntity() {
	return new Employee();
    }

    @Override
    protected void modifyNotIdFields(Employee savedEntity) {
	savedEntity.setName("Jackson");
    }

    @Override
    public Stream<Employee> getAllEntities() {
	return Stream.of(new Employee(), new Employee(),
		Employee.builder().departmentId(-5342).name("Meriaddoc").build(),
		Employee.builder().name("Jack").departmentId(Integer.MAX_VALUE).build(),
		Employee.builder().name("Jack").departmentId(Integer.MAX_VALUE).build(),
		Employee.builder().id(Integer.MIN_VALUE).build());
    }
}
