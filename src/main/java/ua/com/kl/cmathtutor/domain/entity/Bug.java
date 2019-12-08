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
public class Bug implements IdContainer, Serializable {

    @Builder.Default
    private Integer id = 0;
    private String description;
    @Builder.Default
    private Integer employeeId = null;

}
