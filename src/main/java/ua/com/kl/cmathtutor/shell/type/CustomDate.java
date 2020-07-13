package ua.com.kl.cmathtutor.shell.type;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomDate {

    private final Date date;

    public static CustomDate of(Date date) {
        return new CustomDate(date);
    }
}
