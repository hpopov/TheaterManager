package ua.com.kl.cmathtutor.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Department;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.DepartmentRepository;
import ua.com.kl.cmathtutor.service.impl.DefaultDepartmentService;

@ExtendWith(MockitoExtension.class)
class DefaultDepartmentServiceTest {

    @Mock
    DepartmentRepository departmentRepository;
    @Mock
    EmployeeService employeeService;

    DefaultDepartmentService service;

    @Test
    final void getInstance_ShouldReturnTheSameInstance() {
	final DefaultDepartmentService firstInstance = DefaultDepartmentService.getInstance();

	assertThat(DefaultDepartmentService.getInstance(), is(sameInstance(firstInstance)));
    }

    @BeforeEach
    void setUp() throws Exception {
	service = new DefaultDepartmentService(departmentRepository, employeeService);
    }

    @Test
    final void createDepartment_Should_SaveTheDepartment() {
	final Department department = Department.builder().name("Dep1").build();
	when(departmentRepository.save(any())).thenReturn(department);

	assertThat(service.createDepartment(department), is(sameInstance(department)));

	verify(departmentRepository).save(department);
    }

    @Test
    final void whenDepartmentExists_Then_updateDepartmentById_Should_SaveTheDepartment() {
	final Department department = Department.builder().name("Dep1").build();
	when(departmentRepository.save(any())).thenReturn(department);

	final Department createdDepartment = service.createDepartment(department);
	assertAll(() -> assertThat(createdDepartment, is(sameInstance(department))),
		() -> assertThat(createdDepartment.getId(), is(equalTo(0))));
	verify(departmentRepository, atLeastOnce()).save(department);
    }

    @Test
    final void whenDepartmentNotExists_Then_updateDepartmentById_Should_ThrowException() {
	final int departmentId = 1;
	when(departmentRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> {
	    service.updateDepartmentById(departmentId, new Department());
	});

	verify(departmentRepository, only()).findById(departmentId);
    }

    @ParameterizedTest
    @MethodSource("allDepartmentLists")
    final void getAllDepartments_ShouldReturnAllSavedDepartments(List<Department> savedDepartments) {
	when(departmentRepository.findAll()).thenReturn(savedDepartments);

	assertThat(service.getAllDepartments(), is(sameInstance(savedDepartments)));

	verify(departmentRepository).findAll();
    }

    static final Stream<Arguments> allDepartmentLists() {
	return Stream.of(Arguments.of(Lists.newArrayList(new Department(), new Department()),
		Arguments.of(Collections.emptyList())),
		Arguments.of(Lists.newArrayList(new Department())),
		Arguments.of(Lists.newArrayList(new Department(), new Department(), new Department())));
    }

    @Test
    final void whenDepartmentExists_Then_getDepartmentById_ShouldReturnADepartment() throws NotFoundException {
	final Integer departmentId = 45;
	final Department department = new Department(departmentId, "John");
	when(departmentRepository.findById(any())).thenReturn(Optional.of(department));

	assertThat(service.getDepartmentById(departmentId), is(sameInstance(department)));

	verify(departmentRepository).findById(departmentId);
    }

    @Test
    final void whenDepartmentNotExists_Then_getDepartmentById_ShouldThrowAnException() throws NotFoundException {
	final Integer departmentId = 45;
	when(departmentRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.getDepartmentById(departmentId));

	verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    final void whenBothEmployeeAndDepartmentExist_Then_assignEmployeeToDepartment_ShouldChangeDepartmentId()
	    throws NotFoundException {
	final Employee employee = new Employee(1, "John", null);
	final Integer departmentId = Integer.valueOf(1);
	final Department department = new Department(departmentId, "Dep1");
	when(departmentRepository.findById(any())).thenReturn(Optional.of(department));
	when(employeeService.getEmployeeById(any())).thenReturn(employee);
	when(employeeService.updateEmployeeById(any(), any())).thenReturn(employee);

	service.assignEmployeeToDepartment(employee, department);

	assertThat(employee.getId(), is(equalTo(departmentId)));

	verify(departmentRepository, only()).findById(departmentId);
	verify(employeeService).getEmployeeById(employee.getId());
	verify(employeeService).updateEmployeeById(employee.getId(), employee);
    }

    @Test
    final void whenEmployeeNotExist_Then_assignEmployeeToDepartment_ShouldThrowException() throws NotFoundException {
	final Employee employee = new Employee(1, "John", null);
	final Integer employeeId = employee.getId();
	final Department department = new Department();
	when(employeeService.getEmployeeById(any())).thenThrow(NotFoundException.class);

	assertThrows(NotFoundException.class, () -> service.assignEmployeeToDepartment(employee, department));

	verify(employeeService, only()).getEmployeeById(employeeId);
	verifyZeroInteractions(departmentRepository);
    }

    @Test
    final void whenEmployeeExistsAndDepartmentNotExist_Then_assignEmployeeToDepartment_ShouldThrowException()
	    throws NotFoundException {
	final Integer employeeId = 1;
	final Employee employee = new Employee(employeeId, "John", null);
	final Integer departmentId = Integer.valueOf(1);
	final Department department = new Department(departmentId, "Dep1");
	when(employeeService.getEmployeeById(any())).thenReturn(employee);
	when(departmentRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.assignEmployeeToDepartment(employee, department));

	verify(departmentRepository, only()).findById(departmentId);
	verify(employeeService, only()).getEmployeeById(employeeId);
    }

    @ParameterizedTest
    @MethodSource("allEmployeesForDepartment")
    final void whenDepartmentExists_Then_getAllEmployeesInDepartment_ShouldReturnEmployeesWithSpecifiedDepartmentId(
	    List<Employee> employees, Integer departmentId) throws NotFoundException {
	final Department department = Department.builder().id(departmentId).build();
	when(employeeService.getAllEmployees()).thenReturn(employees);
	when(departmentRepository.findById(any())).thenReturn(Optional.of(department));

	final Collection<Employee> expectedEmployeesInDepartment = employees.stream()
		.filter(e -> e.getDepartmentId().equals(departmentId)).collect(Collectors.toList());
	final Collection<Employee> actualEmployeesInDepartment = service.getAllEmployeesInDepartment(department);

	assertThat(expectedEmployeesInDepartment.toArray(), is(equalTo(actualEmployeesInDepartment.toArray())));

	verify(employeeService, only()).getAllEmployees();
	verify(departmentRepository, only()).findById(departmentId);
    }

    final static Stream<Arguments> allEmployeesForDepartment() {
	return Stream.of(
		Arguments.of(Lists.newArrayList(new Employee(2, "Jane", 3), new Employee(5, "John", 3)),
			Integer.valueOf(3)),
		Arguments.of(Lists.newArrayList(new Employee(2, "Jane", 1), new Employee(3, "John", 5)),
			Integer.valueOf(1)),
		Arguments.of(Collections.emptyList(), Integer.valueOf(5)),
		Arguments.of(Lists.newArrayList(new Employee(2, "Jane", 3), new Employee(3, "John", 3)),
			Integer.valueOf(55)));
    }

    @Test
    final void whenDepartmentNotExists_Then_getAllEmployeesInDepartment_ShouldThrowException()
	    throws NotFoundException {
	final Integer departmentId = 5;
	final Department department = Department.builder().id(departmentId).build();
	when(departmentRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.getAllEmployeesInDepartment(department));

	verifyZeroInteractions(employeeService);
	verify(departmentRepository, only()).findById(departmentId);
    }
}
