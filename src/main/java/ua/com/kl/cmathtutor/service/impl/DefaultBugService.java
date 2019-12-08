package ua.com.kl.cmathtutor.service.impl;

import java.util.List;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Bug;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.BugRepository;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryBugRepository;
import ua.com.kl.cmathtutor.service.BugService;
import ua.com.kl.cmathtutor.service.EmployeeService;

@AllArgsConstructor
public class DefaultBugService implements BugService {

    private static DefaultBugService instance;

    public static DefaultBugService getInstance() {
	if (instance == null) {
	    instance = new DefaultBugService();
	}
	return instance;
    }

    private BugRepository bugRepository;
    private EmployeeService employeeService;

    private DefaultBugService() {
	bugRepository = InMemoryBugRepository.getInstance();
	employeeService = DefaultEmployeeService.getInstance();
    }

    @Override
    public Bug createBug(Bug bug) {
	bug.setId(0);
	return bugRepository.save(bug);
    }

    @Override
    public Bug updateBugById(Integer bugId, Bug bugUpdateData) throws NotFoundException {
	bugUpdateData.setId(bugId);
	return bugRepository.findById(bugId).map(bug -> bugRepository.save(bugUpdateData))
		.orElseThrow(bugNotFoundException(bugId));
    }

    private Supplier<NotFoundException> bugNotFoundException(int bugId) {
	return () -> new NotFoundException("Bug with id " + bugId + " was not found!");
    }

    @Override
    public List<Bug> getAllBugs() {
	return bugRepository.findAll();
    }

    @Override
    public Bug getBugById(Integer bugId) throws NotFoundException {
	return bugRepository.findById(bugId).orElseThrow(bugNotFoundException(bugId));
    }

    @Override
    public void assignBugToEmployee(Bug bug, Employee employee) throws NotFoundException {
	Integer employeeId = employee.getId();
	bug = getBugById(bug.getId());
	employeeService.getEmployeeById(employeeId);
	bug.setEmployeeId(employeeId);
	bugRepository.save(bug);
    }

}
