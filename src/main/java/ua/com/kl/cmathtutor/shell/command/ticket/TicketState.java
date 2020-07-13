package ua.com.kl.cmathtutor.shell.command.ticket;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.Ticket;

@Component
@Getter
@Setter
public class TicketState {

    private List<Ticket> tickets;

    public boolean hasTicketToPurchase() {
        return Objects.nonNull(tickets);
    }
}
