package ua.com.kl.cmathtutor.config;

import org.springframework.stereotype.Component;

@Component
public class TicketCalculationConfigProperties {

    public double vipSeatsPriceMultiplier() {
        return 2;
    }

    public double highRatedEventsPriceMultiplier() {
        return 1.2;
    }
}
