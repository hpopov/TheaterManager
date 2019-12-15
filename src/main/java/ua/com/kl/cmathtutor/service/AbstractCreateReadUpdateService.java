package ua.com.kl.cmathtutor.service;

import java.util.List;
import java.util.function.Supplier;

import ua.com.kl.cmathtutor.domain.entity.IdContainer;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.repository.CreateReadUpdateRepository;

public abstract class AbstractCreateReadUpdateService<T extends IdContainer> implements CreateReadUpdateService<T> {

    protected abstract CreateReadUpdateRepository<T> getRepository();

    @Override
    public T create(T entity) {
	entity.setId(null);
	return getRepository().save(entity);
    }

    @Override
    public List<T> getAll() {
	return getRepository().findAll();
    }

    @Override
    public T getById(Integer id) throws NotFoundException {
	return getRepository().findById(id).orElseThrow(notFoundExceptionSupplier(id));
    }

    private Supplier<NotFoundException> notFoundExceptionSupplier(Integer id) {
	return () -> new NotFoundException(makeNotFoundExceptionMessage(id));
    }

    protected abstract String makeNotFoundExceptionMessage(Integer id);

    @Override
    public T updateById(Integer id, T entity) throws NotFoundException {
	entity.setId(id);
	return getRepository().findById(id).map(existedEntity -> getRepository().save(entity))
		.orElseThrow(notFoundExceptionSupplier(id));
    }
}
