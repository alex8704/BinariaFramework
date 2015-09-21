package co.com.binariasystems.fmw.util.mail;

public class MailParseException extends MailException {

	public MailParseException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	public MailParseException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
	public MailParseException(Throwable cause) {
		super("Could not parse mail", cause);
	}

}
