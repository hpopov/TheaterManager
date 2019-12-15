package ua.com.kl.cmathtutor.service;

import java.util.Collection;

import ua.com.kl.cmathtutor.domain.entity.Ticket;

public interface DiscountService {

    void applyDiscountToTickets(Collection<Ticket> tickets);
}
