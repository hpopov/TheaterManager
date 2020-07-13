package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.List;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.Rating;

public class InMemoryEventRepositoryTest extends AbstractCrudInMemoryRepositoryTest<Event> {

    @Override
    protected InMemoryEventRepository getRepositoryForTesting() {
        return new InMemoryEventRepository();
    }

    @Override
    protected Event getDummyEntity() {
        return Event.builder().baseTicketPriceInCents(123L).name("EventName").rating(Rating.LOW).build();
    }

    @Override
    protected void modifyNotIdFields(Event savedEntity) {
        savedEntity.setName("Changed event name");
    }

    @Override
    protected void modifyUniqueAttributes(Event savedEntity) {
    }

    @Override
    public List<Event> getAllEntities() {
        return Lists.newArrayList(getDummyEntity(), getDummyEntity(), getDummyEntity());
    }
}
