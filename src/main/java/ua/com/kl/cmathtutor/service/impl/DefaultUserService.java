package ua.com.kl.cmathtutor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CreateReadUpdateRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;
import ua.com.kl.cmathtutor.service.AbstractCreateReadUpdateService;
import ua.com.kl.cmathtutor.service.UserService;

@Service
public class DefaultUserService extends AbstractCreateReadUpdateService<User> implements UserService {

    private UserRepository userRepository;

    @Autowired
    public DefaultUserService(UserRepository userRepository) {
	this.userRepository = userRepository;
    }

    @Override
    public User getByEmail(String email) throws NotFoundException {
	return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(
		String.format("User with email %s was not found", email)));
    }

    @Override
    protected CreateReadUpdateRepository<User> getRepository() {
	return userRepository;
    }

    @Override
    protected String makeNotFoundExceptionMessage(Integer id) {
	return String.format("User with id %s was not found", id);
    }

}
