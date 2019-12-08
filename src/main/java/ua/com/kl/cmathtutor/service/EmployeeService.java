package ua.com.kl.cmathtutor.service;

import java.util.List;

import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;

public interface EmployeeService {

    Employee createEmployee(Employee employee);

    Employee updateEmployeeById(Integer employeeId, Employee employeeUpdateData) throws NotFoundException;

    List<Employee> getAllEmployees();

    Employee getEmployeeById(Integer employeeId) throws NotFoundException;
}
