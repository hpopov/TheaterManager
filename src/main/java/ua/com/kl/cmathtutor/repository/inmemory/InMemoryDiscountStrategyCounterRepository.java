package ua.com.kl.cmathtutor.repository.inmemory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.DiscountStrategyCounter;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.DuplicateKeyException;
import ua.com.kl.cmathtutor.exception.MandatoryAttributeException;
import ua.com.kl.cmathtutor.repository.DiscountStrategyCounterRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;

public class InMemoryDiscountStrategyCounterRepository extends AbstractCrudInMemoryRepository<DiscountStrategyCounter>
	implements DiscountStrategyCounterRepository {

    private static final String TICKET_OWNER_SHOULD_EXIST_MSG = "Ticket owner should already be persisted!";

    private static final String DISCOUNT_STRATEGY_NAME_SHOULD_EXIST = "Discount strategy name must not be absent for DiscountStrategyCounter!";

    @Setter
    private UserRepository userRepository;

    @Override
    public Optional<DiscountStrategyCounter> findByOwnerAndStrategyName(User owner, String strategyName) {
	return findAll().stream().filter(e -> e.getTicketOwner() == owner || e.getTicketOwner().equals(owner))
		.filter(e -> e.getDiscountStrategyName().equals(strategyName)).findFirst();
    }

    @Override
    public List<DiscountStrategyCounter> findAll() {
	return super.findAll().stream().map(this::refreshOwnerIfNeeded).collect(Collectors.toList());
    }

    @Override
    public Optional<DiscountStrategyCounter> findById(Integer id) {
	return super.findById(id).map(this::refreshOwnerIfNeeded);
    }

    private DiscountStrategyCounter refreshOwnerIfNeeded(DiscountStrategyCounter discountStrategyCounter) {
	User ticketOwner = discountStrategyCounter.getTicketOwner();
	if (Objects.nonNull(ticketOwner)) {
	    discountStrategyCounter.setTicketOwner(userRepository.findById(ticketOwner.getId()).get());
	}
	return discountStrategyCounter;
    }

    @Override
    protected void checkMandatoryAttributes(DiscountStrategyCounter discountStrategyCounter) {
	User ticketOwner = discountStrategyCounter.getTicketOwner();
	if (Objects.nonNull(ticketOwner)
		&& !userRepository.findById(ticketOwner.getId()).isPresent()) {
	    throw new MandatoryAttributeException(TICKET_OWNER_SHOULD_EXIST_MSG);
	}
	String strategyName = discountStrategyCounter.getDiscountStrategyName();
	if (Objects.isNull(strategyName)) {
	    throw new MandatoryAttributeException(DISCOUNT_STRATEGY_NAME_SHOULD_EXIST);
	}
	long discountCounterWithSameTicketOwner = (Objects.isNull(ticketOwner)
		? findAll().stream().filter(dc -> Objects.isNull(dc.getTicketOwner()))
		: findAll().stream().filter(dc -> ticketOwner.equals(dc.getTicketOwner())))
			.filter(dc -> !dc.getId().equals(discountStrategyCounter.getId()))
			.count();
	if (discountCounterWithSameTicketOwner != 0) {
	    throw new DuplicateKeyException(String.format(
		    "Ticket owner has to be unique, but found %s other discountCounters with same ticket owner",
		    discountCounterWithSameTicketOwner));
	}
	long discountCountersWithSameStrategyName = findAll().stream()
		.filter(dc -> dc.getDiscountStrategyName().equals(strategyName))
		.filter(dc -> !dc.getId().equals(discountStrategyCounter.getId()))
		.count();
	if (discountCountersWithSameStrategyName != 0) {
	    throw new DuplicateKeyException(String.format(
		    "Discount strategy name has to be unique, but found %s other discountCounters with same event id",
		    discountCountersWithSameStrategyName));
	}
    }

}
