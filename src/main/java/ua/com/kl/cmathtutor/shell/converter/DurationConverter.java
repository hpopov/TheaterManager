package ua.com.kl.cmathtutor.shell.converter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.shell.type.Duration;

@Component
public class DurationConverter implements Converter<Duration> {

    public static final String FORMAT = "hh:mm";
    public static final long MILLIESECONDS_PER_HOUR = 3600000L;
    public static final long MILLIESECONDS_PER_MINUTE = 60000L;

    private static final String REGEX_FORMAT = "(\\d{2}):(\\d{2})";

    private final Pattern pattern;

    public DurationConverter() {
	pattern = Pattern.compile(REGEX_FORMAT);
    }

    @Override
    public boolean supports(Class<?> type, String optionContext) {
	return Duration.class.isAssignableFrom(type);
    }

    @Override
    public Duration convertFromText(String value, Class<?> targetType, String optionContext) {
	Matcher matcher = pattern.matcher(value);
	if (!matcher.matches()) {
	    throw new IllegalArgumentException(
		    "Unable to convert " + value + " to duration using format " + REGEX_FORMAT);
	}
	int hours = Integer.parseInt(matcher.group(1));
	int minutes = Integer.parseInt(matcher.group(2));
	return Duration.ofMilliseconds(hours * MILLIESECONDS_PER_HOUR + minutes * MILLIESECONDS_PER_MINUTE);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
	    String optionContext, MethodTarget target) {
	return false;
    }

}
