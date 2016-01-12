package co.com.binariasystems.fmw.security.authc.credential;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.AbstractHash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import co.com.binariasystems.fmw.security.crypto.MatchingRequest;

public class ShiroBasedHashedCredentialsMatcher extends HashedCredentialsMatcher{
	
	/**
     * The 'private' part of the hash salt.
     */
    private ByteSource privateSalt;
	
	public ShiroBasedHashedCredentialsMatcher() {
		super();
	}

	public ShiroBasedHashedCredentialsMatcher(String hashAlgorithmName) {
		super(hashAlgorithmName);
	}
	
	public boolean doCredentialsMatch(MatchingRequest request){
		Object tokenHashedCredentials = hashProvidedCredentials(request);
        Object accountCredentials = getCredentials(request);
        return equals(tokenHashedCredentials, accountCredentials);
	}
	
	protected Object getCredentials(MatchingRequest request) {
        Object credentials = StringUtils.defaultString(request.getStoredPassword()).toCharArray();

        byte[] storedBytes = toBytes(credentials);

        if (credentials instanceof String || credentials instanceof char[]) {
            //account.credentials were a char[] or String, so
            //we need to do text decoding first:
            if (isStoredCredentialsHexEncoded()) {
                storedBytes = Hex.decode(storedBytes);
            } else {
                storedBytes = Base64.decode(storedBytes);
            }
        }
        SimpleHash hash = (SimpleHash)newHashInstance();
        hash.setBytes(storedBytes);
        return hash;
    }
	
	private Object hashProvidedCredentials(MatchingRequest request){
		ByteSource salt = StringUtils.isEmpty(request.getStoredPasswordSalt()) ? null : ByteSource.Util.bytes(request.getStoredPasswordSalt());
		return hashProvidedCredentials(request.getProvidedPassword(), salt);
	}
	
	@Override
	protected Object hashProvidedCredentials(AuthenticationToken token, AuthenticationInfo info) {
        Object passwordSalt = null;
        if (info instanceof SaltedAuthenticationInfo) {
        	passwordSalt = ((SaltedAuthenticationInfo) info).getCredentialsSalt();
        }
        return hashProvidedCredentials(token.getCredentials(), passwordSalt);
    }
	
	private Object hashProvidedCredentials(Object providedCredentials, Object credentialsSalt){
		ByteSource publicSaltPart = null;
		if(credentialsSalt != null){
			if(isStoredCredentialsHexEncoded())
        		publicSaltPart = ByteSource.Util.bytes(Hex.decode(convertSaltToBytes(credentialsSalt).getBytes()));
        	else
        		publicSaltPart = ByteSource.Util.bytes(Base64.decode(convertSaltToBytes(credentialsSalt).getBytes()));
		}
		ByteSource combined = combine(privateSalt, publicSaltPart);
        
        return hashProvidedCredentials(providedCredentials, combined, getHashIterations());
	}
	
	/**
     * Acquires the specified {@code salt} argument's bytes and returns them in the form of a {@code ByteSource} instance.
     * <p/>
     * This implementation merely delegates to the convenience {@link #toByteSource(Object)} method for generic
     * conversion.  Can be overridden by subclasses for salt-specific conversion.
     *
     * @param salt the salt to be use for the hash.
     * @return the salt's bytes in the form of a {@code ByteSource} instance.
     * @since 1.2
     */
    protected ByteSource convertSaltToBytes(Object salt) {
        return toByteSource(salt);
    }

    /**
     * Converts a given object into a {@code ByteSource} instance.  Assumes the object can be converted to bytes.
     *
     * @param o the Object to convert into a {@code ByteSource} instance.
     * @return the {@code ByteSource} representation of the specified object's bytes.
     * @since 1.2
     */
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
	
	/**
     * Combines the specified 'private' salt bytes with the specified additional extra bytes to use as the
     * total salt during hash computation.  {@code privateSaltBytes} will be {@code null} }if no private salt has been
     * configured.
     *
     * @param privateSalt the (possibly {@code null}) 'private' salt to combine with the specified extra bytes
     * @param publicSalt  the extra bytes to use in addition to the given private salt.
     * @return a combination of the specified private salt bytes and extra bytes that will be used as the total
     *         salt during hash computation.
     */
    protected ByteSource combine(ByteSource privateSalt, ByteSource publicSalt) {

        byte[] privateSaltBytes = privateSalt != null ? privateSalt.getBytes() : null;
        int privateSaltLength = privateSaltBytes != null ? privateSaltBytes.length : 0;

        byte[] publicSaltBytes = publicSalt != null ? publicSalt.getBytes() : null;
        int extraBytesLength = publicSaltBytes != null ? publicSaltBytes.length : 0;

        int length = privateSaltLength + extraBytesLength;

        if (length <= 0) {
            return null;
        }

        byte[] combined = new byte[length];

        int i = 0;
        for (int j = 0; j < privateSaltLength; j++) {
            assert privateSaltBytes != null;
            combined[i++] = privateSaltBytes[j];
        }
        for (int j = 0; j < extraBytesLength; j++) {
            assert publicSaltBytes != null;
            combined[i++] = publicSaltBytes[j];
        }

        return ByteSource.Util.bytes(combined);
    }

	public ByteSource getPrivateSalt() {
		return privateSalt;
	}

	public void setPrivateSalt(ByteSource privateSalt) {
		this.privateSalt = privateSalt;
	}
}
