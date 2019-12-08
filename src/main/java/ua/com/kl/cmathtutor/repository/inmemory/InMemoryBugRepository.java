package ua.com.kl.cmathtutor.repository.inmemory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.com.kl.cmathtutor.domain.entity.Bug;
import ua.com.kl.cmathtutor.repository.BugRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryBugRepository extends AbstractCrudInMemoryRepository<Bug> implements BugRepository {

    private static InMemoryBugRepository instance;

    public static InMemoryBugRepository getInstance() {
	if (instance == null) {
	    instance = new InMemoryBugRepository();
	}
	return instance;
    }
}
