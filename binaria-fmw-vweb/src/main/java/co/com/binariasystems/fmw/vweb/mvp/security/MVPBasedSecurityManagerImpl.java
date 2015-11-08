
package co.com.binariasystems.fmw.vweb.mvp.security;

import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.dao.SecurityManagerDAO;
import co.com.binariasystems.fmw.security.mgt.SecurityManager;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;

public class MVPBasedSecurityManagerImpl implements SecurityManager{
	private ViewProvider viewProvider;
	private SecurityManagerDAO dao;
	
	
	public MVPBasedSecurityManagerImpl(){}
	
	public SecurityManagerDAO getDao() {
		return dao;
	}

	public void setDao(SecurityManagerDAO dao) {
		this.dao = dao;
	}

	public void setViewProvider(ViewProvider viewProvider) {
		this.viewProvider = viewProvider;
	}

	public boolean isPublicView(String url) {
		return !getDashBoardViewUrl().equals(url) && viewProvider.isPublicView(url);
	}

	public String getForbiddenViewUrl() {
		return viewProvider.getForbiddenViewUrl();
	}

	public String getDashBoardViewUrl() {
		return viewProvider.getDashboardViewUrl();
	}

	public boolean isAuthorized(AuthorizationRequest authRequest) {
		return (getDashBoardViewUrl().equals(authRequest.getResourceURL()) || isPublicView(authRequest.getResourceURL()) || dao.isAuthorized(authRequest));
	}

	public void authenticate(AuthenticationRequest authRequest) throws FMWSecurityException {
		dao.authenticate(authRequest);
	}

	public boolean isAuthenticated(AuthorizationRequest authRequest) {
		return dao.isAuthenticated(authRequest);
	}

	public void logout(AuthorizationRequest authRequest){
		dao.logout(authRequest);
	}

}
