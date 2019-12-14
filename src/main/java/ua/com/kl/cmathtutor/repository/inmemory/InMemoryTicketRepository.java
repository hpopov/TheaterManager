package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.Objects;

import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.InvalidAttributeException;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.EventPresentationRepository;
import ua.com.kl.cmathtutor.repository.TicketRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;

public class InMemoryTicketRepository extends AbstractRefreshableCrudInMemoryRepository<Ticket>
	implements TicketRepository {

    private static final String INVALID_ATTRIBUTE_MESSAGE = "Entity for attribute [%s] in ticket with id %s does not exist";

    @Setter
    private UserRepository userRepository;
    @Setter
    private EventPresentationRepository eventPresentationRepository;

    @Override
    public Ticket save(Ticket ticket) {
	EventPresentation eventPresentation = ticket.getEventPresentation();
	if (Objects.isNull(eventPresentation)) {
	    throw new MandatoryAttributeException("Ticket must have a reference to EventPresentation!");
	}
	if (!eventPresentationRepository.findById(eventPresentation.getId()).isPresent()) {
	    throw new InvalidAttributeException(
		    String.format(INVALID_ATTRIBUTE_MESSAGE, "eventPresentation", ticket.getId()));
	}
	final User ticketOwner = ticket.getOwner();
	if (Objects.nonNull(ticketOwner) && !userRepository.findById(ticketOwner.getId()).isPresent()) {
	    throw new InvalidAttributeException(
		    String.format(INVALID_ATTRIBUTE_MESSAGE, "owner", ticket.getId()));
	}
	return super.save(ticket);
    }

    @Override
    protected Ticket refresh(Ticket ticket) {
	EventPresentation eventPresentation = eventPresentationRepository
		.findById(ticket.getEventPresentation().getId()).get();
	ticket.setEventPresentation(eventPresentation);
	if (Objects.nonNull(ticket.getOwner())) {
	    User owner = userRepository.findById(ticket.getOwner().getId()).get();
	    ticket.setOwner(owner);
	}
	return ticket;
    }

}
