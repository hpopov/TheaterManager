package ua.com.kl.cmathtutor.shell.command.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.shell.command.ExceptionWrapperUtils;
import ua.com.kl.cmathtutor.shell.command.auth.AuthenticationState;

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
                help = "EventPresentation id to search available seats for") final int eventPresentationId
    ) {
        return ExceptionWrapperUtils.handleException(() -> {
            EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
            return String.format(
                    "There are following available seats for event '%s' which takes place in auditory '%s' on %s:%s",
                    eventPresentation.getEvent().getName(), eventPresentation.getAuditorium().getName(),
                    eventPresentation.getAirDate(), OsUtils.LINE_SEPARATOR) +
                    ticketService.getAvailableSeatsForEventPresentation(eventPresentation);
        });
    }

    @CliCommand(value = "ticket purchased",
        help = "View list of all purchased tickets for the specified event presentation [FOR ADMIN USAGE ONLY]")
    public String getAllPurchasedTicketsForEventPresentation(
            @CliOption(key = { "event-presentation-id" }, mandatory = true,
                help = "EventPresentation id to search purchased tickets for") final int eventPresentationId
    ) {
        return ExceptionWrapperUtils.handleException(() -> {
            EventPresentation eventPresentation = eventPresentationService.getById(eventPresentationId);
            String presentedTicketsAsString = ticketService.getPurchasedTicketsForEventPresentation(eventPresentation)
                    .stream().map(Object::toString)
                    .map(str -> str + OsUtils.LINE_SEPARATOR)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            return String.format("Following tickets were purchased for event %s which takes place at %s:%s",
                    eventPresentation.getEvent().getName(), eventPresentation.getAirDate(), OsUtils.LINE_SEPARATOR)
                    + presentedTicketsAsString;
        });
    }
}
