package co.com.binariasystems.fmw.security.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AuthorizationRequest {
	
	
	public AuthorizationRequest() {
	}
	public AuthorizationRequest(String resourceURL, HttpServletRequest httpRequest, HttpSession httpSession) {
		super();
		this.resourceURL = resourceURL;
		this.httpRequest = httpRequest;
		this.httpSession = httpSession;
	}
	private String resourceURL;
	private HttpServletRequest httpRequest;
	private HttpSession httpSession;
	public String getResourceURL() {
		return resourceURL;
	}
	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}
	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	public HttpSession getHttpSession() {
		return httpSession;
	}
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	
}
