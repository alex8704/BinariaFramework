package co.com.binariasystems.fmw.security.dao;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.security.resources.resources;
import co.com.binariasystems.fmw.security.util.SecConstants;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;

public class ShiroBasedSecurityManagerDAO implements SecurityManagerDAO {
	
	private MessageBundleManager messageManager;
	private boolean supportAtmosphereWebSockets;
	
	public ShiroBasedSecurityManagerDAO() {
		messageManager = MessageBundleManager.forPath(SecConstants.DEFAULT_AUTH_MESSAGES_PATH, resources.class);
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
		UsernamePasswordToken authToken = new UsernamePasswordToken(authRequest.getUsername(), authRequest.getPassword());
		try{
			currentUser.login(authToken);
		}catch(UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ex){
			throw new FMWSecurityException(messageManager.getString(ex.getClass().getSimpleName()+".localizedMessage"), ex);
		}catch(AuthenticationException ex){
			throw new FMWSecurityException("Has ocurred an unexpected exception while authenticate user", ex);
		}
	}

	@Override
	public void logout(AuthorizationRequest authRequest) {
		getCurrentSecurityUser(authRequest.getHttpRequest()).logout();
	}
	
	private Subject getCurrentSecurityUser(HttpServletRequest httpRequest){
		Subject currentSubject = supportAtmosphereWebSockets ? (Subject) httpRequest.getAttribute(SecConstants.ATMOSPHERE_SUBJECT_ATTRIBUTE) : null;
		return currentSubject != null ? currentSubject : SecurityUtils.getSubject();
	}

	public boolean isSupportAtmosphereWebSockets() {
		return supportAtmosphereWebSockets;
	}

	public void setSupportAtmosphereWebSockets(boolean supportAtmosphereWebSockets) {
		this.supportAtmosphereWebSockets = supportAtmosphereWebSockets;
	}

}
