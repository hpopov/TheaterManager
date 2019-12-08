package ua.com.kl.cmathtutor.service;

import java.util.List;

import ua.com.kl.cmathtutor.domain.entity.Department;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department updateDepartmentById(Integer departmentId, Department departmentUpdateData) throws NotFoundException;

    List<Department> getAllDepartments();

    Department getDepartmentById(Integer departmentId) throws NotFoundException;

    void assignEmployeeToDepartment(Employee employee, Department department) throws NotFoundException;

    List<Employee> getAllEmployeesInDepartment(Department department) throws NotFoundException;
}
