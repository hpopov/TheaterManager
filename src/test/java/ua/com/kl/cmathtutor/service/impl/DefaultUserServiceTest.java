package ua.com.kl.cmathtutor.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CrudRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest extends AbstractCreateReadUpdateServiceTest<User> {

    @Mock
    UserRepository userRepository;
    DefaultUserService service;

    @Override
    protected AbstractCreateReadUpdateService<User> getServiceForTest() {
        return new DefaultUserService(userRepository);
    }

    @Override
    protected CrudRepository<User> getMockedRepository() {
        return userRepository;
    }

    @Override
    protected User getDummyEntity() {
        return new User();
    }

    @Override
    protected List<User> getAllEntities() {
        return Lists.newArrayList(
                User.builder().birthdayDate(new Date()).isAdmin(true).build(),
                new User(),
                User.builder().email("user@email.com").build());
    }

    @Override
    protected void modifyNotIdFields(User modifiedEntity) {
        modifiedEntity.setFirstName("UserFirstName");
    }

    @BeforeEach
    void setUpUserService() {
        service = new DefaultUserService(userRepository);
    }

    @Test
    void whenUserExists_Then_getByEmail_ShouldReturnUser() throws NotFoundException {
        String email = "email";
        User user = User.builder().email(email).build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        User givenUser = service.getByEmail(email);

        assertThat(givenUser, is(sameInstance(user)));
    }

    @Test
    void whenUserNotExist_Then_getByEmail_ShouldThrowException() throws NotFoundException {
        String email = "email";
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByEmail(email));
    }
}
