package net.grabsalot.core;

/**
 *
 * @author madboyka
 */
public class ApplicationException extends Exception {

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
