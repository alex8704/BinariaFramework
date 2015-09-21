package co.com.binariasystems.fmw.vweb.mvp.security;

import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;

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
	public boolean isAuthorized(String resourceUrl) throws Exception {
		return getDao().isAuthorized(resourceUrl);
	}

}
