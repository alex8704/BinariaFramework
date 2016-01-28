package co.com.binariasystems.fmw.vweb.mvp.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ViewDispatchRequest {
	private HttpServletRequest httpRequest;
	private String viewURL;
	private HttpSession httpSession;
	private boolean popup;
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}
	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	public String getViewURL() {
		return viewURL;
	}
	public void setViewURL(String viewURL) {
		this.viewURL = viewURL;
	}
	public HttpSession getHttpSession() {
		return httpSession;
	}
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	/**
	 * @return the popup
	 */
	public boolean isPopup() {
		return popup;
	}
	/**
	 * @param popup the popup to set
	 */
	public void setPopup(boolean popup) {
		this.popup = popup;
	}
	
	
}
