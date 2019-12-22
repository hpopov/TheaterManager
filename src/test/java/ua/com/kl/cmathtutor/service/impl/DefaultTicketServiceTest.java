package ua.com.kl.cmathtutor.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ua.com.kl.cmathtutor.config.TicketCalculationConfigProperties;
import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.domain.entity.Event;
import ua.com.kl.cmathtutor.domain.entity.EventPresentation;
import ua.com.kl.cmathtutor.domain.entity.Rating;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.exception.TicketsAlreadyBookedException;
import ua.com.kl.cmathtutor.repository.TicketRepository;
import ua.com.kl.cmathtutor.service.DiscountService;
import ua.com.kl.cmathtutor.service.UserService;

@ExtendWith(MockitoExtension.class)
class DefaultTicketServiceTest {

    private static final HashSet<Integer> VIP_SEATS = Sets.newHashSet(1, 2, 3);
    private static final long HIGH_RATED_EVENTS_PRICE_MUL = 3L;
    private static final long VIP_SEATS_PRICE_MUL = 2L;
    private static final long BASE_TICKET_PRICE = 100L;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserService userService;
    @Mock
    private DiscountService discountService;
    @Mock
    private TicketCalculationConfigProperties ticketCalculationProperties;

    DefaultTicketService service;

    @BeforeEach
    void setUp() {
	service = new DefaultTicketService(ticketRepository, userService, discountService, ticketCalculationProperties);
    }

    @Test
    void getAvailableSeatsForEventPresentation_ShouldReturnSeatsWithoutBookedTicketsFor() {
	Auditorium auditorium = createAuditorium();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	when(ticketRepository.findAll())
		.thenReturn(makeTicketsForEventPresentationPlusOneNotRelated(eventPresentation));

	Set<Integer> givenSeats = service.getAvailableSeatsForEventPresentation(eventPresentation);

	assertThat(givenSeats, containsInAnyOrder(Sets.newHashSet(1, 3, 4, 6, 8, 9).toArray()));
	verify(ticketRepository, only()).findAll();
    }

    private EventPresentation createEventPresentation(Auditorium auditorium) {
	return EventPresentation.builder()
		.id(1)
		.auditorium(auditorium)
		.build();
    }

    private Auditorium createAuditorium() {
	return Auditorium.builder()
		.name("a1")
		.numberOfSeats(9)
		.vipSeats(VIP_SEATS)
		.build();
    }

    private List<Ticket> makeTicketsForEventPresentationPlusOneNotRelated(EventPresentation eventPresentation) {
	return Lists.newArrayList(
		Ticket.builder()
			.eventPresentation(eventPresentation)
			.isBooked(true)
			.seatNumber(2)
			.build(),
		Ticket.builder()
			.eventPresentation(eventPresentation)
			.isBooked(true)
			.seatNumber(5)
			.build(),
		Ticket.builder()
			.eventPresentation(eventPresentation)
			.isBooked(true)
			.seatNumber(7)
			.build(),
		Ticket.builder()
			.eventPresentation(EventPresentation.builder().id(111).build())
			.isBooked(true)
			.seatNumber(3)
			.build());
    }

    @ParameterizedTest
    @MethodSource("eventRatingWithValidSeatNumbersAndPricesSource")
    void whenSpecifiedSeatsAreNotBooked_Then_getNewTicketsForEventPresentation_ShouldReturnNewTickets(
	    Rating eventRating, Iterable<Integer> seatNumbersOrdered,
	    Collection<Long> ticketCalculatedPrices)
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").rating(eventRating)
		.build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(seatNumbersOrdered);
	when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
	when(ticketCalculationProperties.vipSeatsPriceMultiplier()).thenReturn((double) VIP_SEATS_PRICE_MUL);
	if (eventRating == Rating.HIGH) {
	    when(ticketCalculationProperties.highRatedEventsPriceMultiplier())
		    .thenReturn((double) HIGH_RATED_EVENTS_PRICE_MUL);
	}
	doNothing().when(discountService).applyDiscountToTickets(any());

	List<Ticket> newTickets = service.getNewTicketsForEventPresentation(eventPresentation, seatNumbers);

	assertAll(() -> assertThat(newTickets.stream().map(Ticket::getSeatNumber).collect(Collectors.toList()),
		contains(seatNumbers.toArray())),
		() -> assertThat(
			newTickets.stream().map(Ticket::getCalculatedPriceInCents).collect(Collectors.toList()),
			contains(ticketCalculatedPrices.toArray())));
	verify(ticketRepository, only()).findAll();
	verify(discountService, only()).applyDiscountToTickets(newTickets);
	verify(ticketCalculationProperties, atLeastOnce()).vipSeatsPriceMultiplier();
	if (eventRating == Rating.HIGH) {
	    verify(ticketCalculationProperties, atLeastOnce()).highRatedEventsPriceMultiplier();
	}
    }

    static List<Arguments> eventRatingWithValidSeatNumbersAndPricesSource() {
	return Lists.newArrayList(
		Arguments.of(Rating.LOW, Lists.newArrayList(2, 5, 8), Lists.newArrayList(
			BASE_TICKET_PRICE * VIP_SEATS_PRICE_MUL, BASE_TICKET_PRICE, BASE_TICKET_PRICE)),
		Arguments.of(Rating.MID, Lists.newArrayList(1, 3, 4, 6, 7, 8, 9), Lists.newArrayList(
			BASE_TICKET_PRICE * VIP_SEATS_PRICE_MUL, BASE_TICKET_PRICE * VIP_SEATS_PRICE_MUL,
			BASE_TICKET_PRICE,
			BASE_TICKET_PRICE, BASE_TICKET_PRICE, BASE_TICKET_PRICE, BASE_TICKET_PRICE)),
		Arguments.of(Rating.HIGH, Lists.newArrayList(2, 5, 7, 8), Lists.newArrayList(
			BASE_TICKET_PRICE * VIP_SEATS_PRICE_MUL * HIGH_RATED_EVENTS_PRICE_MUL,
			BASE_TICKET_PRICE * HIGH_RATED_EVENTS_PRICE_MUL,
			BASE_TICKET_PRICE * HIGH_RATED_EVENTS_PRICE_MUL,
			BASE_TICKET_PRICE * HIGH_RATED_EVENTS_PRICE_MUL)));
    }

    @Test
    void whenSpecifiedSeatsNotExist_Then_getNewTicketsForEventPresentation_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(Lists.newArrayList(1, 4, 11));

	assertThrows(IllegalArgumentException.class,
		() -> service.getNewTicketsForEventPresentation(eventPresentation, seatNumbers));
	verifyZeroInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @Test
    void whenSpecifiedSeatsAreBooked_Then_getNewTicketsForEventPresentation_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(Lists.newArrayList(2, 4, 6, 8));
	when(ticketRepository.findAll())
		.thenReturn(makeTicketsForEventPresentationPlusOneNotRelated(eventPresentation));

	assertThrows(TicketsAlreadyBookedException.class,
		() -> service.getNewTicketsForEventPresentation(eventPresentation, seatNumbers));
	verify(ticketRepository, only()).findAll();
	verifyNoMoreInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @ParameterizedTest
    @MethodSource("eventRatingWithValidSeatNumbersAndPricesSource")
    void whenSpecifiedSeatsAreNotBooked_Then_getNewTicketsForEventPresentationAndOwner_ShouldReturnNewTickets(
	    Rating eventRating, Iterable<Integer> seatNumbersOrdered,
	    Collection<Long> ticketCalculatedPrices)
	    throws TicketsAlreadyBookedException {
	User owner = new User();
	Auditorium auditorium = createAuditorium();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").rating(eventRating)
		.build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(seatNumbersOrdered);
	when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
	when(ticketCalculationProperties.vipSeatsPriceMultiplier()).thenReturn((double) VIP_SEATS_PRICE_MUL);
	if (eventRating == Rating.HIGH) {
	    when(ticketCalculationProperties.highRatedEventsPriceMultiplier())
		    .thenReturn((double) HIGH_RATED_EVENTS_PRICE_MUL);
	}
	doNothing().when(discountService).applyDiscountToTickets(any());

	List<Ticket> newTickets = service.getNewTicketsForEventPresentationAndOwner(eventPresentation, seatNumbers,
		owner);

	assertAll(
		() -> assertThat((int) newTickets.stream().filter(ticket -> owner.equals(ticket.getOwner())).count(),
			is(equalTo(newTickets.size()))),
		() -> assertThat(newTickets.stream().map(Ticket::getSeatNumber).collect(Collectors.toList()),
			contains(seatNumbers.toArray())),
		() -> assertThat(
			newTickets.stream().map(Ticket::getCalculatedPriceInCents).collect(Collectors.toList()),
			contains(ticketCalculatedPrices.toArray())));
	verify(ticketRepository, only()).findAll();
	verify(discountService, only()).applyDiscountToTickets(newTickets);
	verify(ticketCalculationProperties, atLeastOnce()).vipSeatsPriceMultiplier();
	if (eventRating == Rating.HIGH) {
	    verify(ticketCalculationProperties, atLeastOnce()).highRatedEventsPriceMultiplier();
	}
    }

    @Test
    void whenSpecifiedSeatsNotExist_Then_getNewTicketsForEventPresentationAndOwner_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(Lists.newArrayList(1, 4, 11));

	assertThrows(IllegalArgumentException.class,
		() -> service.getNewTicketsForEventPresentationAndOwner(eventPresentation, seatNumbers, null));
	verifyZeroInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @Test
    void whenSpecifiedSeatsAreBooked_Then_getNewTicketsForEventPresentationAndOwner_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(Lists.newArrayList(2, 4, 6, 8));
	when(ticketRepository.findAll())
		.thenReturn(makeTicketsForEventPresentationPlusOneNotRelated(eventPresentation));

	assertThrows(TicketsAlreadyBookedException.class,
		() -> service.getNewTicketsForEventPresentationAndOwner(eventPresentation, seatNumbers, null));
	verify(ticketRepository, only()).findAll();
	verifyNoMoreInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @ParameterizedTest
    @MethodSource("eventRatingWithValidSeatNumbersAndPricesSource")
    void whenSpecifiedSeatsAreNotBooked_Then_bookTickets_ShouldReturnNewTickets(Rating eventRating,
	    List<Integer> seatNumbersOrdered,
	    List<Long> ticketCalculatedPrices)
	    throws TicketsAlreadyBookedException, NotFoundException {
	Auditorium auditorium = createAuditorium();
	int userId = 1;
	User owner = User.builder().id(userId).build();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").rating(eventRating)
		.build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	Set<Integer> seatNumbers = Sets.newLinkedHashSet(seatNumbersOrdered);
	List<Ticket> newTickets = new ArrayList<>(seatNumbers.size());
	for (int i = 0; i < seatNumbers.size(); i++) {
	    Ticket ticket = Ticket.builder()
		    .eventPresentation(eventPresentation).seatNumber(seatNumbersOrdered.get(i)).build();
	    if (i == 0) {
		ticket.setOwner(owner);
	    }
	    newTickets.add(ticket);
	}
	when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
	when(ticketCalculationProperties.vipSeatsPriceMultiplier()).thenReturn((double) VIP_SEATS_PRICE_MUL);
	if (eventRating == Rating.HIGH) {
	    when(ticketCalculationProperties.highRatedEventsPriceMultiplier())
		    .thenReturn((double) HIGH_RATED_EVENTS_PRICE_MUL);
	}
	doNothing().when(discountService).applyDiscountToTickets(any());
	when(ticketRepository.save(any())).then(new ReturnsArgumentAt(0));
	when(userService.updateById(any(), any())).thenReturn(owner);

	List<Ticket> bookedTickets = service.bookTickets(newTickets);

	assertAll(
		() -> assertThat(owner.getPurchasedTicketsNumber(), is(equalTo(1))),
		() -> assertThat(bookedTickets.stream().map(Ticket::getSeatNumber).collect(Collectors.toList()),
			contains(seatNumbers.toArray())),
		() -> assertThat(
			bookedTickets.stream().map(Ticket::getCalculatedPriceInCents).collect(Collectors.toList()),
			contains(ticketCalculatedPrices.toArray())));
	verify(userService).updateById(userId, owner);
	verify(ticketRepository).findAll();
//	verify(discountService, only()).applyDiscountToTickets(newTickets); TODO: question 2)
	verify(ticketCalculationProperties, atLeastOnce()).vipSeatsPriceMultiplier();
	if (eventRating == Rating.HIGH) {
	    verify(ticketCalculationProperties, atLeastOnce()).highRatedEventsPriceMultiplier();
	}
	newTickets.forEach(ticket -> verify(ticketRepository).save(ticket));
    }

    @Test
    void whenSpecifiedSeatsNotExist_Then_bookTickets_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	Integer unexistedSeatNumber = 25;
	List<Ticket> tickets = Lists.newArrayList(
		Ticket.builder().seatNumber(unexistedSeatNumber).eventPresentation(eventPresentation).build());

	assertThrows(IllegalArgumentException.class,
		() -> service.bookTickets(tickets));
	verifyZeroInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @Test
    void whenSpecifiedSeatsAreBooked_Then_bookTickets_ShouldThrowException()
	    throws TicketsAlreadyBookedException {
	Auditorium auditorium = createAuditorium();
	Event event = Event.builder().baseTicketPriceInCents(BASE_TICKET_PRICE).name("Name").build();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	eventPresentation.setEvent(event);
	List<Ticket> ticketsToBook = makeTicketsForEventPresentationPlusOneNotRelated(eventPresentation);
	when(ticketRepository.findAll()).thenReturn(ticketsToBook);

	assertThrows(TicketsAlreadyBookedException.class,
		() -> service.bookTickets(ticketsToBook));
	verify(ticketRepository, only()).findAll();
	verifyNoMoreInteractions(ticketRepository, ticketCalculationProperties, discountService);
    }

    @Test
    void getPurchasedTicketsForEventPresentation_ShouldReturnAllTicketsForSpecifiedEventPresentation() {
	Auditorium auditorium = createAuditorium();
	EventPresentation eventPresentation = createEventPresentation(auditorium);
	List<Ticket> allExistedTickets = makeTicketsForEventPresentationPlusOneNotRelated(eventPresentation);
	when(ticketRepository.findAll()).thenReturn(allExistedTickets);

	List<Ticket> purchasedTicketsForEventPresentation = service
		.getPurchasedTicketsForEventPresentation(eventPresentation);

	assertThat(purchasedTicketsForEventPresentation, containsInAnyOrder(allExistedTickets.stream()
		.filter(ticket -> eventPresentation.equals(ticket.getEventPresentation())).toArray()));
	verify(ticketRepository, only()).findAll();
    }
}
