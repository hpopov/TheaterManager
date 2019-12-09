package ua.com.kl.cmathtutor.repository.inmemory;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventRepository extends AbstractCrudInMemoryRepository<Event> implements EventRepository {

}
