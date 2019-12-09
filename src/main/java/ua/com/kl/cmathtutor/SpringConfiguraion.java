package ua.com.kl.cmathtutor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ua.com.kl.cmathtutor.repository.inmemory.InMemoryRepositoryConfiguration;

@Configuration
@Import(value = InMemoryRepositoryConfiguration.class)
public class SpringConfiguraion {

}
