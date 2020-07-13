package ua.com.kl.cmathtutor.repository.inmemory;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.User;

class InMemoryUserRepositoryTest extends AbstractCrudInMemoryRepositoryTest<User> {

    private InMemoryUserRepository repository;

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

    @BeforeEach
    void setUpUserRepository() {
        repository = getRepositoryForTesting();
    }

    @Test
    void whenUserExists_Then_findByEmail_ShouldReturnUserWithEmail() {
        String email = "email";
        User user = getDummyEntity();
        user.setEmail("email");
        repository.save(user);

        Optional<User> givenUser = repository.findByEmail(email);

        assertTrue(givenUser.isPresent());
        assertThat(givenUser.get(), is(equalTo(user)));
    }

    @Test
    void whenUserNotExist_Then_findByEmail_ShouldReturnEmptyOptional() {
        String email = "email";

        Optional<User> givenUser = repository.findByEmail(email);

        assertFalse(givenUser.isPresent());
    }
}
