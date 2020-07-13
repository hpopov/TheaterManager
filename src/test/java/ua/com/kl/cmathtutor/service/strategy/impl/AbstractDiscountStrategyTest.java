package ua.com.kl.cmathtutor.service.strategy.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

abstract class AbstractDiscountStrategyTest {

    @ParameterizedTest
    @CsvSource(value = { "3", "0", "5" })
    void getDiscountsPercentForTickets_ShouldReturnSameDimensionOfDiscountsAsTickets(int dimension) {
        Collection<Ticket> tickets = Collections.nCopies(dimension, makeDummyTicket());

        Collection<Double> discounts = getDiscountStrategy().getDiscountsPercentForTickets(tickets);

        assertThat(tickets.size(), is(equalTo(discounts.size())));
    }

    protected abstract DiscountStrategy getDiscountStrategy();

    private Ticket makeDummyTicket() {
        return Ticket.builder().calculatedPriceInCents(123L).discountInPercent(1d).build();
    }

    @Test
    void whenTicketsHaveDifferentOwners_Then_getDiscountsPercentForTickets_ShouldThrowException() {
        Collection<Ticket> tickets = Lists.newArrayList(makeTicketWithOwnerId(1),
                makeTicketWithOwnerId(2));

        assertThrows(IllegalArgumentException.class,
                () -> getDiscountStrategy().getDiscountsPercentForTickets(tickets));
    }

    private Ticket makeTicketWithOwnerId(Integer id) {
        Ticket ticket = makeDummyTicket();
        ticket.setOwner(User.builder().id(id).build());
        return ticket;
    }
}
