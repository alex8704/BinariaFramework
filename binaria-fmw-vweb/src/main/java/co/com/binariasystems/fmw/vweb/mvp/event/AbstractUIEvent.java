package co.com.binariasystems.fmw.vweb.mvp.event;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUIEvent<ID_TYPE> implements UIEvent<ID_TYPE>{

	public AbstractUIEvent() {
	}
	
	protected Map<String, Object> dataMap = new HashMap<String, Object>();

	@Override
	public UIEvent<ID_TYPE> set(String key, Object data) {
		dataMap.put(key, data);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T get(String key, Class<T> clazz) {
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
