package ua.com.kl.cmathtutor.aspect;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ua.com.kl.cmathtutor.repository.DiscountStrategyCounterRepository;
import ua.com.kl.cmathtutor.repository.EventCounterRepository;
import ua.com.kl.cmathtutor.repository.EventRepository;
import ua.com.kl.cmathtutor.repository.UserRepository;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryDiscountStrategyCounterRepository;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryEventCounterRepository;

@Configuration
@Import(AspectConfiguration.class)
class AspectIntegrationSpringContext {

    @Bean
    public EventCounterRepository eventCounterRepository() {
	InMemoryEventCounterRepository repository = new InMemoryEventCounterRepository();
	repository.setEventRepository(eventRepository());
	return repository;
    }

    @Bean
    public EventRepository eventRepository() {
	return Mockito.mock(EventRepository.class);
    }

    @Bean
    public DiscountStrategyCounterRepository discountStrategyCounterRepository() {
	InMemoryDiscountStrategyCounterRepository repository = new InMemoryDiscountStrategyCounterRepository();
	repository.setUserRepository(userRepository());
	return repository;
    }

    @Bean
    public UserRepository userRepository() {
	return Mockito.mock(UserRepository.class);
    }
}
