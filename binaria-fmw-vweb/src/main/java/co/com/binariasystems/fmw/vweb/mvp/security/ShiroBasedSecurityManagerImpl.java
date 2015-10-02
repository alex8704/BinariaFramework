package co.com.binariasystems.fmw.vweb.mvp.security;

import static co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo.RESOURCE_URL_ARG;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;
import co.com.binariasystems.fmw.vweb.mvp.security.model.FMWSecurityException;

public class ShiroBasedSecurityManagerImpl implements SecurityManager {
	
	private ViewProvider viewProvider;
	
	public void setViewProvider(ViewProvider viewProvider) {
		this.viewProvider = viewProvider;
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
		Subject currentUser = SecurityUtils.getSubject();
		boolean authenticated = currentUser.isAuthenticated();
		return (getDashBoardViewUrl().equals(authInfo.getString(RESOURCE_URL_ARG)) || currentUser.isPermitted(RESOURCE_URL_ARG) || isPublicView(RESOURCE_URL_ARG));
	}

	@Override
	public void authenticate(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAuthenticated(AuthorizationAndAuthenticationInfo authInfo) {
		return SecurityUtils.getSubject().isAuthenticated();
	}

	@Override
	public void logout(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		SecurityUtils.getSubject().logout();
	}

}
