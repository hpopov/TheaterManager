package ua.com.kl.cmathtutor.shell.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Component
public class IntegerSetConverter implements Converter<Set<Integer>> {

    private static final String SET_VALUE_PATTERN = "\\d{1,6}(,\\d{1,6})*";

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public Set<Integer> convertFromText(String value, Class<?> targetType, String optionContext) {
        if (!value.matches(SET_VALUE_PATTERN)) {
            throw new IllegalArgumentException("Value " + value
                    + " can not be converted to set of integer, expected pattern is " + SET_VALUE_PATTERN);
        }
        Iterable<Integer> integers = Stream.of(value.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        return Sets.newLinkedHashSet(integers);
    }

    @Override
    public boolean getAllPossibleValues(
            List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target
    ) {
        return false;
    }
}
