package gr.nerv.services.sqm.poller;
public class SQMLEException extends Exception {

	/**
	 * Generic SQMLE device exception class, for later
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SQMLEException() {
		super();
	}

	/**
	 * @param message
	 */
	public SQMLEException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SQMLEException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SQMLEException(Throwable cause) {
		super(cause);
	}

}
