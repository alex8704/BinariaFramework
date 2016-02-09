package co.com.binariasystems.fmw.security.auditory;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import co.com.binariasystems.fmw.security.util.SecConstants;


public abstract class ShiroBasedAuditoryDataProvider<T> implements AuditoryDataProvider<T> {
	
	protected boolean supportAtmosphereWebSockets;

	public boolean isSupportAtmosphereWebSockets() {
		return supportAtmosphereWebSockets;
	}

	public void setSupportAtmosphereWebSockets(boolean supportAtmosphereWebSockets) {
		this.supportAtmosphereWebSockets = supportAtmosphereWebSockets;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getCurrenAuditoryUser(HttpServletRequest httpRequest) {
		return (T) getCurrentSubject(httpRequest).getPrincipal();
	}

	@Override
	public Date getCurrentDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	protected Subject getCurrentSubject(HttpServletRequest httpRequest){
		Subject currentSubject = supportAtmosphereWebSockets ? (Subject) httpRequest.getAttribute(SecConstants.ATMOSPHERE_SUBJECT_ATTRIBUTE) : null;
		return currentSubject != null ? currentSubject : SecurityUtils.getSubject();
	}
	
}
