package ua.com.kl.cmathtutor.domain.entity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Auditorium {

    private final String name;
    private final Integer numberOfSeats;
    private final Set<Integer> vipSeats;

    public Auditorium(String name, Integer numberOfSeats, String vipSeatsString) {
	this.name = name;
	this.numberOfSeats = numberOfSeats;
	this.vipSeats = Stream.of(vipSeatsString.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    }

}
