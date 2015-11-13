package co.com.binariasystems.fmw.security.auditory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface AuditoryDataProvider<T> {
	public T getCurrenAuditoryUserByHttpSession(HttpSession httpSession);
	public T getCurrenAuditoryUserByServletRequest(HttpServletRequest httpRequest);
	public Date getCurrentDate();
}
