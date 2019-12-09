package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event implements IdContainer, Serializable {

    private Integer id;
    private String name;
    private Long baseTicketPriceInCents;
    private Rating rating;
}
