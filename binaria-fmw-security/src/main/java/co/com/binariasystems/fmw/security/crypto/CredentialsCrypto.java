package co.com.binariasystems.fmw.security.crypto;

import co.com.binariasystems.fmw.security.crypto.impl.EncryptionResult;

public interface CredentialsCrypto {
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";
	public static final String MD2 = "MD2";
	public static final String MD5 = "MD5";
	
	public String encryptPasswordSimple(EncryptionRequest request);
	public EncryptionResult encryptPassword(EncryptionRequest request);
	public String objectToBase64(Object source);
	
	public boolean credentialsMatches(MatchingRequest request);
	
}
