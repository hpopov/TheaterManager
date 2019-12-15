package ua.com.kl.cmathtutor.service.strategy;

import java.util.Collection;

import ua.com.kl.cmathtutor.domain.entity.Ticket;

public interface DiscountStrategy {

    Collection<Double> getDiscountsPercentForTickets(Collection<Ticket> tickets);
}
