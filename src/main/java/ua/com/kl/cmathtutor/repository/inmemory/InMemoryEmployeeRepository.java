package ua.com.kl.cmathtutor.repository.inmemory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Employee;
import ua.com.kl.cmathtutor.repository.EmployeeRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryEmployeeRepository extends AbstractCrudInMemoryRepository<Employee> implements EmployeeRepository {

    private static InMemoryEmployeeRepository instance;

    public static InMemoryEmployeeRepository getInstance() {
	if (instance == null) {
	    instance = new InMemoryEmployeeRepository();
	}
	return instance;
    }
}
