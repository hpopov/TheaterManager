package ua.com.kl.cmathtutor.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> findAll();

    Optional<T> findById(Integer id);

    T save(T department);
}
