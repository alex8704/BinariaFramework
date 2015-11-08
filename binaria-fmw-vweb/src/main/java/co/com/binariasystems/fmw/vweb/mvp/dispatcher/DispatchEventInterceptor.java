
package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.security.mgt.SecurityManager;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;

public class DispatchEventInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatchEventInterceptor.class);
	
	private SecurityManager securityManager;
	private boolean dummy;
	
	public void intercept(ProceedingJoinPoint joinPoint) throws ViewDispatchException{
		ViewDispatchRequest dispatchEvent = (ViewDispatchRequest)joinPoint.getArgs()[0];
		
		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.setResourceURL(dispatchEvent.getViewURL());
		authRequest.setHttpRequest(dispatchEvent.getHttpRequest());
		authRequest.setHttpSession(dispatchEvent.getHttpSession());
		
		boolean authenticated = securityManager.isAuthenticated(authRequest);
		boolean authorized = false;
		try{
			if(authenticated){
				authorized = securityManager.isAuthorized(authRequest);
			}else 
				authorized = securityManager.isPublicView(dispatchEvent.getViewURL());
			String targetUrl = new StringBuilder(ViewProvider.SPECIAL_VIEWS_URL).append("?").append(ViewProvider.AUTHENTICATION_VIEW_PARAM_IDENTIFIER).toString();
			targetUrl = authorized ? dispatchEvent.getViewURL() : (authenticated ? securityManager.getForbiddenViewUrl() : targetUrl);
			
			printDebugInformation(authorized, dispatchEvent.getViewURL(), targetUrl);
			
			dispatchEvent.setViewURL(targetUrl);
			joinPoint.proceed(new Object[]{dispatchEvent});
		}catch(Throwable ex){
			throw new ViewDispatchException(ex);
		}
	}
	
	private void printDebugInformation(boolean authorized, String requestedUrl, String targetUrl){
		if(!authorized)
			LOGGER.warn("Detected Unauthorized access to resource {}, redirecting to {}", requestedUrl, targetUrl);
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
