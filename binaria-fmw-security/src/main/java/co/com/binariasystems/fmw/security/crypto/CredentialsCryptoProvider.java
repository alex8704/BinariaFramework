package co.com.binariasystems.fmw.security.crypto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CredentialsCryptoProvider {
	private CredentialsCryptoProvider(){}
	
	public static CredentialsCrypto get(CredentialsCryptoId id){
		Method getInstanceMtd = null;
		CredentialsCrypto resp = null;
		try {
			getInstanceMtd = id.implementationClazz().getMethod("getInstance");
			resp = (CredentialsCrypto) getInstanceMtd.invoke(null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			resp = null;
		}
		return resp;
	}
}
