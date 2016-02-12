package co.com.binariasystems.fmw.reflec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;

public class TypeHelper {
	
	public static boolean isFloatingPointNumber(Class<?> clazz){
		return (BigDecimal.class.isAssignableFrom(clazz) ||
				Double.class.isAssignableFrom(clazz) || Double.TYPE.isAssignableFrom(clazz) || 
				Float.class.isAssignableFrom(clazz) || Float.TYPE.isAssignableFrom(clazz));
	}
	
	public static boolean isNotFloatingPointNumber(Class<?> clazz){
		return (BigInteger.class.isAssignableFrom(clazz) || 
				Byte.class.isAssignableFrom(clazz) || Byte.TYPE.isAssignableFrom(clazz) ||
				Short.class.isAssignableFrom(clazz) || Short.TYPE.isAssignableFrom(clazz) || 
				Integer.class.isAssignableFrom(clazz) || Integer.TYPE.isAssignableFrom(clazz) ||
				Long.class.isAssignableFrom(clazz) || Long.TYPE.isAssignableFrom(clazz));
	}

	public static boolean isBasicType(Class<?> clazz){
		return (clazz.isPrimitive() || Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || CharSequence.class.isAssignableFrom(clazz))
				|| clazz.getName().startsWith("java.") || clazz.getName().startsWith("com.sun") || clazz.getName().startsWith("com.oracle")
				|| clazz.getName().startsWith("sun.") || clazz.getName().startsWith("oracle.");
	}
	
	public static boolean isNotBasicType(Class clazz){
		return !isBasicType(clazz);
	}
	
	public static boolean isDateOrTimeType(Class clazz){
		return Date.class.isAssignableFrom(clazz);
	}
	
	public static boolean isNumericType(Class<?> clazz){
		return (Number.class.isAssignableFrom(clazz) || Long.TYPE.isAssignableFrom(clazz)
				 || Integer.TYPE.isAssignableFrom(clazz) || Double.TYPE.isAssignableFrom(clazz)
				 || Float.TYPE.isAssignableFrom(clazz) || Short.TYPE.isAssignableFrom(clazz)
				 || Byte.TYPE.isAssignableFrom(clazz));
	}
	
	public static boolean isCollectionType(Class clazz){
		return (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) ||
				Iterator.class.isAssignableFrom(clazz) || Enumeration.class.isAssignableFrom(clazz) || clazz.isArray());
	}
	
	
	public static <E extends Enum<E>> E enumFromOrdinal(int ordinal, Class<E> enumClass){
		E[] vals = enumClass.getEnumConstants();
		for(E value : vals)
			if(value.ordinal() == ordinal) return value;
		return null;
	}
	
	public static String objectToString(Object value){
		Object obj = null;
		if(value == null)
			obj = "";
		if(Boolean.TYPE.isAssignableFrom(value.getClass()) || Boolean.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Byte.TYPE.isAssignableFrom(value.getClass()) || Byte.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Short.TYPE.isAssignableFrom(value.getClass()) || Short.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Integer.TYPE.isAssignableFrom(value.getClass()) || Integer.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Long.TYPE.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Float.TYPE.isAssignableFrom(value.getClass()) || Float.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(Double.TYPE.isAssignableFrom(value.getClass()) || Double.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(BigInteger.class.isAssignableFrom(value.getClass()))
			obj = value;
		else if(BigDecimal.class.isAssignableFrom(value.getClass()))
			obj = ((BigDecimal)value).toPlainString();
		else if(Time.class.isAssignableFrom(value.getClass()))
			obj = new SimpleDateFormat(FMWConstants.SHORT_TIME_FORMAT).format((Time)value);
		else if(Timestamp.class.isAssignableFrom(value.getClass()))
			obj = new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format((Timestamp)value);
		else if(Date.class.isAssignableFrom(value.getClass()))
			obj = new SimpleDateFormat(FMWConstants.DATE_DEFAULT_FORMAT).format((Timestamp)value);
		if(Enum.class.isAssignableFrom(value.getClass()))
			obj = ((Enum)value).name().replaceAll("_", " ");
		if(Listable.class.isAssignableFrom(value.getClass()))
			obj = ((Listable)value).getDescription();
		else
			obj = value;
		
		return obj.toString();
	}

}
