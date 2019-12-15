package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Objects;

import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventRepository;

public class InMemoryEventRepository extends AbstractCrudInMemoryRepository<Event> implements EventRepository {

    private static final String ATTRIBUTE_IS_MANDATORY_MSG = "Attribute [%s] is mandatory for entity Event";

    @Override
    protected void checkMandatoryAttributes(Event event) {
	if (Objects.isNull(event.getBaseTicketPriceInCents())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "baseTicketPriceInCents"));
	}
	if (Objects.isNull(event.getName())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "name"));
	}
	if (Objects.isNull(event.getRating())) {
	    throw new MandatoryAttributeException(String.format(ATTRIBUTE_IS_MANDATORY_MSG, "rating"));
	}
    }

}
