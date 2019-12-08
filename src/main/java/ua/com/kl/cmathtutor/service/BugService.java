package ua.com.kl.cmathtutor.service;

import java.util.List;

import ua.com.kl.cmathtutor.domain.entity.Bug;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;

public interface BugService {

    Bug createBug(Bug bug);

    Bug updateBugById(Integer bugId, Bug bugUpdateData) throws NotFoundException;

    List<Bug> getAllBugs();

    Bug getBugById(Integer bugId) throws NotFoundException;

    void assignBugToEmployee(Bug bug, Employee employee) throws NotFoundException;
}
