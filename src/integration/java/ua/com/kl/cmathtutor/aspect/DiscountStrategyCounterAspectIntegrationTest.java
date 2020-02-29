package ua.com.kl.cmathtutor.aspect;

import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.DiscountStrategyCounter;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.repository.DiscountStrategyCounterRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;
import ua.com.kl.cmathtutor.service.strategy.impl.BirthdayDiscountStrategy;
import ua.com.kl.cmathtutor.service.strategy.impl.EveryNthTicketDiscountStrategy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DiscountStrategyCounterAspectIntegrationTest.SpringContext.class)
class DiscountStrategyCounterAspectIntegrationTest {

    @Autowired
    DiscountStrategy birthdayDiscountStrategy;
    @Autowired
    DiscountStrategy everyNthTicketDiscountStrategy;
    @Autowired
    DiscountStrategyCounterRepository discountStrategyCounterRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void whenBirthdayDiscountStrategy_isApplied_then_DiscountStrategyCounter_shouldBeIncremented() {
	User owner = User.builder().birthdayDate(Calendar.getInstance().getTime()).build();
	List<Ticket> tickets = Lists.newArrayList(Ticket.builder().owner(owner).build(),
		Ticket.builder().owner(owner).build());
	when(userRepository.findById(any())).thenReturn(Optional.of(owner));
	Long counterBefore = getCounterValue(owner, birthdayDiscountStrategy);

	Collection<Double> discountsPercentForTickets = birthdayDiscountStrategy.getDiscountsPercentForTickets(tickets);

	assertThat("Verify that strategy has been applied", discountsPercentForTickets.stream().anyMatch(d -> d != 0));
	assertThat(getCounterValue(owner, birthdayDiscountStrategy), is(equalTo(counterBefore + 1)));
    }

    @Test
    void whenBirthdayDiscountStrategy_isNotApplied_then_DiscountStrategyCounter_shouldNotBeIncremented() {
	Calendar calendar = Calendar.getInstance();
	calendar.roll(Calendar.DATE, 50);
	User owner = User.builder().birthdayDate(calendar.getTime()).build();
	List<Ticket> tickets = Lists.newArrayList(Ticket.builder().owner(owner).build(),
		Ticket.builder().owner(owner).build());
	when(userRepository.findById(any())).thenReturn(Optional.of(owner));
	Long counterBefore = getCounterValue(owner, birthdayDiscountStrategy);

	Collection<Double> discountsPercentForTickets = birthdayDiscountStrategy.getDiscountsPercentForTickets(tickets);

	assertThat("Verify that strategy has NOT been applied",
		discountsPercentForTickets.stream().allMatch(d -> d.equals(Double.valueOf(0))));
	assertThat(getCounterValue(owner, birthdayDiscountStrategy), is(equalTo(counterBefore)));
    }

    @Test
    void whenEveryNthTicketDiscountStrategy_isApplied_then_DiscountStrategyCounter_shouldBeIncremented() {
	User owner = User.builder().birthdayDate(Calendar.getInstance().getTime()).build();
	List<Ticket> tickets = Collections.nCopies(15, Ticket.builder().owner(owner).build());
	when(userRepository.findById(any())).thenReturn(Optional.of(owner));
	Long counterBefore = getCounterValue(owner, everyNthTicketDiscountStrategy);

	Collection<Double> discountsPercentForTickets = everyNthTicketDiscountStrategy
		.getDiscountsPercentForTickets(tickets);

	assertThat("Verify that strategy has been applied", discountsPercentForTickets.stream().anyMatch(d -> d != 0));
	assertThat(getCounterValue(owner, everyNthTicketDiscountStrategy), is(equalTo(counterBefore + 1)));
    }

    @Test
    void whenEveryNthTicketDiscountStrategy_isNotApplied_then_DiscountStrategyCounter_shouldNotBeIncremented() {
	Calendar calendar = Calendar.getInstance();
	User owner = User.builder().birthdayDate(calendar.getTime()).build();
	List<Ticket> tickets = Lists.newArrayList(Ticket.builder().owner(owner).build(),
		Ticket.builder().owner(owner).build());
	when(userRepository.findById(any())).thenReturn(Optional.of(owner));
	Long counterBefore = getCounterValue(owner, everyNthTicketDiscountStrategy);

	Collection<Double> discountsPercentForTickets = everyNthTicketDiscountStrategy
		.getDiscountsPercentForTickets(tickets);

	assertThat("Verify that strategy has NOT been applied",
		discountsPercentForTickets.stream().allMatch(d -> d.equals(Double.valueOf(0))));
	assertThat(getCounterValue(owner, everyNthTicketDiscountStrategy), is(equalTo(counterBefore)));
    }

    private Long getCounterValue(User owner, DiscountStrategy discountStrategy) {
	return discountStrategyCounterRepository.findByOwnerAndStrategyName(owner,
		discountStrategy.getClass().getSimpleName()).map(DiscountStrategyCounter::getCounter)
		.orElse(Long.valueOf(0));
    }

    @Configuration
    public static class SpringContext extends AspectIntegrationSpringContext {

	@Bean
	public BirthdayDiscountStrategy birthdayDiscountStrategy() {
	    return new BirthdayDiscountStrategy();
	}

	@Bean
	public EveryNthTicketDiscountStrategy everyNthTicketDiscountStrategy() {
	    return new EveryNthTicketDiscountStrategy();
	}
    }

}
