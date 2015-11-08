package co.com.binariasystems.fmw.security.mgt;

import co.com.binariasystems.fmw.business.FMWBusiness;
import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;


public interface SecurityManager extends FMWBusiness{
	public boolean isPublicView(String url);
	public String getForbiddenViewUrl();
	public String getDashBoardViewUrl();
	
	
	public void logout(AuthorizationRequest authInfo);
	public void authenticate(AuthenticationRequest authInfo) throws FMWSecurityException;
	public boolean isAuthenticated(AuthorizationRequest authInfo);
	public boolean isAuthorized(AuthorizationRequest authInfo);
}
