package ua.com.kl.cmathtutor.exeption;

@SuppressWarnings("serial")
public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(String message) {
	super(message);
    }
}
