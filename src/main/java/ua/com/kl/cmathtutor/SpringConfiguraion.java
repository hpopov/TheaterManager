package ua.com.kl.cmathtutor;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.shell.converters.DateConverter;

import ua.com.kl.cmathtutor.repository.inmemory.InMemoryRepositoryConfiguration;

@Configuration
@Import(value = InMemoryRepositoryConfiguration.class)
@ImportResource(locations = "classpath:auditorium-spring.xml")
@ComponentScan
public class SpringConfiguraion {

    @Bean
    public DateConverter dateConverter() {
	return new DateConverter(new SimpleDateFormat("dd-mm-yyyy"));
    }
}
