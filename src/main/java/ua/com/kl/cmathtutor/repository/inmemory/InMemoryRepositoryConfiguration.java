package ua.com.kl.cmathtutor.repository.inmemory;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = "classpath:inmemory-repository-spring.xml")
public class InMemoryRepositoryConfiguration {

}
