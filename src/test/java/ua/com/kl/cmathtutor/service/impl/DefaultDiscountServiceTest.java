package ua.com.kl.cmathtutor.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.service.impl.DefaultDiscountService;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

@ExtendWith(MockitoExtension.class)
class DefaultDiscountServiceTest {

    @Mock
    private DiscountStrategy discountStrategy1;
    @Mock
    private DiscountStrategy discountStrategy2;
    private List<Double> discounts1 = Lists.newArrayList(12d, 5d, 0d);
    private List<Double> discounts2 = Lists.newArrayList(1d, 5d, 4d);
    private List<Double> greatestDiscounts = Lists.newArrayList(12d, 5d, 4d);

    DefaultDiscountService service;

    @BeforeEach
    void initialize() {
	service = new DefaultDiscountService(Lists.newArrayList(discountStrategy1, discountStrategy2));
    }

    @Test
    void applyDiscount_ShouldInvokeAllStrategies() {
	ArrayList<Ticket> tickets = makeThreeDummyTickets();
	when(discountStrategy1.getDiscountsPercentForTickets(any())).thenReturn(discounts1);
	when(discountStrategy2.getDiscountsPercentForTickets(any())).thenReturn(discounts2);

	service.applyDiscountToTickets(tickets);

	verify(discountStrategy1).getDiscountsPercentForTickets(tickets);
	verify(discountStrategy2).getDiscountsPercentForTickets(tickets);
    }

    private ArrayList<Ticket> makeThreeDummyTickets() {
	return Lists.newArrayList(
		Ticket.builder()
			.discountInPercent(0d)
			.calculatedPriceInCents(123L)
			.build(),
		Ticket.builder()
			.discountInPercent(0d)
			.calculatedPriceInCents(452L)
			.build(),
		Ticket.builder()
			.discountInPercent(0d)
			.calculatedPriceInCents(9001L)
			.build());
    }

    @Test
    void whenDimensionsOfTicketsAndDiscountsDiffer_Then_applyDiscount_ShouldInvokeAllStrategies() {
	ArrayList<Ticket> tickets = Lists.newArrayList(new Ticket(), new Ticket());
	when(discountStrategy1.getDiscountsPercentForTickets(any())).thenReturn(discounts1);

	assertThrows(IllegalArgumentException.class, () -> service.applyDiscountToTickets(tickets));

	verify(discountStrategy1).getDiscountsPercentForTickets(tickets);
	verifyNoMoreInteractions(discountStrategy1, discountStrategy2);
    }

    @Test
    void applyDiscount_ShouldSelectMostDiscountForEachTicket() {

	ArrayList<Ticket> tickets = makeThreeDummyTickets();
	when(discountStrategy1.getDiscountsPercentForTickets(any())).thenReturn(discounts1);
	when(discountStrategy2.getDiscountsPercentForTickets(any())).thenReturn(discounts2);

	service.applyDiscountToTickets(tickets);
	List<Double> discountsInTickets = tickets.stream().sequential().map(Ticket::getDiscountInPercent)
		.collect(Collectors.toList());

	assertThat(discountsInTickets, contains(greatestDiscounts.toArray()));
    }

    @Test
    void applyDiscount_ShouldModifyEachTicket() {

	ArrayList<Ticket> tickets = makeThreeDummyTickets();
	when(discountStrategy1.getDiscountsPercentForTickets(any())).thenReturn(discounts1);
	when(discountStrategy2.getDiscountsPercentForTickets(any())).thenReturn(discounts2);

	service.applyDiscountToTickets(tickets);

	for (Ticket ticket : tickets) {
	    assertThat(ticket.getTotalPriceInCents(), is(equalTo(ticket.getCalculatedPriceInCents()
		    - (long) (ticket.getCalculatedPriceInCents() * ticket.getDiscountInPercent() / 100))));
	}
    }
}
