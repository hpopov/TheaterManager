package ua.com.kl.cmathtutor.repository;

import java.util.Optional;

import ua.com.kl.cmathtutor.domain.entity.User;

public interface UserRepository extends CrudRepository<User> {

    Optional<User> findByEmail(String email);
}
