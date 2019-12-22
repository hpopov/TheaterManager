package ua.com.kl.cmathtutor.shell.tickets;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.exception.TicketsAlreadyBookedException;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.shell.auth.AuthenticationState;

@Component
public class BookTicketsCommands implements CommandMarker {
    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private EventPresentationService eventPresentationService;

    @CliAvailabilityIndicator({ "ticket book" })
    public boolean isBookTicketsCommandAvailable() {
	return true;
    }

    @CliCommand(value = "ticket book",
	    help = "Book tickets for the specified event presentation and seats in interactive mode")
    public String bookTicketsForEventPresentationAndSeats(
	    @CliOption(key = { "event-presentation-id" }, mandatory = true,
		    help = "EventPresentation id to search available seats for") final int eventPresentationId,
	    @CliOption(key = { "seats" }, mandatory = true,
		    help = "EventPresentation id to search available seats for") final int[] seats)
	    throws NotFoundException, TicketsAlreadyBookedException {
	EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
	Set<Integer> seatsSet = Sets.newHashSet();
	for (int i = 0; i < seats.length; i++) {
	    seatsSet.add(seats[i]);
	}
	List<Ticket> newTickets;
	if (authenticationState.isAuthenticated()) {
	    newTickets = ticketService.getNewTicketsForEventPresentationAndOwner(eventPresentation, seatsSet,
		    authenticationState.getAuthenticatedUser());
	} else {
	    newTickets = ticketService.getNewTicketsForEventPresentation(eventPresentation, seatsSet);
	}
	System.out.println(
		String.format("Ticket details for event %s[%s] which takes place on %s in %s auditorium:",
			eventPresentation.getEvent().getName(), eventPresentation.getEvent().getId(),
			eventPresentation.getAirDate(), eventPresentation.getAuditorium().getName()));
	System.out.print(newTickets.stream()
		.map(ticket -> String.format("Seat %s, price: %s, discount: %s%%, total: %s\r\n",
			ticket.getSeatNumber(), formatMoney(ticket.getCalculatedPriceInCents()),
			ticket.getDiscountInPercent(), formatMoney(ticket.getTotalPriceInCents())))
		.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));
	System.out.println(String.format("====> Total price <====> %s",
		formatMoney(newTickets.stream().mapToLong(Ticket::getTotalPriceInCents).sum())));
	System.out.print("Would you like to make a purchase[Y/N]?");
	Scanner sc = new Scanner(System.in);
	char answer = sc.next().charAt(0);
	sc.close();
	if (isAnswerPositive(answer)) {
	    long totalPriceInCents = ticketService.bookTickets(newTickets).stream()
		    .mapToLong(Ticket::getTotalPriceInCents).sum();
	    return "Your payment of " + formatMoney(totalPriceInCents) + " was carried out successfully";
	}
	return "Your booking was discarded successfully";
    }

    private String formatMoney(long moneyInUsdCents) {
	long dollars = moneyInUsdCents / 100L;
	long cents = moneyInUsdCents % 100L;
	return dollars + "." + cents + "$";
    }

    private boolean isAnswerPositive(char answer) {
	return answer == 'y' || answer == 'Y';
    }
}
