package co.com.binariasystems.fmw.util.mail;

public class MailPreparationException extends MailException {

	public MailPreparationException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	public MailPreparationException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
	public MailPreparationException(Throwable cause) {
		super("Could not prepare mail", cause);
	}

}
