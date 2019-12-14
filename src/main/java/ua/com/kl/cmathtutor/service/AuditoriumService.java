package ua.com.kl.cmathtutor.service;

import java.util.List;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.exception.NotFoundException;

public interface AuditoriumService {

    List<Auditorium> getAll();

    Auditorium getByName(String name) throws NotFoundException;
}
