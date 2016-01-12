package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import co.com.binariasystems.fmw.exception.FMWException;

public class ViewDispatchException extends FMWException {

	public ViewDispatchException() {
		// TODO Auto-generated constructor stub
	}

	public ViewDispatchException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ViewDispatchException(Throwable cause) {
		super(cause.getMessage(), cause);
		// TODO Auto-generated constructor stub
	}

	public ViewDispatchException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ViewDispatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
