package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import org.aspectj.lang.ProceedingJoinPoint;

import co.com.binariasystems.fmw.vweb.mvp.event.RequestDispatchEvent;
import co.com.binariasystems.fmw.vweb.mvp.security.SecurityManager;

public class DispatchEventInterceptor {
	private SecurityManager securityManager;
	private boolean dummy;
	
	public void intercept(ProceedingJoinPoint joinPoint) throws Throwable{
		RequestDispatchEvent dispatchEvent = (RequestDispatchEvent)joinPoint.getArgs()[0];
		joinPoint.proceed();
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}
	
	
	
}
