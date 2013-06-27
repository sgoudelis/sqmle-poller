package gr.nerv.services.sqm.poller;

public class SQMLETimeOutException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SQMLETimeOutException() {
		super();
	}

	/**
	 * @param message
	 */
	public SQMLETimeOutException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SQMLETimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SQMLETimeOutException(Throwable cause) {
		super(cause);
	}
}
