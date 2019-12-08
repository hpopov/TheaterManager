package ua.com.kl.cmathtutor.repository.inmemory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Department;
import ua.com.kl.cmathtutor.repository.DepartmentRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryDepartmentRepository extends AbstractCrudInMemoryRepository<Department>
	implements DepartmentRepository {

    private static InMemoryDepartmentRepository instance;

    public static InMemoryDepartmentRepository getInstance() {
	if (instance == null) {
	    instance = new InMemoryDepartmentRepository();
	}
	return instance;
    }
}
