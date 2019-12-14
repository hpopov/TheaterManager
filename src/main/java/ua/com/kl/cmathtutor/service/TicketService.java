package ua.com.kl.cmathtutor.service;

import java.util.List;
import java.util.Set;

import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.TicketsAlreadyBookedException;

public interface TicketService {

    Set<Integer> getAvailableSeatsForEventPresentation(EventPresentation eventPresentation);

    List<Ticket> getNewTicketsForEventPresentation(EventPresentation eventPresentation, Set<Integer> seatNumbers)
	    throws TicketsAlreadyBookedException;

    List<Ticket> getNewTicketsForEventPresentationAndOwner(EventPresentation eventPresentation,
	    Set<Integer> seatNumbers,
	    User owner) throws TicketsAlreadyBookedException;

    List<Ticket> bookTickets(List<Ticket> tickets) throws TicketsAlreadyBookedException;

    List<Ticket> getPurchasedTicketsForEventPresentation(EventPresentation eventPresentation);
}
