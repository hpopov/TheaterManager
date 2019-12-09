package ua.com.kl.cmathtutor.domain.entity;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditorium {

    private String name;
    private Integer numberOfSeats;
    private Set<Integer> vipSeats;
}
