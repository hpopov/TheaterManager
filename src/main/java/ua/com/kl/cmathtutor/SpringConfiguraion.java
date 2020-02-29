package ua.com.kl.cmathtutor;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import ua.com.kl.cmathtutor.repository.inmemory.InMemoryRepositoryConfiguration;
import ua.com.kl.cmathtutor.service.strategy.DiscountStrategy;

@Configuration
@Import(InMemoryRepositoryConfiguration.class)
@ImportResource(locations = "classpath:auditorium-spring.xml")
//TODO: what if include automatically only base configurations? within configuration choose implementations (i.e. DAO)
@ComponentScan
public class SpringConfiguraion {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Iterable<DiscountStrategy> discountStrategies() {
	LinkedList<DiscountStrategy> strategies = new LinkedList<>();
	for (String beanName : applicationContext.getBeanNamesForType(DiscountStrategy.class)) {
	    strategies.add(applicationContext.getBean(beanName, DiscountStrategy.class));
	}
	return strategies;
    }
}
