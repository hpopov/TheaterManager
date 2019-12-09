package ua.com.kl.cmathtutor.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {

//    T createInstance();

    List<T> findAll();

    Optional<T> findById(Integer id);

    T save(T entity);

    boolean delete(T entity);
}
