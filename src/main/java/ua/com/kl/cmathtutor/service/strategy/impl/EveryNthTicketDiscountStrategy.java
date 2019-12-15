
package ua.com.kl.cmathtutor.service.strategy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;

@Component
public class EveryNthTicketDiscountStrategy extends AbstractDiscountStrategy {

    private int n = 10;
    private double nthTicketDiscountInPercent = 50;

    @Override
    protected Collection<Double> getDiscountInPercentForSingleUserTickets(User owner, Collection<Ticket> tickets) {
	int alreadyBoughtWithoutDiscountTicketsNumber = 0;
	if (Objects.nonNull(owner)) {
	    alreadyBoughtWithoutDiscountTicketsNumber = owner.getPurchasedTicketsNumber() % n;
	}
	List<Double> discountsInPercent = new ArrayList<>(tickets.size());
	for (int i = 0; i < tickets.size(); ++i) {
	    if ((i + alreadyBoughtWithoutDiscountTicketsNumber) % n == 0) {
		discountsInPercent.add(nthTicketDiscountInPercent);
	    } else {
		discountsInPercent.add(0d);
	    }
	}
	return discountsInPercent;
    }

}