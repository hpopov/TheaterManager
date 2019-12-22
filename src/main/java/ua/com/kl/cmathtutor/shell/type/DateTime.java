package ua.com.kl.cmathtutor.shell.type;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTime {

    private final Date date;

    public static DateTime of(Date date) {
	return new DateTime(date);
    }
}
