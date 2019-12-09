package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.stream.Stream;

import ua.com.kl.cmathtutor.domain.entity.User;

class InMemoryUserRepositoryTest extends AbstractCrudInMemoryRepositoryTest<User> {

    @Override
    protected InMemoryUserRepository getRepositoryForTesting() {
	return new InMemoryUserRepository();
    }

    @Override
    protected User getDummyEntity() {
	return new User();
    }

    @Override
    protected void modifyNotIdFields(User savedEntity) {
	savedEntity.setFirstName("Alabama");
    }

    @Override
    public Stream<User> getAllEntities() {
	return Stream.of(new User());
    }

}
