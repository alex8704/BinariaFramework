
package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import static co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants.SECURITY_SUBJECT_ATTRIBUTE;
import static co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo.RESOURCE_URL_ARG;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.vweb.mvp.event.RequestDispatchEvent;
import co.com.binariasystems.fmw.vweb.mvp.security.SecurityManager;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;

import com.vaadin.server.VaadinService;

public class DispatchEventInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatchEventInterceptor.class);
	
	private SecurityManager securityManager;
	private boolean dummy;
	
	public void intercept(ProceedingJoinPoint joinPoint) throws ViewDispatchException{
		RequestDispatchEvent dispatchEvent = (RequestDispatchEvent)joinPoint.getArgs()[0];
		String requestedUrl = dispatchEvent.getString(RequestDispatchEvent.URL_PROPERTY);
		
		
		AuthorizationAndAuthenticationInfo authInfo = new AuthorizationAndAuthenticationInfo()
		.set(RESOURCE_URL_ARG, requestedUrl)
		.set(AuthorizationAndAuthenticationInfo.SECURITY_SUBJECT_ARG, VaadinService.getCurrentRequest().getAttribute(SECURITY_SUBJECT_ATTRIBUTE));
		
		boolean authenticated = securityManager.isAuthenticated(authInfo);
		boolean authorized = false;
		try{
			if(authenticated){
				authorized = securityManager.isAuthorized(authInfo);
			}else 
				authorized = securityManager.isPublicView(requestedUrl);
			String targetUrl = new StringBuilder(ViewProvider.SPECIAL_VIEWS_URL).append("?").append(ViewProvider.AUTHENTICATION_VIEW_PARAM_IDENTIFIER).toString();
			targetUrl = authorized ? requestedUrl : (authenticated ? securityManager.getForbiddenViewUrl() : targetUrl);
			
			printDebugInformation(authorized, requestedUrl, targetUrl);
			
			dispatchEvent.set(RequestDispatchEvent.URL_PROPERTY, targetUrl);
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
