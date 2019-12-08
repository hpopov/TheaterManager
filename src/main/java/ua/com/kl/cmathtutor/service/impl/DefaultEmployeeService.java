package ua.com.kl.cmathtutor.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.EmployeeRepository;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryEmployeeRepository;
import ua.com.kl.cmathtutor.service.EmployeeService;

@AllArgsConstructor
public class DefaultEmployeeService implements EmployeeService {

    private static DefaultEmployeeService instance;

    public static DefaultEmployeeService getInstance() {
	if (instance == null) {
	    instance = new DefaultEmployeeService();
	}
	return instance;
    }

    private EmployeeRepository employeeRepository;

    private DefaultEmployeeService() {
	employeeRepository = InMemoryEmployeeRepository.getInstance();
    }

    @Override
    public Employee createEmployee(Employee employee) {
	employee.setId(0);
	return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployeeById(Integer employeeId, Employee employeeUpdateData) throws NotFoundException {
	Optional<Employee> employee = employeeRepository.findById(employeeId);
	employeeUpdateData.setId(employeeId);
	return employee.map(empl -> employeeRepository.save(employeeUpdateData))
		.orElseThrow(employeeNotFoundException(employeeId));
    }

    private Supplier<NotFoundException> employeeNotFoundException(int employeeId) {
	return () -> new NotFoundException("Employee with id " + employeeId + " was not found!");
    }

    @Override
    public List<Employee> getAllEmployees() {
	return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) throws NotFoundException {
	return employeeRepository.findById(employeeId).orElseThrow(employeeNotFoundException(employeeId));
    }

}
