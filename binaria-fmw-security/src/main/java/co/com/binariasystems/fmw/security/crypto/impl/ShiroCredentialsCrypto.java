package co.com.binariasystems.fmw.security.crypto.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.HashFormatFactory;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import co.com.binariasystems.fmw.security.authc.credential.ShiroBasedHashedCredentialsMatcher;
import co.com.binariasystems.fmw.security.crypto.CredentialsCrypto;
import co.com.binariasystems.fmw.security.crypto.CredentialsCryptoId;
import co.com.binariasystems.fmw.security.crypto.CredentialsCryptoProvider;
import co.com.binariasystems.fmw.security.crypto.EncryptionRequest;
import co.com.binariasystems.fmw.security.crypto.MatchingRequest;

public class ShiroCredentialsCrypto extends CodecSupport implements CredentialsCrypto {
	private static CredentialsCrypto instance;
	private HashFormatFactory hashFormatFactory;
	private HashFormat hexFormat;
	private HashFormat base64Format;
	
	private ShiroCredentialsCrypto(){
		hashFormatFactory = new DefaultHashFormatFactory();
		hexFormat = hashFormatFactory.getInstance("hex");
		base64Format = hashFormatFactory.getInstance("base64");
	}
	
	public static CredentialsCrypto getInstance(){
		if(instance == null){
			synchronized (ShiroCredentialsCrypto.class) {
				instance = new ShiroCredentialsCrypto();
			}
		}
		return instance;
	}

	@Override
	public String encryptPasswordSimple(EncryptionRequest request) {
		return encryptPassword(request).getEncryptedValue();
	}

	@Override
	public EncryptionResult encryptPassword(EncryptionRequest request) {
		DefaultHashService hashService = new DefaultHashService();
		hashService.setHashIterations(request.getHashIterations() <= 0 ? 1 : request.getHashIterations()); 
		hashService.setHashAlgorithmName(request.getAlgorithmName());
		hashService.setPrivateSalt(new SimpleByteSource(Base64.decodeToString(StringUtils.defaultString(request.getPrivateSalt()))));
		hashService.setGeneratePublicSalt(request.isGeneratePublicSalt());

		DefaultPasswordService passwordService = new DefaultPasswordService();
		passwordService.setHashService(hashService);
		Hash hash = passwordService.hashPassword(request.getSource());
		
		EncryptionResult result = new EncryptionResult();
		if(hash != null){
			result.setAlgorithmName(hash.getAlgorithmName());
			result.setBase64Salt(hash.getSalt().toBase64());
			result.setHexSalt(hash.getSalt().toHex());
			result.setEncryptedValue(request.isHexFormat() ? hexFormat.format(hash) : base64Format.format(hash));
			result.setEncryptedBytes(hash.getBytes());
		}
		
		return result;
	}

	@Override
	public String objectToBase64(Object source) {
		return toByteSource(source).toBase64();
	}
	
	protected ByteSource toByteSource(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof ByteSource) {
            return (ByteSource) o;
        }
        byte[] bytes = toBytes(o);
        return ByteSource.Util.bytes(bytes);
    }

	@Override
	public boolean credentialsMatches(MatchingRequest request) {
		ShiroBasedHashedCredentialsMatcher cm = new ShiroBasedHashedCredentialsMatcher(request.getAlgorithmName());
		
		cm.setPrivateSalt(new SimpleByteSource(Base64.decodeToString( StringUtils.defaultString(request.getPrivateSalt()) )));
		cm.setHashIterations(request.getHashIterations());
		cm.setStoredCredentialsHexEncoded(request.isHexEncoded());
		
		return cm.doCredentialsMatch(request);
	}
	
	public static void main(String[] args) {
		CredentialsCrypto crypto = CredentialsCryptoProvider.get(CredentialsCryptoId.SHIRO);
		EncryptionRequest request = new EncryptionRequest();
		request.setAlgorithmName(CredentialsCrypto.SHA256);
		request.setGeneratePublicSalt(true);
		request.setHashIterations(50000);
		request.setHexFormat(true);
		request.setPrivateSalt("dzNidDNzdDRwcDUzY3VyM3A0NTV3MHJkc2FsdA==");
		request.setSource("Gana1111");

		EncryptionResult resp = crypto.encryptPassword(request);
		System.out.println("Valor encriptado: "+resp.getEncryptedValue());
		System.out.println("SaltBase64: "+resp.getBase64Salt());
		System.out.println("SaltHex: "+resp.getHexSalt());
		System.out.println("Algoritmo: "+resp.getAlgorithmName());
		System.out.println();
		System.out.println("---------------------------------------");
		System.out.println();
		MatchingRequest mreq = new  MatchingRequest();
		mreq.setAlgorithmName(CredentialsCrypto.SHA256);
		mreq.setHashIterations(50000);
		mreq.setHexEncoded(true);
		mreq.setPrivateSalt("dzNidDNzdDRwcDUzY3VyM3A0NTV3MHJkc2FsdA==");
		mreq.setProvidedPassword("Gana1111");
		mreq.setStoredPassword(resp.getEncryptedValue());
		mreq.setStoredPasswordSalt(resp.getHexSalt());
		
		System.out.println("Matches?: '"+mreq.getProvidedPassword()+"' with '"+mreq.getStoredPassword()+"'? "+crypto.credentialsMatches(mreq));
		
		
	}

}
