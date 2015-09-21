package co.com.binariasystems.fmw.security.crypto.impl;

public class EncryptionResult {
	private String hexSalt;
	private String base64Salt;
	private byte[] saltBytes;
	private String algorithmName;
	private String encryptedValue;
	private byte[] encryptedBytes;
	public String getHexSalt() {
		return hexSalt;
	}
	public void setHexSalt(String hexSalt) {
		this.hexSalt = hexSalt;
	}
	public String getBase64Salt() {
		return base64Salt;
	}
	public void setBase64Salt(String base64Salt) {
		this.base64Salt = base64Salt;
	}
	public byte[] getSaltBytes() {
		return saltBytes;
	}
	public void setSaltBytes(byte[] saltBytes) {
		this.saltBytes = saltBytes;
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	public String getEncryptedValue() {
		return encryptedValue;
	}
	public void setEncryptedValue(String encryptedValue) {
		this.encryptedValue = encryptedValue;
	}
	public byte[] getEncryptedBytes() {
		return encryptedBytes;
	}
	public void setEncryptedBytes(byte[] encryptedBytes) {
		this.encryptedBytes = encryptedBytes;
	}
	
}
