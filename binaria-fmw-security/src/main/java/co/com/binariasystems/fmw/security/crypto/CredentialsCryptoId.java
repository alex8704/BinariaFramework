package co.com.binariasystems.fmw.security.crypto;

import co.com.binariasystems.fmw.security.crypto.impl.ShiroCredentialsCrypto;

public enum CredentialsCryptoId {
	SHIRO{
		@Override
		Class<? extends CredentialsCrypto> implementationClazz() {
			return ShiroCredentialsCrypto.class;
		}
	};
	
	abstract Class<? extends CredentialsCrypto> implementationClazz();
}
