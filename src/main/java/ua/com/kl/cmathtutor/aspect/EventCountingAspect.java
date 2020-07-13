package ua.com.kl.cmathtutor.aspect;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventCounter;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.repository.EventCounterRepository;

@Aspect
@Component
@Slf4j
public class EventCountingAspect {

    private EventCounterRepository eventCounterRepository;

    @Autowired
    public EventCountingAspect(EventCounterRepository eventCounterRepository) {
        this.eventCounterRepository = eventCounterRepository;
    }

    @Pointcut("execution(ua.com.kl.cmathtutor.domain.entity.Event "
            + "ua.com.kl.cmathtutor.service.*.getById(..) throws *)")
    private void getByIdMethod() {
    }

    @Pointcut("this(ua.com.kl.cmathtutor.service.EventService+)")
    private void withinEventServiceSuccessors() {
    }

    @Pointcut("getByIdMethod() && withinEventServiceSuccessors()")
    private void eventServiceGetByIdMethod() {
    }

    @AfterReturning(pointcut = "eventServiceGetByIdMethod()", returning = "event")
    public void countEventQueriesByName(Event event) {
        EventCounter eventCounter = resolveEventCounter(event.getId());
        eventCounter.setAccessedCounter(eventCounter.getAccessedCounter() + 1);
        eventCounterRepository.save(eventCounter);
    }

    private EventCounter resolveEventCounter(Integer eventId) {
        return eventCounterRepository.findByEventId(eventId)
                .orElseGet(() -> EventCounter.builder().eventId(eventId).build());
    }

    @Pointcut("this(ua.com.kl.cmathtutor.service.TicketService+)")
    private void withinTicketServiceSuccessors() {
    }

    @Pointcut("execution(* ua.com.kl.cmathtutor.service.*.*.getNewTicketsForEventPresentation(..) throws *)"
            + " && args(eventPresentation, seatNumbers)")
    private void getNewTicketsForEventPresentationMethod(
            EventPresentation eventPresentation,
            Set<Integer> seatNumbers
    ) {
    }

    @Pointcut("withinTicketServiceSuccessors() && getNewTicketsForEventPresentationMethod(eventPresentation, seatNumbers)")
    private void getNewTicketsWithinTicketService(
            EventPresentation eventPresentation,
            Set<Integer> seatNumbers
    ) {
    }

    @AfterReturning(pointcut = "getNewTicketsWithinTicketService(ep, seatNumbers)")
    private void countQueryingOfEventBasePrice(EventPresentation ep, Set<Integer> seatNumbers)
            throws Throwable {
        EventCounter eventCounter = resolveEventCounter(ep.getEvent().getId());
        eventCounter.setPriceQueriedCounter(eventCounter.getPriceQueriedCounter() + 1);
        eventCounterRepository.save(eventCounter);
    }

    @Pointcut("execution(java.util.List<ua.com.kl.cmathtutor.domain.entity.Ticket> "
            + "ua.com.kl.cmathtutor.service.*.*.bookTickets(..))")
    private void bookTickets() {
    }

    @AfterReturning(pointcut = "withinTicketServiceSuccessors() && bookTickets()", returning = "tickets")
    private void countTimesWhenTicketsAreBookedForEvent(List<Ticket> tickets) {
        Set<Integer> eventIds = tickets.stream().map(Ticket::getEventPresentation).map(EventPresentation::getEvent)
                .map(Event::getId).collect(Collectors.toSet());
        if (eventIds.remove(null)) {
            log.warn("Some event presentations among booked tickets don't refer to saved Event!");
        }
        eventIds.forEach(this::incrementTicketsBookedCounterForEventId);
    }

    private void incrementTicketsBookedCounterForEventId(Integer eventId) {
        EventCounter eventCounter = resolveEventCounter(eventId);
        eventCounter.setTicketsBookedCounter(eventCounter.getTicketsBookedCounter() + 1);
        eventCounterRepository.save(eventCounter);
    }
}
