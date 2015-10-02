package co.com.binariasystems.fmw.vweb.mvp.security;

import static co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo.RESOURCE_URL_ARG;

import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;
import co.com.binariasystems.fmw.vweb.mvp.security.model.FMWSecurityException;

public class SecurityManagerImpl implements SecurityManager{
	
	private ViewProvider viewProvider;
	private SecurityManagerDAO dao;
	
	public ViewProvider getViewProvider() {
		return viewProvider;
	}

	public void setViewProvider(ViewProvider viewProvider) {
		this.viewProvider = viewProvider;
	}

	public SecurityManagerDAO getDao() {
		if(dao == null)
			dao = new SecurityManagerDAODummyImpl();
		return dao;
	}

	public void setDao(SecurityManagerDAO dao) {
		this.dao = dao;
	}

	@Override
	public boolean isPublicView(String url) {
		return viewProvider.isPublicView(url);
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
	public void logout(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void authenticate(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAuthenticated(AuthorizationAndAuthenticationInfo authInfo) {
		return getDao().isAuthorized(authInfo.getString(RESOURCE_URL_ARG));
	}

	@Override
	public boolean isAuthorized(AuthorizationAndAuthenticationInfo authInfo) throws FMWSecurityException {
		// TODO Auto-generated method stub
		return false;
	}


}
