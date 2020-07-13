package ua.com.kl.cmathtutor.shell.configuration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TheaterPromptProvider extends DefaultPromptProvider {

    @Override
    public String getPrompt() {
        return "theater>";
    }

    @Override
    public String getProviderName() {
        return "Theater Manager";
    }
}
