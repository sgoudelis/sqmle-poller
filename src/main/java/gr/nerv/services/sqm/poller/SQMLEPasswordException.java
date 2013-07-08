package gr.nerv.services.sqm.poller;

public class SQMLEPasswordException extends Exception {

	/**
	 * Generic SQMLE device exception class, for later
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SQMLEPasswordException() {
		super();
	}

	/**
	 * @param message
	 */
	public SQMLEPasswordException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SQMLEPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SQMLEPasswordException(Throwable cause) {
		super(cause);
	}

}
