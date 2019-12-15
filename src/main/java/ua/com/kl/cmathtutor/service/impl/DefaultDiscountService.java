package ua.com.kl.cmathtutor.service.impl;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.service.DiscountService;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

@Service
public class DefaultDiscountService implements DiscountService {

    @Autowired
    private Iterable<DiscountStrategy> discountStrategies;

    @Override
    public void applyDiscountToTickets(Collection<Ticket> tickets) {
	for (DiscountStrategy discountStrategy : discountStrategies) {
	    Collection<Double> discountsPercentForTickets = discountStrategy.getDiscountsPercentForTickets(tickets);
	    if (discountsPercentForTickets.size() != tickets.size()) {
		throw new IllegalArgumentException(
			"Dimensions of tickets and discountsPercentForTickets must be equal!");
	    }
	    Iterator<Ticket> ticketsIt = tickets.iterator();
	    Iterator<Double> discountsIt = discountsPercentForTickets.iterator();
	    while (ticketsIt.hasNext()) {
		Ticket ticket = ticketsIt.next();
		Double discountInPercent = discountsIt.next();
		if (ticket.getDiscountInPercent() < discountInPercent) {
		    ticket.setDiscountInPercent(discountInPercent);
		}
	    }
	}
	tickets.forEach(this::applyDiscountForTicket);
    }

    private void applyDiscountForTicket(Ticket ticket) {
	ticket.setTotalPriceInCents(ticket.getCalculatedPriceInCents()
		- (long) (ticket.getCalculatedPriceInCents() * ticket.getDiscountInPercent() / 100));
    }

}
