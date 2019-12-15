package ua.com.kl.cmathtutor.exception;

@SuppressWarnings("serial")
public class MandatoryAttributeException extends RuntimeException {

    public MandatoryAttributeException(String message) {
	super(message);
    }
}
