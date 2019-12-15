package ua.com.kl.cmathtutor.repository.inmemory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ua.com.kl.cmathtutor.domain.entity.IdContainer;

public abstract class AbstractRefreshableCrudInMemoryRepository<T extends IdContainer & Serializable>
	extends AbstractCrudInMemoryRepository<T> {

    @Override
    public List<T> findAll() {
        return super.findAll().stream().map(this::refresh).collect(Collectors.toList());
    }
    
    @Override
    public Optional<T> findById(Integer id) {
        return super.findById(id).map(this::refresh);
    }
    
    protected abstract T refresh(T entity);
}
