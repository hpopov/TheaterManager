package ua.com.kl.cmathtutor.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.impl.DefaultAuditoriumService;

class DefaultAuditoriumServiceTest {

    DefaultAuditoriumService service;

    private List<Auditorium> auditoriums;

    @BeforeEach
    void initialize() {
	auditoriums = Lists.newArrayList(
		Auditorium.builder().name("1").numberOfSeats(12).vipSeats(Sets.newHashSet(1, 2, 3)).build(),
		Auditorium.builder().name("2").numberOfSeats(6).vipSeats(Sets.newHashSet(5, 6)).build());
	service = new DefaultAuditoriumService(auditoriums);
    }

    @Test
    void getAll_ShouldReturnAllAuditoriums() {
	List<Auditorium> allAuditoriums = service.getAll();

	assertThat(allAuditoriums, containsInAnyOrder(auditoriums.toArray()));
    }

    @Test
    void whenAuditoriumIsPresent_Then_getByName_ShouldReturnAuditoriumWithName() throws NotFoundException {
	String existedName = "2";

	Auditorium byName = service.getByName(existedName);

	assertNotNull(byName);
	assertThat(byName.getName(), is(equalTo(existedName)));
    }

    @Test
    void whenAuditoriumIsAbssent_Then_getByName_ShouldThrowAnException() throws NotFoundException {
	String notExistedName = "notExistedName";

	assertThrows(NotFoundException.class, () -> service.getByName(notExistedName));
    }
}
