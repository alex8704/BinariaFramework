package co.com.binariasystems.fmw.exception;

public class FMWUncheckedException extends RuntimeException {

	public FMWUncheckedException() {
		super();
	}

	public FMWUncheckedException(String message) {
		super(message);
	}

	public FMWUncheckedException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public FMWUncheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FMWUncheckedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
