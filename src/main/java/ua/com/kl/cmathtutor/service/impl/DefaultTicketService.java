package ua.com.kl.cmathtutor.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import lombok.extern.slf4j.Slf4j;
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
import ua.com.kl.cmathtutor.service.TicketService;
import ua.com.kl.cmathtutor.service.UserService;

@Slf4j
@Service
public class DefaultTicketService implements TicketService {

    private TicketRepository ticketRepository;
    private UserService userService;
    private DiscountService discountService;
    private TicketCalculationConfigProperties ticketCalculationProperties;

    @Autowired
    public DefaultTicketService(TicketRepository ticketRepository, UserService userService,
	    DiscountService discountService, TicketCalculationConfigProperties ticketCalculationProperties) {
	this.ticketRepository = ticketRepository;
	this.userService = userService;
	this.discountService = discountService;
	this.ticketCalculationProperties = ticketCalculationProperties;
    }

    @Override
    public Set<Integer> getAvailableSeatsForEventPresentation(EventPresentation eventPresentation) {
	Set<Integer> bookedSeatNumbers = getPurchasedTicketsForEventPresentation(eventPresentation).stream()
		.map(Ticket::getSeatNumber).collect(Collectors.toSet());
	return IntStream.range(1, eventPresentation.getAuditorium().getNumberOfSeats() + 1).boxed()
		.filter(seat -> !bookedSeatNumbers.contains(seat)).collect(Collectors.toSet());
    }

    @Override
    public List<Ticket> getNewTicketsForEventPresentation(EventPresentation eventPresentation, Set<Integer> seatNumbers)
	    throws TicketsAlreadyBookedException {
	assertSpecifiedSeatsExistInAuditorium(seatNumbers, eventPresentation.getAuditorium());
	assertTicketsForSpecifiedSeatsAreNotBookedAlready(eventPresentation, seatNumbers);
	List<Ticket> tickets = seatNumbers.stream()
		.map(seat -> Ticket.builder().eventPresentation(eventPresentation).seatNumber(seat).build())
		.peek(this::calculateTicketPrice)
		.collect(Collectors.toList());
	discountService.applyDiscountToTickets(tickets);
	return tickets;
    }

    private void assertSpecifiedSeatsExistInAuditorium(Set<Integer> seatNumbers, Auditorium auditorium) {
	List<Integer> unexistedSeatNumbers = seatNumbers.stream().filter(seat -> seat > auditorium.getNumberOfSeats())
		.collect(Collectors.toList());
	if (unexistedSeatNumbers.size() > 0) {
	    throw new IllegalArgumentException(String.format("There are no seats within auditory '%s' with number %s",
		    auditorium.getName(), unexistedSeatNumbers));
	}
    }

    private void assertTicketsForSpecifiedSeatsAreNotBookedAlready(EventPresentation eventPresentation,
	    Set<Integer> seatNumbers) throws TicketsAlreadyBookedException {
	Set<Integer> occupiedSeatNumbers = getPurchasedTicketsForEventPresentation(eventPresentation).stream()
		.map(Ticket::getSeatNumber).filter(seatNumbers::contains).collect(Collectors.toSet());
	if (!occupiedSeatNumbers.isEmpty()) {
	    throw new TicketsAlreadyBookedException();
	}
    }

    private void calculateTicketPrice(Ticket ticket) {
	Event event = ticket.getEventPresentation().getEvent();
	long ticketPrice = event.getBaseTicketPriceInCents();
	Integer seatNumber = ticket.getSeatNumber();
	if (ticket.getEventPresentation().getAuditorium().getVipSeats().contains(seatNumber)) {
	    ticketPrice *= ticketCalculationProperties.vipSeatsPriceMultiplier();
	}
	if (event.getRating() == Rating.HIGH) {
	    ticketPrice *= ticketCalculationProperties.highRatedEventsPriceMultiplier();
	}
	ticket.setCalculatedPriceInCents(ticketPrice);
    }

    @Override
    public List<Ticket> getNewTicketsForEventPresentationAndOwner(EventPresentation eventPresentation,
	    Set<Integer> seatNumbers, User owner) throws TicketsAlreadyBookedException {
	assertSpecifiedSeatsExistInAuditorium(seatNumbers, eventPresentation.getAuditorium());
	assertTicketsForSpecifiedSeatsAreNotBookedAlready(eventPresentation, seatNumbers);
	List<Ticket> tickets = seatNumbers.stream()
		.map(seat -> Ticket.builder()
			.eventPresentation(eventPresentation)
			.seatNumber(seat)
			.owner(owner)
			.build())
		.peek(this::calculateTicketPrice)
		.collect(Collectors.toList());
	discountService.applyDiscountToTickets(tickets);
	return tickets;
    }

    @Override
    public List<Ticket> bookTickets(List<Ticket> tickets) throws TicketsAlreadyBookedException {
	HashMultimap<EventPresentation, Integer> seatNumbersByEventPresentation = HashMultimap.create();
	Multimap<User, Ticket> ticketsByOwner = LinkedHashMultimap.create();
	tickets.forEach(ticket -> {
	    seatNumbersByEventPresentation.put(ticket.getEventPresentation(), ticket.getSeatNumber());
	    ticketsByOwner.put(ticket.getOwner(), ticket);
	});
	for (EventPresentation key : seatNumbersByEventPresentation.keySet()) {
	    assertSpecifiedSeatsExistInAuditorium(seatNumbersByEventPresentation.get(key), key.getAuditorium());
	    assertTicketsForSpecifiedSeatsAreNotBookedAlready(key, seatNumbersByEventPresentation.get(key));
	}
	tickets.forEach(this::calculateTicketPrice);
	for (User owner : ticketsByOwner.keySet()) {
	    discountService.applyDiscountToTickets(ticketsByOwner.get(owner));
	}
	return tickets.stream().map(ticketRepository::save).peek(this::incrementPurchasedTicketsForOwner)
		.collect(Collectors.toList());
    }

    private void incrementPurchasedTicketsForOwner(Ticket ticket) {
	User owner = ticket.getOwner();
	if (Objects.isNull(owner)) {
	    return;
	}
	owner.setPurchasedTicketsNumber(owner.getPurchasedTicketsNumber() + 1);
	try {
	    userService.updateById(owner.getId(), owner);
	} catch (NotFoundException e) {
	    log.error("Owner for ticket with id {} was not found."
		    + " Owner's purchasedTickesNumber increment will not be performed", ticket.getId());
	}
    }

    @Override
    public List<Ticket> getPurchasedTicketsForEventPresentation(EventPresentation eventPresentation) {
	Integer eventPresentationId = eventPresentation.getId();
	return ticketRepository.findAll().stream()
		.filter(ticket -> eventPresentationId.equals(ticket.getEventPresentation().getId()))
		.collect(Collectors.toList());
    }

}
