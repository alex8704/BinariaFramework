package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import co.com.binariasystems.fmw.exception.FMWException;

public class FormValidationException extends FMWException {
	private List<String> validationMessages;
	public FormValidationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FormValidationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public FormValidationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FormValidationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public FormValidationException(String message, List<String> validationMessages) {
		super(message);
		this.validationMessages = validationMessages;
	}

	public FormValidationException(Throwable cause) {
		super(cause.getMessage(), cause);
		// TODO Auto-generated constructor stub
	}

	public List<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(List<String> validationMessages) {
		this.validationMessages = validationMessages;
	}
	
}
