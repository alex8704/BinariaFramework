
package co.com.binariasystems.fmw.vweb.mvp.security;

import static co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo.RESOURCE_URL_ARG;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;
import co.com.binariasystems.fmw.vweb.mvp.security.model.FMWSecurityException;

public class ShiroBasedSecurityManagerImpl implements SecurityManager {
	private ViewProvider viewProvider;
	private MessageBundleManager messageManager;
	
	
	public ShiroBasedSecurityManagerImpl(){
		messageManager = MessageBundleManager.forPath(VWebCommonConstants.DEFAULT_AUTH_MESSAGES_PATH, ShiroBasedSecurityManagerImpl.class);
	}
	
	public void setViewProvider(ViewProvider viewProvider) {
		this.viewProvider = viewProvider;
	}
	
	public void setMessageManager(MessageBundleManager messageManager) {
		this.messageManager = messageManager;
	}

	@Override
	public boolean isPublicView(String url) {
		return !getDashBoardViewUrl().equals(url) && viewProvider.isPublicView(url);
	}

	@Override
	public String getForbiddenViewUrl() {
		return viewProvider.getForbiddenViewUrl();
	}

	@Override
	public String getDashBoardViewUrl() {
		return viewProvider.getDashboardViewUrl();
	}

	@Override
	public boolean isAuthorized(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		Subject currentUser = getCurrentSecurityUser(authInfo);
		return (getDashBoardViewUrl().equals(authInfo.getString(RESOURCE_URL_ARG)) || isPublicView(RESOURCE_URL_ARG) || currentUser.isPermitted(RESOURCE_URL_ARG));
	}

	@Override
	public void authenticate(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		Subject currentUser = getCurrentSecurityUser(authInfo);
		UsernamePasswordToken authToken = new UsernamePasswordToken(
				authInfo.getString(AuthorizationAndAuthenticationInfo.USERNAME_ARG), 
				authInfo.getString(AuthorizationAndAuthenticationInfo.USERPASSWORD_ARG));
		
		try{
			currentUser.login(authToken);
		}catch(UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ex){
			throw new FMWSecurityException(messageManager.getString(ex.getClass().getSimpleName()+".localizedMessage"), ex);
		}catch(AuthenticationException ex){
			throw new FMWSecurityException("Has ocurred an unexpected exception while authenticate user", ex);
		}
	}

	@Override
	public boolean isAuthenticated(AuthorizationAndAuthenticationInfo authInfo) {
		return getCurrentSecurityUser(authInfo).isAuthenticated();
	}

	@Override
	public void logout(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		getCurrentSecurityUser(authInfo).logout();
	}
	
	
	private Subject getCurrentSecurityUser(AuthorizationAndAuthenticationInfo authInfo){
		Subject currentSubject = authInfo.get(AuthorizationAndAuthenticationInfo.SECURITY_SUBJECT_ARG, Subject.class);
		
		return currentSubject != null ? currentSubject : SecurityUtils.getSubject();
	}

}
