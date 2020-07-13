package ua.com.kl.cmathtutor.shell.configuration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TheaterHistoryFileNameProvider extends DefaultHistoryFileNameProvider {

    @Override
    public String getHistoryFileName() {
        return "theater-shell.log";
    }

    @Override
    public String getProviderName() {
        return "Theater Manager";
    }
}
