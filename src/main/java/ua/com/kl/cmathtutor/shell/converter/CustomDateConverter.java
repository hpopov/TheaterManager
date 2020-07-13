package ua.com.kl.cmathtutor.shell.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.shell.type.CustomDate;

@Component
public class CustomDateConverter implements Converter<CustomDate> {

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    private DateFormat dateFormat;

    public CustomDateConverter() {
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return CustomDate.class.isAssignableFrom(type);
    }

    @Override
    public CustomDate convertFromText(String value, Class<?> targetType, String optionContext) {
        try {
            return CustomDate.of(dateFormat.parse(value));
        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "Unable to convert " + value + " to custom date using format " + DATE_FORMAT);
        }
    }

    @Override
    public boolean getAllPossibleValues(
            List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target
    ) {
        return false;
    }
}
