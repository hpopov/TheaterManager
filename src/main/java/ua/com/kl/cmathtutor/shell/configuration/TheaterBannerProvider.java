package ua.com.kl.cmathtutor.shell.configuration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TheaterBannerProvider extends DefaultBannerProvider {

    @Override
    public String getBanner() {
	StringBuilder sb = new StringBuilder();
	sb
		.append("|===========================================================================|")
		.append(OsUtils.LINE_SEPARATOR)
		.append("|=====================         Theater Manager         =====================|")
		.append(OsUtils.LINE_SEPARATOR)
		.append("|===========================================================================|");
	return sb.toString();
    }

    @Override
    public String getProviderName() {
	return "Theater Manager";
    }

    @Override
    public String getVersion() {
	return "1.0.0";
    }

    @Override
    public String getWelcomeMessage() {
	return "Welcome to the Theater Manager app. Please, use tab to get auto-complete." + OsUtils.LINE_SEPARATOR
		+ "If you need any help, just go ahead and type 'help' and press Enter";
    }
}
