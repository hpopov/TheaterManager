package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Ticket implements IdContainer, Serializable {

    private Integer id;
    private EventPresentation eventPresentation;
    @Nullable
    private User owner;
    @Builder.Default
    private Boolean isBooked = false;
    private Integer seatNumber;
    private Long calculatedriceInCents;
}
