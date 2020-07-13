package ua.com.kl.cmathtutor.service.strategy.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

class EveryNthTicketDiscountStrategyTest extends AbstractDiscountStrategyTest {

    EveryNthTicketDiscountStrategy strategy;

    @BeforeEach
    void initialize() {
        strategy = new EveryNthTicketDiscountStrategy();
    }

    @Override
    protected DiscountStrategy getDiscountStrategy() {
        return strategy;
    }

    @Test
    void whenUserIsAbsent_Then_getDiscountsPercentForTickets_ShouldCalculateDiscountForEach10thTicket() {
        List<Ticket> tickets = Collections.nCopies(45, null).stream().map(o -> new Ticket())
                .collect(Collectors.toList());
        Double[] expectedDiscounts = new Double[tickets.size()];
        for (int i = 0; i < tickets.size(); i++) {
            expectedDiscounts[i] = (i + 1) % 10 == 0 ? 50d : 0d;
        }

        Collection<Double> discounts = strategy.getDiscountsPercentForTickets(tickets);

        assertThat(discounts, contains(expectedDiscounts));
    }

    @ParameterizedTest
    @CsvSource({ "0", "11", "20", "43", "57", "89", "95", "74" })
    void whenUserIsPresent_Then_getDiscountsPercentForTickets_ShouldCalculateDiscountCountingPurchasedTickets(
            Integer purchasedTicketsNumber
    ) {
        User owner = User.builder().id(1).purchasedTicketsNumber(purchasedTicketsNumber).build();
        List<Ticket> tickets = Collections.nCopies(45, null).stream().map(o -> Ticket.builder().owner(owner).build())
                .collect(Collectors.toList());
        int uncountedPurchasedTicketsNumber = purchasedTicketsNumber % 10;
        Double[] expectedDiscounts = new Double[tickets.size()];
        for (int i = 0; i < tickets.size(); i++) {
            expectedDiscounts[i] = (uncountedPurchasedTicketsNumber + i + 1) % 10 == 0 ? 50d : 0d;
        }

        Collection<Double> discounts = strategy.getDiscountsPercentForTickets(tickets);

        assertThat(discounts, contains(expectedDiscounts));
    }
}
