package ua.com.kl.cmathtutor.service.impl;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Department;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.DepartmentRepository;
import ua.com.kl.cmathtutor.repository.inmemory.InMemoryDepartmentRepository;
import ua.com.kl.cmathtutor.service.DepartmentService;
import ua.com.kl.cmathtutor.service.EmployeeService;

@AllArgsConstructor
public class DefaultDepartmentService implements DepartmentService {

    private static DefaultDepartmentService instance;

    public static DefaultDepartmentService getInstance() {
	if (instance == null) {
	    instance = new DefaultDepartmentService();
	}
	return instance;
    }

    private DepartmentRepository departmentRepository;
    private EmployeeService employeeService;

    private DefaultDepartmentService() {
	departmentRepository = InMemoryDepartmentRepository.getInstance();
	employeeService = DefaultEmployeeService.getInstance();
    }

    @Override
    public Department createDepartment(Department department) {
	department.setId(0);
	return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartmentById(Integer departmentId, Department departmentUpdateData)
	    throws NotFoundException {
	departmentUpdateData.setId(departmentId);
	return departmentRepository.findById(departmentId).map(dept -> departmentRepository.save(departmentUpdateData))
		.orElseThrow(departmentNotFoundException(departmentId));
    }

    private Supplier<NotFoundException> departmentNotFoundException(int departmentId) {
	return () -> new NotFoundException("Department with id " + departmentId + " was not found!");
    }

    @Override
    public List<Department> getAllDepartments() {
	return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Integer departmentId) throws NotFoundException {
	return departmentRepository.findById(departmentId).orElseThrow(departmentNotFoundException(departmentId));
    }

    @Override
    public void assignEmployeeToDepartment(Employee employee, Department department) throws NotFoundException {
	Integer departmentId = department.getId();
	employee = employeeService.getEmployeeById(employee.getId());
	getDepartmentById(departmentId);
	employee.setDepartmentId(departmentId);
	employeeService.updateEmployeeById(employee.getId(), employee);
    }

    @Override
    public List<Employee> getAllEmployeesInDepartment(Department department) throws NotFoundException {
	Integer departmentId = department.getId();
	getDepartmentById(departmentId);
	return employeeService.getAllEmployees().stream().filter(empl -> departmentId.equals(empl.getDepartmentId()))
		.collect(Collectors.toList());
    }

}
