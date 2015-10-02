package co.com.binariasystems.fmw.vweb.mvp.security.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationAndAuthenticationInfo implements Serializable {
	public static final String USERNAME_ARG = "USERNAME_ARG";
	public static final String RESOURCE_URL_ARG = "RESOURCE_URL_ARG";
	public static final String USERPASSWORD_ARG = "USERPASSWORD_ARG";
	
	protected Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public AuthorizationAndAuthenticationInfo set(String key, Object data) {
		dataMap.put(key, data);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T)dataMap.get(key);
	}
	
	public String getString(String key){
		return get(key, String.class);
	}
	
	public Date getDate(String key){
		return get(key, Date.class);
	}
	
	public Timestamp getTimestamp(String key){
		return get(key, Timestamp.class);
	}
	
	public BigDecimal getBigDecimal(String key){
		return get(key, BigDecimal.class);
	}
	
	public BigInteger getBigInteger(String key){
		return get(key, BigInteger.class);
	}
	
	public int getInt(String key){
		return get(key, Integer.TYPE);
	}
	
	public long getLong(String key){
		return get(key, Long.TYPE);
	}
	
	public float getFloat(String key){
		return get(key, Float.TYPE);
	}
	
	public double getDouble(String key){
		return get(key, Double.TYPE);
	}
	
	public short getShort(String key){
		return get(key, Short.TYPE);
	}
	
	public byte getByte(String key){
		return get(key, Byte.TYPE);
	}
}
