package co.com.binariasystems.fmw.security.authc;

import co.com.binariasystems.fmw.security.FMWSecurityException;


public interface SecurityPrincipalConverter<R,M> {
	public M toPrincipalModel(R representation) throws FMWSecurityException;
	public R toPrincipalRepresentation(M model) throws FMWSecurityException;
}
