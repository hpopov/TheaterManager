package ua.com.kl.cmathtutor.service.strategy.impl;

import java.util.Collection;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

@Slf4j
public abstract class AbstractDiscountStrategy implements DiscountStrategy {

    @Override
    public Collection<Double> getDiscountsPercentForTickets(Collection<Ticket> tickets) {
	long distinctOwnersCount = tickets.stream().map(Ticket::getOwner)
		.map(owner -> owner == null ? null : owner.getId()).distinct().count();
	if (distinctOwnersCount > 1L) {
	    throw new IllegalArgumentException("Tickets handled to DiscountStrategy should have the same owner!");
	}
	if (tickets.size() == 0) {
	    log.warn("Reveived tickets collection with size of 0. Probably something went wrong...");
	    return Collections.emptyList();
	}
	return getDiscountInPercentForSingleUserTickets(tickets.iterator().next().getOwner(), tickets);
    }

    protected abstract Collection<Double> getDiscountInPercentForSingleUserTickets(User owner,
	    Collection<Ticket> tickets);

}
