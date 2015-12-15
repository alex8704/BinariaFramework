package co.com.binariasystems.fmw.security.util;

import co.com.binariasystems.fmw.security.resources.resources;

public interface SecConstants {
	public static final String DEFAULT_AUTH_MESSAGES_PATH = resources.messagesPackage() + ".auth_messages";
	public static final String ATMOSPHERE_SUBJECT_ATTRIBUTE = org.atmosphere.cpr.FrameworkConfig.SECURITY_SUBJECT;
}
