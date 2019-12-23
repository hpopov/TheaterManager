package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements IdContainer, Serializable {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    @ToString.Exclude
    private String password;
    private Date birthdayDate;
    @Builder.Default
    private Integer purchasedTicketsNumber = 0;
    @Builder.Default
    private boolean isAdmin = false;
}
