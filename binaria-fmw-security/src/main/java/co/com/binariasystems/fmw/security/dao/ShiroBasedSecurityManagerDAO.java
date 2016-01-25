package co.com.binariasystems.fmw.security.dao;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.security.resources.resources;
import co.com.binariasystems.fmw.security.util.SecConstants;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;

public class ShiroBasedSecurityManagerDAO implements SecurityManagerDAO {
	private static final Logger LOGGER  =  LoggerFactory.getLogger(ShiroBasedSecurityManagerDAO.class);
	
	private MessageBundleManager messageManager;
	private boolean supportAtmosphereWebSockets;
	private MessageFormat authExceptionFmt;
	
	public ShiroBasedSecurityManagerDAO() {
		messageManager = MessageBundleManager.forPath(SecConstants.DEFAULT_AUTH_MESSAGES_PATH, resources.class);
		authExceptionFmt = new MessageFormat("{0}: {1}");
	}

	@Override
	public boolean isAuthorized(AuthorizationRequest authRequest) {
		return getCurrentSecurityUser(authRequest.getHttpRequest()).isPermitted(authRequest.getResourceURL());
	}
	
	@Override
	public boolean isAuthenticated(AuthorizationRequest authRequest) {
		return getCurrentSecurityUser(authRequest.getHttpRequest()).isAuthenticated();
	}



	@Override
	public void authenticate(AuthenticationRequest authRequest) throws FMWSecurityException {
		Subject currentUser = getCurrentSecurityUser(authRequest.getHttpRequest());
		UsernamePasswordToken authToken = new UsernamePasswordToken(authRequest.getUsername(), 
				authRequest.getPassword(), 
				Boolean.TRUE.equals(authRequest.getRememberMe()),
				authRequest.getHost());
		try{
			currentUser.login(authToken);
		}catch(UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ex){
			LOGGER.error(ex.toString(), ex);
			throw new FMWSecurityException(messageManager.getString(ex.getClass().getSimpleName()+".localizedMessage"), ex);
		}catch(AuthenticationException ex){
			LOGGER.error(ex.toString(), ex);
			String message = authExceptionFmt.format(new String[]{messageManager.getString(ex.getClass().getSimpleName()+".localizedMessage"), ex.getMessage()});
			throw new FMWSecurityException(message, ex);
		}
	}

	@Override
	public void logout(AuthorizationRequest authRequest) {
		getCurrentSecurityUser(authRequest.getHttpRequest()).logout();
	}
	
	private Subject getCurrentSecurityUser(HttpServletRequest httpRequest){
		Subject currentSubject = (supportAtmosphereWebSockets && httpRequest != null) ? (Subject) httpRequest.getAttribute(SecConstants.ATMOSPHERE_SUBJECT_ATTRIBUTE) : null;
		return currentSubject != null ? currentSubject : SecurityUtils.getSubject();
	}

	public boolean isSupportAtmosphereWebSockets() {
		return supportAtmosphereWebSockets;
	}

	public void setSupportAtmosphereWebSockets(boolean supportAtmosphereWebSockets) {
		this.supportAtmosphereWebSockets = supportAtmosphereWebSockets;
	}

}
