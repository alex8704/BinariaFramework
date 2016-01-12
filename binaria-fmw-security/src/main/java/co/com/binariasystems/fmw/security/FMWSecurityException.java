package co.com.binariasystems.fmw.security;

import co.com.binariasystems.fmw.exception.FMWException;


public class FMWSecurityException extends FMWException {

	public FMWSecurityException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FMWSecurityException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public FMWSecurityException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FMWSecurityException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FMWSecurityException(Throwable cause) {
		super(cause.getMessage(), cause);
		// TODO Auto-generated constructor stub
	}
	
}
