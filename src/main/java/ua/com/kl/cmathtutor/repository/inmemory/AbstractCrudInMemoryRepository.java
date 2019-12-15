package ua.com.kl.cmathtutor.repository.inmemory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import ua.com.kl.cmathtutor.domain.entity.IdContainer;
import ua.com.kl.cmathtutor.repository.CrudRepository;

public abstract class AbstractCrudInMemoryRepository<T extends IdContainer & Serializable>
	implements CrudRepository<T> {

    private AtomicInteger idCounter;
    private Map<Integer, T> entitiesById;

    public AbstractCrudInMemoryRepository() {
	this.idCounter = new AtomicInteger(1);
	this.entitiesById = new HashMap<>();
    }

    protected Integer selectId() {
	return Integer.valueOf(idCounter.getAndIncrement());
    }

    protected T deepCopy(T entity) {
	return SerializationUtils.roundtrip(entity);
    }

    @Override
    public List<T> findAll() {
	return entitiesById.values().stream().map(this::deepCopy).collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(Integer id) {
	if (Objects.isNull(id)) {
	    return Optional.empty();
	}
	return Optional.ofNullable(entitiesById.get(id));
    }

    @Override
    public T save(T entity) {
	if (Objects.isNull(entity)) {
	    throw new IllegalArgumentException("entity must not be null");
	}
	checkMandatoryAttributes(entity);
	if (Objects.isNull(entity.getId()) || !entitiesById.containsKey(entity.getId())) {
	    entity.setId(selectId());
	}
	entitiesById.put(entity.getId(), deepCopy(entity));
	return entity;
    }

    protected abstract void checkMandatoryAttributes(T entity);

    public boolean deleteById(Integer id) {
	if (Objects.isNull(id)) {
	    return false;
	}
	return entitiesById.remove(id) == null ? false : true;
    }

    public boolean delete(T entity) {
	return deleteById(entity.getId());
    }
}
