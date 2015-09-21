package co.com.binariasystems.fmw.vweb.mvp.security;


public interface SecurityManagerDAO{
	//Esta interface debe ser implementada en la aplicacion que hara uso del Framework
	//Ya que cada aplicacion tendra su propio modelo de datos de Seguridad
	
	
	public boolean isAuthorized(String resourceUrl) throws Exception;
	
}
