package ua.com.kl.cmathtutor.exception;

@SuppressWarnings("serial")
public class TicketsAlreadyBookedException extends Exception {

    public TicketsAlreadyBookedException(String message) {
        super(message);
    }
}
