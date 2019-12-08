package ua.com.kl.cmathtutor.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

import ua.com.kl.cmathtutor.domain.entity.Bug;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.exeption.NotFoundException;
import ua.com.kl.cmathtutor.repository.BugRepository;
import ua.com.kl.cmathtutor.service.impl.DefaultBugService;

@ExtendWith(MockitoExtension.class)
class DefaultBugServiceTest {

    @Mock
    BugRepository bugRepository;
    @Mock
    EmployeeService employeeService;

    DefaultBugService service;

    @Test
    final void getInstance_ShouldReturnTheSameInstance() {
	final DefaultBugService firstInstance = DefaultBugService.getInstance();

	assertThat(DefaultBugService.getInstance(), is(sameInstance(firstInstance)));
    }

    @BeforeEach
    void setUp() throws Exception {
	service = new DefaultBugService(bugRepository, employeeService);
    }

    @Test
    final void createBug_Should_SaveTheBug() {
	final Bug bug = Bug.builder().description("Bug1").build();
	when(bugRepository.save(any())).thenReturn(bug);

	assertThat(service.createBug(bug), is(sameInstance(bug)));

	verify(bugRepository).save(bug);
    }

    @Test
    final void whenBugExists_Then_updateBugById_Should_SaveTheBug() {
	final Bug bug = Bug.builder().description("Bug1").build();
	when(bugRepository.save(any())).thenReturn(bug);

	final Bug createdBug = service.createBug(bug);
	assertAll(() -> assertThat(createdBug, is(sameInstance(bug))),
		() -> assertThat(createdBug.getId(), is(equalTo(0))));
	verify(bugRepository, atLeastOnce()).save(bug);
    }

    @Test
    final void whenBugNotExists_Then_updateBugById_Should_ThrowException() {
	final Integer bugId = 1;
	when(bugRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> {
	    service.updateBugById(bugId, new Bug());
	});

	verify(bugRepository, only()).findById(bugId);
    }

    @ParameterizedTest
    @MethodSource("allBugLists")
    final void getAllBugs_ShouldReturnAllSavedBugs(List<Bug> savedBugs) {
	when(bugRepository.findAll()).thenReturn(savedBugs);

	assertThat(service.getAllBugs(), is(sameInstance(savedBugs)));

	verify(bugRepository).findAll();
    }

    static final Stream<Arguments> allBugLists() {
	return Stream.of(Arguments.of(Lists.newArrayList(new Bug(), new Bug()),
		Arguments.of(Collections.emptyList())),
		Arguments.of(Lists.newArrayList(new Bug())),
		Arguments.of(Lists.newArrayList(new Bug(), new Bug(), new Bug())));
    }

    @Test
    final void whenBugExists_Then_getBugById_ShouldReturnABug() throws NotFoundException {
	final int bugId = 45;
	final Bug bug = new Bug(bugId, "Bug John", null);
	when(bugRepository.findById(any())).thenReturn(Optional.of(bug));

	assertThat(service.getBugById(bugId), is(sameInstance(bug)));

	verify(bugRepository).findById(bugId);
    }

    @Test
    final void whenBugNotExists_Then_getBugById_ShouldThrowAnException() throws NotFoundException {
	final Integer bugId = 45;
	when(bugRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.getBugById(bugId));

	verify(bugRepository, times(1)).findById(bugId);
    }

    @Test
    final void whenBothEmployeeAndBugExist_Then_assignBugToEmployee_ShouldChangeEmployeeId() throws NotFoundException {
	final Employee employee = new Employee(1, "John", null);
	final Bug bug = new Bug(5, "Dep1", null);
	when(bugRepository.findById(any())).thenReturn(Optional.of(bug));
	when(employeeService.getEmployeeById(any())).thenReturn(employee);
	when(bugRepository.save(any())).thenReturn(bug);

	service.assignBugToEmployee(bug, employee);

	assertThat(bug.getEmployeeId(), is(equalTo(employee.getId())));

	verify(bugRepository).findById(bug.getId());
	verify(employeeService).getEmployeeById(employee.getId());
	verify(bugRepository).save(bug);
	verifyNoMoreInteractions(bugRepository);
    }

    @Test
    final void whenBugNotExist_Then_assignEmployeeToBug_ShouldThrowException() throws NotFoundException {
	final Employee employee = new Employee();
	final Bug bug = new Bug(1, "John the bug", null);
	final Integer bugId = bug.getId();
	when(bugRepository.findById(any())).thenReturn(Optional.empty());

	assertThrows(NotFoundException.class, () -> service.assignBugToEmployee(bug, employee));

	verify(bugRepository, only()).findById(bugId);
	verifyZeroInteractions(employeeService);
    }

    @Test
    final void whenBugExistsAndEmployeeNotExist_Then_assignEmployeeToBug_ShouldThrowException()
	    throws NotFoundException {
	final Integer employeeId = 1;
	final Employee employee = new Employee(employeeId, "John", null);
	final Integer bugId = 1;
	final Bug bug = new Bug(bugId, "B1", null);
	when(bugRepository.findById(any())).thenReturn(Optional.of(bug));
	when(employeeService.getEmployeeById(any())).thenThrow(NotFoundException.class);

	assertThrows(NotFoundException.class, () -> service.assignBugToEmployee(bug, employee));

	verify(bugRepository, only()).findById(bugId);
	verify(employeeService, only()).getEmployeeById(employeeId);
    }

}
