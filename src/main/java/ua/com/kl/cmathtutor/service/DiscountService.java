package ua.com.kl.cmathtutor.service;

import java.util.Collection;
import java.util.List;

import ua.com.kl.cmathtutor.domain.entity.Ticket;

public interface DiscountService {

    Long calculateTotalDiscount(Collection<Ticket> tickets);

    List<Ticket> applyDiscountToTickets(Collection<Ticket> tickets);
}
