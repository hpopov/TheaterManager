package ua.com.kl.cmathtutor.domain.entity;

import java.util.Set;

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
}
