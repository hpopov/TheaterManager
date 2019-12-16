package ua.com.kl.cmathtutor.shell.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Rating;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.AuditoriumService;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.EventService;
import ua.com.kl.cmathtutor.shell.auth.AuthenticationState;

@Component
public class EventCommands implements CommandMarker {

    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private EventPresentationService eventPresentationService;
    @Autowired
    private EventService eventService;
    @Autowired
    private AuditoriumService auditoriumService;

    @CliAvailabilityIndicator({ "event all-presentations", "event all" })
    public boolean isGetAllPresentationsAvailable() {
	return true;
    }

    @CliAvailabilityIndicator({ "event place", "event present" })
    public boolean isAdminEventCommandsAvailable() {
	return authenticationState.isAdminAuthenticated();
    }

    @CliCommand(value = "event all-presentations", help = "View list of all available event presentations")
    public String getAllEventPresentations() {
	return "Currently available presentations are:\r\n" +
		eventPresentationService.getAll().stream()
			.map(ep -> "EventPresentation" + "[" + ep.getId() + "]: event '" + ep.getEvent().getName()
				+ "'[" + ep.getEvent().getId() + "] airDate: " + ep.getAirDate() + " auditorium "
				+ ep.getAuditorium().getName())
			.map(str -> str + "\r\n").collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "event all", help = "View list of all available events")
    public String getAllEvents() {
	return "Currently available events are:\r\n" +
		eventService.getAll().stream()
			.map(Object::toString)
			.map(str -> str + "\r\n").collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "event place", help = "Place new Event")
    public Event placeEvent(
	    @CliOption(key = { "name" }, mandatory = true, help = "First name of the user") final String name,
	    @CliOption(key = {
		    "baseTicketPriceInCents" }, mandatory = false, help = "Base price of Ticket [US cents]") final long baseTicketPriceInCents,
	    @CliOption(key = { "rating" }, mandatory = true, help = "Ratinge of the event") final Rating rating) {
	return eventService.create(
		Event.builder().baseTicketPriceInCents(baseTicketPriceInCents).name(name).rating(rating).build());
    }

    @CliCommand(value = "event present", help = "Create event presentation for specified event id")
    public EventPresentation createEventPresentation(
	    @CliOption(key = { "eventId" }, mandatory = true, help = "Id of the event to present") final int eventId,
	    @CliOption(key = {
		    "auditorium" }, mandatory = true, help = "Name of the auditorium") final String auditoriumName,
	    @CliOption(key = {
		    "airDate" }, mandatory = true, help = "Air date in dd-mm-yyyy format") final Date airDate,
	    @CliOption(key = {
		    "durationInMilliseconds" }, mandatory = true, help = "Duration of presentation in millis. Default is 1 hour", specifiedDefaultValue = "36000000") final long durationInMilliseconds)
	    throws NotFoundException {
	Auditorium auditorium = auditoriumService.getByName(auditoriumName);
	Event event = eventService.getById(eventId);
	return eventPresentationService.create(EventPresentation.builder().airDate(airDate).auditorium(auditorium)
		.durationInMilliseconds(durationInMilliseconds).event(event).build());
    }
}
