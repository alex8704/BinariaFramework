package co.com.binariasystems.fmw.security.auditory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

public interface AuditoryDataProvider<T> {
	public T getCurrenAuditoryUserByHttpSession(HttpSession ht);
	public T getCurrenAuditoryUserByServletRequest(ServletRequest request);
}
