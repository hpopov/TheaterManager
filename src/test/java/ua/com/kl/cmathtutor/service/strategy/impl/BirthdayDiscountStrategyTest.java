package ua.com.kl.cmathtutor.service.strategy.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

class BirthdayDiscountStrategyTest extends AbstractDiscountStrategyTest {

    BirthdayDiscountStrategy strategy;

    @BeforeEach
    void initialize() {
	strategy = new BirthdayDiscountStrategy();
    }

    @Test
    void whenOwnerIsAbsent_Then_getDiscountsPercentForTickets_ShouldReturnZero() {
	ArrayList<Ticket> tickets = Lists.newArrayList(new Ticket(), new Ticket());

	assertThat(strategy.getDiscountsPercentForTickets(tickets), contains(Collections.nCopies(2, 0d).toArray()));
    }

    @ParameterizedTest(name = "User with birthday at {0} should receive discount of 5% per each ticket")
    @MethodSource("getBirthdayDatesWithin5Days")
    void whenOwnerBirthdayIsWithin5Days_Then_getDiscountsPercentForTickets_ShouldReturnDiscount(Date birthdayDate) {
	List<Ticket> tickets = createTicketsWithOwnerBirthday(birthdayDate);

	assertThat(strategy.getDiscountsPercentForTickets(tickets), contains(Collections.nCopies(2, 5d).toArray()));
    }

    private static List<Date> getBirthdayDatesWithin5Days() {
	return Collections.nCopies(11, Calendar.getInstance()).stream().map(cal -> {
	    Calendar nowCalendar = (Calendar) cal.clone();
	    nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
	    nowCalendar.set(Calendar.MINUTE, 0);
	    nowCalendar.set(Calendar.SECOND, 0);
	    nowCalendar.set(Calendar.MILLISECOND, 0);
	    return nowCalendar;
	}).peek(new Consumer<Calendar>() {

	    private int shift = -5;

	    @Override
	    public void accept(Calendar t) {
		t.add(Calendar.DAY_OF_YEAR, shift);
		t.add(Calendar.YEAR, shift);
		++shift;
	    }

	}).map(Calendar::getTime).collect(Collectors.toList());
    }

    private List<Ticket> createTicketsWithOwnerBirthday(Date birthdayDate) {
	User owner = User.builder().birthdayDate(birthdayDate).build();
	return Lists.newArrayList(Ticket.builder().owner(owner).build(),
		Ticket.builder().owner(owner).build());
    }

    @Override
    protected DiscountStrategy getDiscountStrategy() {
	return strategy;
    }

}
