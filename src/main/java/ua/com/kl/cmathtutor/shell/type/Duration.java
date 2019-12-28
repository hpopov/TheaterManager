package ua.com.kl.cmathtutor.shell.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Duration {

    private long durationInMilliseconds;

    public static Duration ofMilliseconds(long durationInMilliseconds) {
	return new Duration(durationInMilliseconds);
    }
}
