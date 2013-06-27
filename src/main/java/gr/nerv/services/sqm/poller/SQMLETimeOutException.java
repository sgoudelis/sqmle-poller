package gr.nerv.services.sqm.poller;

public class SQMLETimeOutException extends Exception {

	private static final long serialVersionUID = 1L;

	public SQMLETimeOutException() {
		super();
	}

	public SQMLETimeOutException(String message) {
		super(message);
	}

	public SQMLETimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQMLETimeOutException(Throwable cause) {
		super(cause);
	}
}
