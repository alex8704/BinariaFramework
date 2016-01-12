package co.com.binariasystems.fmw.exception;

public class FMWDataAccessException extends FMWException {

	public FMWDataAccessException() {
		super();
	}

	public FMWDataAccessException(String message) {
		super(message);
	}

	public FMWDataAccessException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public FMWDataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public FMWDataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
