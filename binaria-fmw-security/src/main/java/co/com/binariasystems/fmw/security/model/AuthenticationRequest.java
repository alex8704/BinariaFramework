package co.com.binariasystems.fmw.security.model;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationRequest {
	private String username;
	private String password;
	private HttpServletRequest httpRequest;
	
	
	public AuthenticationRequest() {
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
}
