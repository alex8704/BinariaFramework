package co.com.binariasystems.fmw.security.dao;

import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;



public interface SecurityManagerDAO{
	//Esta interface debe ser implementada en la aplicacion que hara uso del Framework
	//Ya que cada aplicacion tendra su propio modelo de datos de Seguridad
	
	
	public boolean isAuthorized(AuthorizationRequest authRequest);
	public boolean isAuthenticated(AuthorizationRequest authRequest);
	public void authenticate(AuthenticationRequest authRequest) throws FMWSecurityException;
	public void logout(AuthorizationRequest authRequest);
	
}
