package co.com.binariasystems.fmw.security.crypto;

public class MatchingRequest {
	private String providedPassword;
	private String storedPassword;
	private String privateSalt;
	private boolean isHexEncoded;
	private String algorithmName;
	private int hashIterations;
	private String storedPasswordSalt;
	public String getProvidedPassword() {
		return providedPassword;
	}
	public void setProvidedPassword(String providedPassword) {
		this.providedPassword = providedPassword;
	}
	public String getStoredPassword() {
		return storedPassword;
	}
	public void setStoredPassword(String storedPassword) {
		this.storedPassword = storedPassword;
	}
	public String getPrivateSalt() {
		return privateSalt;
	}
	public void setPrivateSalt(String privateSalt) {
		this.privateSalt = privateSalt;
	}
	public boolean isHexEncoded() {
		return isHexEncoded;
	}
	public void setHexEncoded(boolean isHexEncoded) {
		this.isHexEncoded = isHexEncoded;
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	public int getHashIterations() {
		return hashIterations;
	}
	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}
	public String getStoredPasswordSalt() {
		return storedPasswordSalt;
	}
	public void setStoredPasswordSalt(String storedPasswordSalt) {
		this.storedPasswordSalt = storedPasswordSalt;
	}
	
	
}
