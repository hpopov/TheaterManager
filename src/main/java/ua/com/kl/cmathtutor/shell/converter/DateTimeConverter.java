package ua.com.kl.cmathtutor.shell.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.shell.type.DateTime;

@Component
public class DateTimeConverter implements Converter<DateTime> {

    public static final String FORMAT = "dd-MM-yyyy HH:mm:ss";
    private DateFormat dateFormat;

    public DateTimeConverter() {
        dateFormat = new SimpleDateFormat(FORMAT);
    }

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return DateTime.class.isAssignableFrom(type);
    }

    @Override
    public DateTime convertFromText(String value, Class<?> targetType, String optionContext) {
        Date date;
        try {
            date = dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to parse datetime with format " + FORMAT);
        }
        return DateTime.of(date);
    }

    @Override
    public boolean getAllPossibleValues(
            List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target
    ) {
        return false;
    }
}
