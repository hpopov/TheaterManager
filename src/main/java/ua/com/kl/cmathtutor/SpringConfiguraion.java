package ua.com.kl.cmathtutor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import ua.com.kl.cmathtutor.repository.inmemory.InMemoryRepositoryConfiguration;

@Configuration
@Import(value = InMemoryRepositoryConfiguration.class)
@ImportResource(locations = "classpath:auditorium-spring.xml")
public class SpringConfiguraion {

}
