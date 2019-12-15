package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Calendar;
import java.util.List;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.User;

class InMemoryUserRepositoryTest extends AbstractCrudInMemoryRepositoryTest<User> {

    @Override
    protected InMemoryUserRepository getRepositoryForTesting() {
	return new InMemoryUserRepository();
    }

    @Override
    protected User getDummyEntity() {
	return User.builder().birthdayDate(Calendar.getInstance().getTime()).email("unique").password("nimda").build();
    }

    @Override
    protected void modifyNotIdFields(User savedEntity) {
	savedEntity.setFirstName("Alabama");
    }

    @Override
    protected void modifyUniqueAttributes(User savedEntity) {
	savedEntity.setEmail(savedEntity.getEmail() + "1");
    }

    @Override
    public List<User> getAllEntities() {
	return Lists.newArrayList(getDummyEntity(), getDummyEntity2());
    }

    private User getDummyEntity2() {
	User dummyEntity = getDummyEntity();
	dummyEntity.setEmail("otherUnique");
	return dummyEntity;
    }

}
