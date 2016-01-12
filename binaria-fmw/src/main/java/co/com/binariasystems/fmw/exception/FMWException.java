package co.com.binariasystems.fmw.exception;

public class FMWException extends Exception {

	public FMWException() {
		super();
	}

	public FMWException(String message) {
		super(message);
	}

	public FMWException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public FMWException(String message, Throwable cause) {
		super(message, cause);
	}

	public FMWException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
