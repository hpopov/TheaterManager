package ua.com.kl.cmathtutor.shell.command.event;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Rating;
import ua.com.kl.cmathtutor.service.AuditoriumService;
import ua.com.kl.cmathtutor.service.EventPresentationService;
import ua.com.kl.cmathtutor.service.EventService;
import ua.com.kl.cmathtutor.shell.command.ExceptionWrapperUtils;
import ua.com.kl.cmathtutor.shell.command.auth.AuthenticationState;
import ua.com.kl.cmathtutor.shell.converter.DateTimeConverter;
import ua.com.kl.cmathtutor.shell.converter.DurationConverter;
import ua.com.kl.cmathtutor.shell.type.DateTime;
import ua.com.kl.cmathtutor.shell.type.Duration;

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

    @PostConstruct
    private void init() {
	Event sherlockHolmesEvent = eventService.create(Event.builder()
		.baseTicketPriceInCents(10000L)
		.name("Sherlock Holmes & Dr Watson")
		.rating(Rating.HIGH)
		.build());
	eventPresentationService.create(EventPresentation.builder()
		.airDate(new Date())
		.auditorium(auditoriumService.getAll().get(0))
		.durationInMilliseconds(5400000L)
		.event(sherlockHolmesEvent)
		.build());
    }

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
	return "Currently available presentations are:" + OsUtils.LINE_SEPARATOR
		+ eventPresentationService.getAll().stream()
			.map(ep -> String.format(
				"EventPresentation[%s]: event: '%s'[%s], airDate: %s, duration: %sh %sm, auditorium: '%s'",
				ep.getId(), ep.getEvent().getName(), ep.getEvent().getId(), ep.getAirDate(),
				ep.getDurationInMilliseconds() / DurationConverter.MILLIESECONDS_PER_HOUR,
				(ep.getDurationInMilliseconds() % DurationConverter.MILLIESECONDS_PER_HOUR)
					/ DurationConverter.MILLIESECONDS_PER_MINUTE,
				ep.getAuditorium().getName()))
			.map(str -> str + OsUtils.LINE_SEPARATOR).collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "event all", help = "View list of all available events")
    public String getAllEvents() {
	return "Currently available events are:" + OsUtils.LINE_SEPARATOR +
		eventService.getAll().stream()
			.map(Object::toString)
			.map(str -> str + OsUtils.LINE_SEPARATOR).collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "event place", help = "Place new Event [FOR ADMIN USAGE ONLY]")
    public Event placeEvent(
	    @CliOption(key = { "name" }, mandatory = true, help = "First name of the user") final String name,
	    @CliOption(key = {
		    "baseTicketPriceInCents" }, mandatory = false,
		    help = "Base price of Ticket [US cents]") final long baseTicketPriceInCents,
	    @CliOption(key = { "rating" }, mandatory = true, help = "Ratinge of the event") final Rating rating) {
	return eventService.create(
		Event.builder().baseTicketPriceInCents(baseTicketPriceInCents).name(name).rating(rating).build());
    }

    @CliCommand(value = "event present",
	    help = "Create event presentation for specified event id [FOR ADMIN USAGE ONLY]")
    public String createEventPresentation(
	    @CliOption(key = { "eventId" }, mandatory = true, help = "Id of the event to present") final int eventId,
	    @CliOption(key = {
		    "auditorium" }, mandatory = true, help = "Name of the auditorium") final String auditoriumName,
	    @CliOption(key = {
		    "airDate" }, mandatory = true,
		    help = "Air date in '" + DateTimeConverter.FORMAT + "' format") final DateTime airDateTime,
	    @CliOption(key = {
		    "duration" }, mandatory = false,
		    help = "Duration of presentation in format'" + DurationConverter.FORMAT + "'. Default is 1 hour",
		    unspecifiedDefaultValue = "01:00") final Duration duration) {
	return ExceptionWrapperUtils.handleException(() -> {
	    Auditorium auditorium = auditoriumService.getByName(auditoriumName);
	    Event event = eventService.getById(eventId);
	    return eventPresentationService
		    .create(EventPresentation.builder().airDate(airDateTime.getDate()).auditorium(auditorium)
			    .durationInMilliseconds(duration.getDurationInMilliseconds()).event(event).build())
		    .toString();
	});
    }
}
