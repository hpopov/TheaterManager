package ua.com.kl.cmathtutor.service;

import java.util.List;

import ua.com.kl.cmathtutor.exception.NotFoundException;

public interface CreateReadUpdateService<T> {

    T create(T entity);

    List<T> getAll();

    T getById(Integer id) throws NotFoundException;

    T updateById(Integer id, T entity) throws NotFoundException;

}
