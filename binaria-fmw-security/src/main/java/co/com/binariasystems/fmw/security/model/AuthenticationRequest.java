package co.com.binariasystems.fmw.security.model;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationRequest {
	private String username;
	private String password;
	private HttpServletRequest httpRequest;
	private Boolean rememberMe;
	private String host;
	
	public AuthenticationRequest() {
	}
	
	public AuthenticationRequest(String username, String password, Boolean rememberMe, String host, HttpServletRequest httpRequest) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.httpRequest = httpRequest;
		this.host = host;
	}
	
	public AuthenticationRequest(String username, String password, Boolean rememberMe, HttpServletRequest httpRequest) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.httpRequest = httpRequest;
	}
	
	public AuthenticationRequest(String username, String password, HttpServletRequest httpRequest) {
		this.username = username;
		this.password = password;
		this.httpRequest = httpRequest;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}
	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * @return the rememberMe
	 */
	public Boolean getRememberMe() {
		return rememberMe;
	}

	/**
	 * @param rememberMe the rememberMe to set
	 */
	public void setRememberMe(Boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	
}
