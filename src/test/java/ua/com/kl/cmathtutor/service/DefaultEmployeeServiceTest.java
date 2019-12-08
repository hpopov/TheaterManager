package ua.com.kl.cmathtutor.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.EmployeeRepository;
import ua.com.kl.cmathtutor.service.impl.DefaultEmployeeService;

@ExtendWith(MockitoExtension.class)
class DefaultEmployeeServiceTest {

    @Mock
    EmployeeRepository repository;

    DefaultEmployeeService service;

    @Test
    final void getInstance_ShouldReturnTheSameInstance() {
	final DefaultEmployeeService firstInstance = DefaultEmployeeService.getInstance();

	assertThat(DefaultEmployeeService.getInstance(), is(sameInstance(firstInstance)));
    }

    @BeforeEach
    void setUp() throws Exception {
	service = new DefaultEmployeeService(repository);
    }

    @Test
    final void createEmployee_Should_SaveTheEmployee() {
	final Employee employee = Employee.builder().name("Tom").build();
	when(repository.save(any())).thenReturn(employee);

	assertThat(service.createEmployee(employee), is(sameInstance(employee)));

	verify(repository).save(employee);
    }

    @Test
    final void whenEmployeeExists_Then_updateEmployeeById_Should_SaveTheEmployee() {
	final Employee employee = Employee.builder().name("Tom").build();
	when(repository.save(any())).thenReturn(employee);

	final Employee createdEmployee = service.createEmployee(employee);

	assertAll(() -> assertThat(createdEmployee, is(sameInstance(employee))),
		() -> assertThat(createdEmployee.getId(), is(equalTo(0))));

	verify(repository, atLeastOnce()).save(employee);
    }

    @Test
    final void whenEmployeeNotExists_Then_updateEmployeeById_Should_ThrowException() {
	final int employeeId = 1;
	when(repository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> {
	    service.updateEmployeeById(employeeId, new Employee());
	});

	verify(repository, only()).findById(employeeId);
    }

    @ParameterizedTest
    @MethodSource("allEmployeesLists")
    final void getAllEmployees_ShouldReturnAllSavedEmployees(List<Employee> savedEmployees) {
	when(repository.findAll()).thenReturn(savedEmployees);

	assertThat(service.getAllEmployees(), is(sameInstance(savedEmployees)));

	verify(repository, times(1)).findAll();
    }

    static final Stream<Arguments> allEmployeesLists() {
	return Stream.of(Arguments.of(Lists.newArrayList(new Employee(), new Employee()),
		Arguments.of(Collections.emptyList())),
		Arguments.of(Lists.newArrayList(new Employee())),
		Arguments.of(Lists.newArrayList(new Employee(), new Employee(), new Employee())));
    }

    @Test
    final void whenEmployeeExists_Then_getEmployeeById_ShouldReturnAnEmployee() throws NotFoundException {
	final int employeeId = 45;
	final Employee employee = new Employee(employeeId, "John", 21);
	when(repository.findById(any())).thenReturn(Optional.of(employee));

	assertThat(service.getEmployeeById(employeeId), is(sameInstance(employee)));

	verify(repository, times(1)).findById(employeeId);
    }

    @Test
    final void whenEmployeeNotExists_Then_getEmployeeById_ShouldThrowAnException() throws NotFoundException {
	final int employeeId = 45;
	when(repository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.getEmployeeById(employeeId));

	verify(repository, times(1)).findById(employeeId);
    }

}
