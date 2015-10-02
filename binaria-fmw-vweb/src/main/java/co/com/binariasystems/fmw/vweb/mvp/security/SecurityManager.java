package co.com.binariasystems.fmw.vweb.mvp.security;

import co.com.binariasystems.fmw.business.FMWBusiness;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;
import co.com.binariasystems.fmw.vweb.mvp.security.model.FMWSecurityException;

public interface SecurityManager extends FMWBusiness{
	public boolean isPublicView(String url);
	public String getForbiddenViewUrl();
	public String getDashBoardViewUrl();
	
	
	public void logout(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException;
	public void authenticate(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException;
	public boolean isAuthenticated(AuthorizationAndAuthenticationInfo authInfo);
	public boolean isAuthorized(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException;
}
