package ua.com.kl.cmathtutor.exception;

@SuppressWarnings("serial")
public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(String message) {
	super(message);
    }
}
