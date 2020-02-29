package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCounter implements IdContainer, Serializable {

    private Integer id;
    private Integer eventId;
    @Builder.Default
    private Long accessedCounter = 0L;
    @Builder.Default
    private Long priceQueriedCounter = 0L;
    @Builder.Default
    private Long ticketsBookedCounter = 0L;
}
