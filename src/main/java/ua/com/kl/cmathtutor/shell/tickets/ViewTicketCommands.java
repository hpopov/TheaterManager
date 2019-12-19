package ua.com.kl.cmathtutor.shell.tickets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.shell.auth.AuthenticationState;

@Component
public class ViewTicketCommands implements CommandMarker {

    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private EventPresentationService eventPresentationService;

    @CliAvailabilityIndicator({ "ticket available" })
    public boolean isGetAvailableTicketsForEventPresentationCommandAvailable() {
	return true;
    }

    @CliAvailabilityIndicator({ "ticket purchased" })
    public boolean isGetPurchasedTicketsForEventPresentationCommandAvailable() {
	return authenticationState.isAdminAuthenticated();
    }

    @CliCommand(value = "ticket available",
	    help = "View list of all available seats for the specified event presentation")
    public String getAllAvailableTicketsForEventPresentation(
	    @CliOption(key = { "event-presentation-id" }, mandatory = true,
		    help = "EventPresentation id to search available seats for") final int eventPresentationId)
	    throws NotFoundException {
	EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
	return String.format("There are following available seats for event %s which takes place at %s:\r\n",
		eventPresentation.getEvent().getName(), eventPresentation.getAirDate()) +
		ticketService.getAvailableSeatsForEventPresentation(eventPresentation);
    }

    @CliCommand(value = "ticket purchased",
	    help = "View list of all purchased tickets for the specified event presentation")
    public String getAllPurchasedTicketsForEventPresentation(
	    @CliOption(key = { "event-presentation-id" }, mandatory = true,
		    help = "EventPresentation id to search purchased tickets for") final int eventPresentationId)
	    throws NotFoundException {
	EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
	String presentedTicketsAsString = ticketService.getPurchasedTicketsForEventPresentation(eventPresentation)
		.stream().map(Object::toString)
		.map(str -> str + "\r\n").collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
		.toString();
	return String.format("Following tickets were purchased for event %s which takes place at %s:\r\n",
		eventPresentation.getEvent().getName(), eventPresentation.getAirDate()) + presentedTicketsAsString;
    }
}
