package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;


public interface ViewProvider {
	public static final String SPECIAL_VIEWS_URL = "/";
	public static final String AUTHENTICATION_VIEW_PARAM_IDENTIFIER = "authentication-view";
	public static final String DASHBOARD_VIEW_PARAM_IDENTIFIER = "dashboard-view";
	public static final String FORBIDDEN_VIEW_PARAM_IDENTIFIER = "http-403-view";
	public static final String RESNOTFOUND_VIEW_PARAM_IDENTIFIER = "http-404-view";
	
	
	public ViewAndController getView(RequestData request) throws FMWException;
	public boolean isPublicView(String request);
	public ViewInfo getViewInfo(RequestData request);
	public String getDashboardViewUrl();
	public String getForbiddenViewUrl();
	public void configure() throws ViewConfigurationException;
	public String getViewUrlByClass(Class<?> viewClass);
	
}
