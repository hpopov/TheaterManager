package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Objects;
import java.util.Optional;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.DuplicateKeyException;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.UserRepository;

public class InMemoryUserRepository extends AbstractCrudInMemoryRepository<User> implements UserRepository {

    private static final String ATTRIBUTE_IS_MANDATORY_MSG = "Attribute [%s] is mandatory for entity User";

    @Override
    public Optional<User> findByEmail(String email) {
	return findAll().stream().filter(user -> email.equals(user.getEmail())).findFirst();
    }

    @Override
    protected void checkMandatoryAttributes(User entity) {
	if (Objects.isNull(entity.getBirthdayDate())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "birthdayDate"));
	}
	if (Objects.isNull(entity.getEmail())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "email"));
	}
	long usersWithSameEmailCount = findAll().stream()
		.filter(user -> user.getEmail().equals(entity.getEmail()))
		.filter(user -> !user.getId().equals(entity.getId()))
		.count();
	if (usersWithSameEmailCount != 0) {
	    throw new DuplicateKeyException(String.format(
		    "User email has to be unique, but found %s other users with same email", usersWithSameEmailCount));
	}
	if (Objects.isNull(entity.getPassword())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "password"));
	}
    }
}
