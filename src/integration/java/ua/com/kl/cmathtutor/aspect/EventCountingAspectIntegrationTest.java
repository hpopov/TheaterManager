package ua.com.kl.cmathtutor.aspect;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ua.com.kl.cmathtutor.config.TicketCalculationConfigProperties;
import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventCounter;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.exception.TicketsAlreadyBookedException;
import ua.com.kl.cmathtutor.repository.EventCounterRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;
import ua.com.kl.cmathtutor.repository.TicketRepository;
import ua.com.kl.cmathtutor.service.DiscountService;
import ua.com.kl.cmathtutor.service.EventService;
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.service.UserService;
import ua.com.kl.cmathtutor.service.impl.DefaultEventService;
import ua.com.kl.cmathtutor.service.impl.DefaultTicketService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EventCountingAspectIntegrationTest.SpringContext.class)
class EventCountingAspectIntegrationTest {

    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventCounterRepository eventCounterRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;

    @DirtiesContext
    @Test
    void whenEventServiceGetById_isCalled_then_accessedCounter_shouldBeIncremented()
	    throws NotFoundException {
	Integer eventId = 5;
	Event event = Event.builder().id(eventId).baseTicketPriceInCents(534L).name("EventName").build();
	when(eventRepository.findById(any())).thenReturn(Optional.of(event));
	assertThat(eventCounterRepository.findByEventId(eventId).isPresent(), is(false));

	eventService.getById(eventId);

	Optional<EventCounter> eventCounterOptional = eventCounterRepository.findByEventId(eventId);
	assertThat(eventCounterOptional.isPresent(), is(true));
	EventCounter eventCounter = eventCounterOptional.get();
	assertThat(eventCounter.getAccessedCounter(), is(equalTo(1L)));
    }

    @DirtiesContext
    @Test
    void whenTicketServiceGetNewTicketsForEventPresentation_isCalled_then_priceQueriedCounter_shouldBeIncremented()
	    throws TicketsAlreadyBookedException {
	Integer eventId = 5;
	Integer eventPresentationId = 13;
	Set<Integer> seatNumbers = Sets.newHashSet(1, 3, 8);
	Event event = Event.builder().id(eventId).baseTicketPriceInCents(534L).name("EventName").build();
	Auditorium auditorium = Auditorium.builder().name("AuditoriumName").numberOfSeats(100)
		.vipSeats(Collections.emptySet()).build();
	EventPresentation eventPresentation = EventPresentation.builder().id(eventPresentationId).auditorium(auditorium)
		.event(event).build();
	when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
	when(ticketRepository.save(any())).thenAnswer(new ReturnsArgumentAt(0));
	when(eventRepository.findById(any())).thenReturn(Optional.of(event));
	assertThat(eventCounterRepository.findByEventId(eventId).isPresent(), is(false));

	ticketService.getNewTicketsForEventPresentation(eventPresentation, seatNumbers);

	Optional<EventCounter> eventCounterOptional = eventCounterRepository.findByEventId(eventId);
	assertThat(eventCounterOptional.isPresent(), is(true));
	EventCounter eventCounter = eventCounterOptional.get();
	assertThat(eventCounter.getPriceQueriedCounter(), is(equalTo(1L)));
    }

    @DirtiesContext
    @Test
    void whenTicketServiceBookTickets_isCalled_then_ticketsBookedCounter_shouldBeIncremented()
	    throws TicketsAlreadyBookedException {
	Integer eventId = 5;
	Integer eventPresentationId = 13;
	Integer seatNumber = 5;
	Event event = Event.builder().id(eventId).baseTicketPriceInCents(534L).name("EventName").build();
	Auditorium auditorium = Auditorium.builder().name("AuditoriumName").numberOfSeats(100)
		.vipSeats(Collections.emptySet()).build();
	EventPresentation eventPresentation = EventPresentation.builder().id(eventPresentationId).auditorium(auditorium)
		.event(event).build();
	Ticket ticket = Ticket.builder().eventPresentation(eventPresentation).seatNumber(seatNumber).build();
	when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
	when(ticketRepository.save(any())).thenAnswer(new ReturnsArgumentAt(0));
	when(eventRepository.findById(any())).thenReturn(Optional.of(event));
	assertThat(eventCounterRepository.findByEventId(eventId).isPresent(), is(false));

	ticketService.bookTickets(Lists.newArrayList(ticket));

	Optional<EventCounter> eventCounterOptional = eventCounterRepository.findByEventId(eventId);
	assertThat(eventCounterOptional.isPresent(), is(true));
	EventCounter eventCounter = eventCounterOptional.get();
	assertThat(eventCounter.getTicketsBookedCounter(), is(equalTo(1L)));
    }

    @Configuration
    static class SpringContext extends AspectIntegrationSpringContext {
	@Bean
	public EventService eventService() {
	    return new DefaultEventService(eventRepository());
	}

	@Bean
	public TicketService ticketService() {
	    return new DefaultTicketService(ticketRepository(), userService(), discountService(),
		    ticketCalculationProperties());
	}

	@Bean
	public TicketCalculationConfigProperties ticketCalculationProperties() {
	    return new TicketCalculationConfigProperties();
	}

	@Bean
	public TicketRepository ticketRepository() {
	    return Mockito.mock(TicketRepository.class);
	}

	@Bean
	public UserService userService() {
	    return Mockito.mock(UserService.class);
	}

	@Bean
	public DiscountService discountService() {
	    return Mockito.mock(DiscountService.class);
	}
    }
}
