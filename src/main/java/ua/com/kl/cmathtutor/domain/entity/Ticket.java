package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements IdContainer, Serializable {

    private Integer id;
    private EventPresentation eventPresentation;
    @Nullable
    private User owner;
    @Builder.Default
    private Boolean isBooked = false;
    private Integer seatNumber;
    private Long calculatedPriceInCents;
    @Builder.Default
    private Double discountInPercent = 0d;
    private Long totalPriceInCents;
}
