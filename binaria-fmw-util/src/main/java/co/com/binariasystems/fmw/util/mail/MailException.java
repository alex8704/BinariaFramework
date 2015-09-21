package co.com.binariasystems.fmw.util.mail;

import co.com.binariasystems.fmw.exception.FMWAbstractRuntimeException;

public abstract class MailException extends FMWAbstractRuntimeException {

	public MailException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	public MailException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
	

}
