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
public class DiscountStrategyCounter implements IdContainer, Serializable {

    private Integer id;
    private User ticketOwner;
    private String discountStrategyName;
    @Builder.Default
    private Long counter = 0L;
}
