package ua.com.kl.cmathtutor.service;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exeption.NotFoundException;

public interface UserService extends CreateReadUpdateService<User> {

    User getByEmail(String email) throws NotFoundException;
}
