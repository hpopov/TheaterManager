package ua.com.kl.cmathtutor.aspect;

import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.DiscountStrategyCounter;
import ua.com.kl.cmathtutor.domain.entity.Ticket;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.repository.DiscountStrategyCounterRepository;

@Aspect
@Component
public class DiscountStrategyCounterAspect {

    private DiscountStrategyCounterRepository discountStrategyCounterRepository;

    @Autowired
    private DiscountStrategyCounterAspect(DiscountStrategyCounterRepository discountStrategyCounterRepository) {
        this.discountStrategyCounterRepository = discountStrategyCounterRepository;
    }

    @Pointcut("execution(java.util.Collection<java.lang.Double> ua.com.kl.cmathtutor.service.strategy*.*." +
            "getDiscountsPercentForTickets(..)) && args(tickets)")
    private void getDiscountsPercentForTickets(Collection<Ticket> tickets) {
    }

    @Pointcut("this(ua.com.kl.cmathtutor.service.strategy.DiscountStrategy+)")
    private void withinDiscountStrategy() {
    }

    @SuppressWarnings("unchecked")
    @Around("getDiscountsPercentForTickets(tickets) && withinDiscountStrategy()")
    private Collection<Double> countDiscountApplied(ProceedingJoinPoint jp, Collection<Ticket> tickets)
            throws Throwable {
        Collection<Double> discounts = (Collection<Double>) jp.proceed();
        String strategyClassSimpleName = jp.getThis().getClass().getSimpleName();
        if (tickets.isEmpty()) {
            return discounts;
        }
        User ticketOwner = tickets.iterator().next().getOwner();
        if (discounts.stream().anyMatch(d -> d != 0)) {
            incrementDiscountCounter(ticketOwner, strategyClassSimpleName);
        }
        return discounts;
    }

    private void incrementDiscountCounter(User owner, String strategyClassSimpleName) {
        DiscountStrategyCounter discountStrategyCounter = discountStrategyCounterRepository
                .findByOwnerAndStrategyName(owner, strategyClassSimpleName).orElse(
                        DiscountStrategyCounter.builder().discountStrategyName(strategyClassSimpleName)
                                .ticketOwner(owner)
                                .build());
        discountStrategyCounter.setCounter(discountStrategyCounter.getCounter() + 1);
        discountStrategyCounterRepository.save(discountStrategyCounter);
    }
}
