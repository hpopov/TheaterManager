package ua.com.kl.cmathtutor.shell.command.ticket;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.exception.TicketsAlreadyBookedException;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.shell.command.auth.AuthenticationState;

@Component
public class BookTicketsCommands implements CommandMarker {
    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private EventPresentationService eventPresentationService;
    @Autowired
    private TicketState ticketState;

    @CliAvailabilityIndicator({ "ticket book" })
    public boolean isObserveTicketsCommandAvailable() {
	return true;
    }

    @CliAvailabilityIndicator({ "ticket book-confirm", "ticket book-discard", "ticket book-review" })
    public boolean isBookTicketsCommandAvailable() {
	return ticketState.hasTicketToPurchase();
    }

    @CliCommand(value = "ticket book",
	    help = "Observe tickets for the specified event presentation and seats to be able to book them later")
    public String observeTicketsForEventPresentationAndSeats(
	    @CliOption(key = { "event-presentation-id" }, mandatory = true,
		    help = "EventPresentation id to search available seats for") final int eventPresentationId,
	    @CliOption(key = { "seats" }, mandatory = true,
		    help = "Set of seat numbers to book tickets for") final Set<Integer> seats)
	    throws NotFoundException, TicketsAlreadyBookedException {
	EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
	List<Ticket> newTickets;
	if (authenticationState.isAuthenticated()) {
	    newTickets = ticketService.getNewTicketsForEventPresentationAndOwner(eventPresentation, seats,
		    authenticationState.getAuthenticatedUser());
	} else {
	    newTickets = ticketService.getNewTicketsForEventPresentation(eventPresentation, seats);
	}
	ticketState.setTickets(newTickets);
	return reviewTickets(eventPresentation, newTickets);
    }

    private String reviewTickets(EventPresentation eventPresentation, List<Ticket> newTickets) {
	return String.format("Ticket details for event %s[%s] which takes place on %s in %s auditorium:\r\n",
		eventPresentation.getEvent().getName(), eventPresentation.getEvent().getId(),
		eventPresentation.getAirDate(), eventPresentation.getAuditorium().getName()) +
		newTickets.stream()
			.map(ticket -> makeTicketRecord(ticket))
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
		+
		String.format("====> Total price <====> %s\r\n",
			formatMoney(newTickets.stream().mapToLong(Ticket::getTotalPriceInCents).sum()))
		+
		"If you'd like to make a purchase to finish your booking, please, invoke 'ticket book-confirm'. "
		+ "Otherwise invoke 'ticket book-discard'.";
    }

    private String formatMoney(long moneyInUsdCents) {
	long dollars = moneyInUsdCents / 100L;
	long cents = moneyInUsdCents % 100L;
	return dollars + "." + cents + "$";
    }

    private String makeTicketRecord(Ticket ticket) {
	return String.format("Seat %s, price: %s, discount: %s%%, total: %s\r\n",
		ticket.getSeatNumber(), formatMoney(ticket.getCalculatedPriceInCents()),
		ticket.getDiscountInPercent(), formatMoney(ticket.getTotalPriceInCents()));
    }

    @CliCommand(value = "ticket book-confirm",
	    help = "Confirm tickets booking, which was observed during the last 'ticket book' command invokation")
    public String bookTicketsFromState() throws TicketsAlreadyBookedException {
	long totalPriceInCents = ticketService
		.bookTickets(ticketState.getTickets().stream().peek(this::refreshOwner).collect(Collectors.toList()))
		.stream()
		.mapToLong(Ticket::getTotalPriceInCents).sum();
	ticketState.setTickets(null);
	return "Your payment of " + formatMoney(totalPriceInCents) + " was carried out successfully";
    }

    private void refreshOwner(Ticket ticket) {
	ticket.setOwner(authenticationState.getAuthenticatedUser());
    }

    @CliCommand(value = "ticket book-discard",
	    help = "Book tickets, which was observed during the last 'ticket book' command invokation")
    public String discardTicketsBookingFromState() {
	ticketState.setTickets(null);
	return "Your booking was discarded successfully";
    }

    @CliCommand(value = "ticket book-review",
	    help = "View tickets, which was selected during the last 'ticket book' command invokation")
    public String reviewTicketsBookingFromState() {
	List<Ticket> tickets = ticketState.getTickets();
	tickets.forEach(this::refreshOwner);
	return reviewTickets(tickets.get(0).getEventPresentation(), tickets);
    }
}
