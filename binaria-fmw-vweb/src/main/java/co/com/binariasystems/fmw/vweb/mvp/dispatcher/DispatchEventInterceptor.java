package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import static co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo.RESOURCE_URL_ARG;

import org.aspectj.lang.ProceedingJoinPoint;

import co.com.binariasystems.fmw.vweb.mvp.event.RequestDispatchEvent;
import co.com.binariasystems.fmw.vweb.mvp.security.SecurityManager;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;

public class DispatchEventInterceptor {
	private SecurityManager securityManager;
	private boolean dummy;
	
	public void intercept(ProceedingJoinPoint joinPoint) throws ViewDispatchException{
		RequestDispatchEvent dispatchEvent = (RequestDispatchEvent)joinPoint.getArgs()[0];
		AuthorizationAndAuthenticationInfo authInfo = new AuthorizationAndAuthenticationInfo()
				.set(RESOURCE_URL_ARG, dispatchEvent.getString(RequestDispatchEvent.URL_PROPERTY));
		boolean authenticated = securityManager.isAuthenticated(authInfo);
		boolean authorized = false;
		try{
			if(authenticated){
				authorized = securityManager.isAuthorized(authInfo);
			}else 
				authorized = securityManager.isPublicView(dispatchEvent.getString(RequestDispatchEvent.URL_PROPERTY));
			
			String targetUrl = ViewProvider.SPECIAL_VIEWS_URL+"?"+ViewProvider.AUTHENTICATION_VIEW_PARAM_IDENTIFIER;//Por defecto se tiene la url de autenticacion
			targetUrl = authorized ? dispatchEvent.getString(RequestDispatchEvent.URL_PROPERTY) : (authenticated ? securityManager.getForbiddenViewUrl() : targetUrl);
			dispatchEvent.set(RequestDispatchEvent.URL_PROPERTY, targetUrl);
			joinPoint.proceed(new Object[]{dispatchEvent});
		}catch(Throwable ex){
			throw new ViewDispatchException(ex);
		}
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
