package ua.com.kl.cmathtutor.repository;

import java.util.Optional;

import ua.com.kl.cmathtutor.domain.entity.DiscountStrategyCounter;
import ua.com.kl.cmathtutor.domain.entity.User;

public interface DiscountStrategyCounterRepository extends CrudRepository<DiscountStrategyCounter> {

    Optional<DiscountStrategyCounter> findByOwnerAndStrategyName(User owner, String strategyName);
}
