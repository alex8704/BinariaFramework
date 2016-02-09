package co.com.binariasystems.fmw.security.auditory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface AuditoryDataProvider<T> {
	public T getCurrenAuditoryUser(HttpSession httpSession);
	public T getCurrenAuditoryUser(HttpServletRequest httpRequest);
	public Object gettCurrentAuditoryUserForEntityCRUD(HttpSession httpSession);
	public Object gettCurrentAuditoryUserForEntityCRUD(HttpServletRequest httpRequest);
	public Date getCurrentDate();
}
