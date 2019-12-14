package ua.com.kl.cmathtutor.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.AuditoriumService;

@Service
public class DefaultAuditoriumService implements AuditoriumService {

    @Autowired
    private List<Auditorium> auditoriums;
    
    @Override
    public List<Auditorium> getAll() {
	return Collections.unmodifiableList(auditoriums);
    }

    @Override
    public Auditorium getByName(String name) throws NotFoundException {
	return auditoriums.stream().filter(aud -> aud.getName().equals(name)).findFirst()
		.orElseThrow(auditoriumNotFoundException(name));
    }

    private Supplier<NotFoundException> auditoriumNotFoundException(String name) {
	return () -> new NotFoundException(String.format("Auditorium with name %s was not found", name));
    }

}
