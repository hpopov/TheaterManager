package ua.com.kl.cmathtutor.domain.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPresentation implements IdContainer, Serializable {

    private Integer id;
    private Date airDate;
    private Long durationInMilliseconds;
    private Event event;
    private Auditorium auditorium;
}
