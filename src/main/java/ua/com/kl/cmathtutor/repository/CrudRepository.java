package ua.com.kl.cmathtutor.repository;

public interface CrudRepository<T> extends CreateReadUpdateRepository<T> {

    boolean delete(T entity);
}
