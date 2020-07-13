package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditorium implements Serializable {

    private String name;
    private Integer numberOfSeats;
    private Set<Integer> vipSeats;

    public Auditorium(String name, Integer numberOfSeats, String vipSeatsString) {
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.vipSeats = Stream.of(vipSeatsString.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    }
}
