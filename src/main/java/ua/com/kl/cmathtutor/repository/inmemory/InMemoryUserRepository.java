package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Optional;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.DuplicateKeyException;
import ua.com.kl.cmathtutor.repository.UserRepository;

public class InMemoryUserRepository extends AbstractCrudInMemoryRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByEmail(String email) {
	return findAll().stream().filter(user -> email.equals(user.getEmail())).findFirst();
    }

    @Override
    public User save(User entity) {
	long usersWithSameEmailCount = findAll().stream()
		.filter(user -> entity.getEmail().equals(user.getEmail()))
		.filter(user -> !entity.getId().equals(user.getId()))
		.count();
	if (usersWithSameEmailCount != 0) {
	    throw new DuplicateKeyException(String.format(
		    "User email has to be unique, but found %s users with same email", usersWithSameEmailCount));
	}
	return super.save(entity);
    }
}
