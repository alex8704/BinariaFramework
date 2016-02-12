package co.com.binariasystems.fmw.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;

public class ObjectUtils {
private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);
	
	private static final String NULL_STRING = "null";
	private static final String EMPTY_STRING = "";
	private static final ConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> classessPropertyDescCache = new ConcurrentHashMap<Class<?>, Map<String,PropertyDescriptor>>();
	
	/**
	 * Determine if the given objects are equal, returning {@code true}
	 * if both are {@code null} or {@code false} if only one is
	 * {@code null}.
	 * <p>Compares arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}
	
	/**
	 * Return a String representation of the specified Object.
	 * <p>Builds a String representation of the contents in case of an array.
	 * Returns {@code "null"} if {@code obj} is {@code null}.
	 * @param obj the object to build a String representation for
	 * @return a String representation of {@code obj}
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : EMPTY_STRING);
	}
	
	/**
	 * Determine whether the given array is empty:
	 * i.e. {@code null} or of zero length.
	 * @param array the array to check
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}
	
	
//	public static <F, T> T transferProperties(F sourceObject, T targetObject) throws FMWUncheckedException{
//		try {
//			BeanUtils.copyProperties(targetObject, sourceObject);
//		} catch (IllegalAccessException e) {
//			throw new FMWUncheckedException(e.getMessage(), e);
//		} catch (InvocationTargetException e) {
//			throw new FMWUncheckedException(e.getMessage(), e);
//		}
//		return targetObject;
//	}
//	
//	public static <F, T> T transferProperties(F sourceObject, Class<T> targetType) throws FMWUncheckedException{
//		T resp = null;
//		try {
//			resp = transferProperties(sourceObject, targetType.newInstance());
//		} catch (ReflectiveOperationException e) {
//			throw new FMWUncheckedException(e.getMessage(), e);
//		}
//		return resp;
//	}
//	
//	public static <F, T> List<T> transferPropertiesList(List<F> sourceList, Class<T> targetType) throws FMWUncheckedException{
//		try {
//			List<T> operationResult = new ArrayList<T>();
//			for(F item : sourceList){
//				operationResult.add(transferProperties(item, targetType.newInstance()));
//			}
//			return operationResult;
//		
//		} catch (ReflectiveOperationException e) {
//			throw new FMWUncheckedException(e.getMessage(), e);
//		}
//	}
	
	private static Map<String, PropertyDescriptor> putClassPropertiesInCacheAndReturn(Class<?> clazz){
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
		Map<String, PropertyDescriptor> descriptorsMap = new HashMap<String, PropertyDescriptor>();
		for(PropertyDescriptor descriptor : propertyDescriptors){
			descriptorsMap.put(descriptor.getName(), descriptor);
		}
		classessPropertyDescCache.putIfAbsent(clazz, descriptorsMap);
		return descriptorsMap;
	}
	
	public static <F, T> T transferProperties(F sourceObject, T targetObject) throws FMWUncheckedException{
		if(sourceObject == null) return (T)null;
		Map<String, PropertyDescriptor> sourceProps = classessPropertyDescCache.get(sourceObject.getClass());
		Map<String, PropertyDescriptor> targetProps = classessPropertyDescCache.get(targetObject.getClass());
		sourceProps = sourceProps != null ? sourceProps : putClassPropertiesInCacheAndReturn(sourceObject.getClass());
		targetProps = targetProps != null ? targetProps : putClassPropertiesInCacheAndReturn(targetObject.getClass());
		
		try {
			PropertyDescriptor sourcePropertyDesc = null;
			PropertyDescriptor targetPropertyDesc = null;
			for(String propertyName : sourceProps.keySet()){
				sourcePropertyDesc = sourceProps.get(propertyName);
				targetPropertyDesc = targetProps.get(propertyName);
				if(targetPropertyDesc == null || "class".equals(propertyName)) continue;
				
				if (PropertyUtils.isReadable(sourceObject, propertyName) && PropertyUtils.isWriteable(targetObject, propertyName)){
					//Caso especial en que dos propiedades se llaman igual pero son de diferentes clases con iguales atributos
					//Entonces se hace un mecanismo de copiado recursivo [Solo para clases propias de la aplicacion, que no hagan parte del API de java]
					if(!targetPropertyDesc.getPropertyType().isEnum() && !sourcePropertyDesc.getPropertyType().isEnum()
							&& !TypeHelper.isBasicType(targetPropertyDesc.getPropertyType()) && !TypeHelper.isBasicType(sourcePropertyDesc.getPropertyType())){
						Object value = PropertyUtils.getSimpleProperty(sourceObject, propertyName);
						Object target = transferProperties(value,targetPropertyDesc.getPropertyType());
						BeanUtils.copyProperty(targetObject, propertyName, target);
					}
					else if(!targetPropertyDesc.getPropertyType().isArray() && !Collection.class.isAssignableFrom(targetPropertyDesc.getPropertyType()) &&
							targetPropertyDesc.getPropertyType().isAssignableFrom(sourcePropertyDesc.getPropertyType()) ){
						Object value = PropertyUtils.getSimpleProperty(sourceObject, propertyName);
						UpperTransform upperTrans = FieldUtils.getField(sourceObject.getClass(), propertyName, true).getAnnotation(UpperTransform.class);
						boolean setUpper = (CharSequence.class.isAssignableFrom(sourcePropertyDesc.getPropertyType()) && upperTrans != null);
						
						BeanUtils.copyProperty(targetObject, propertyName, (setUpper && value != null ? StringUtils.upperCase(value.toString()) :value));
					}
				}
				
			}
		} catch (ReflectiveOperationException e) {
			throw new FMWUncheckedException(e.getMessage(), e);
		} 
		return targetObject;
	}
	
	public static <F, T> T transferProperties(F sourceObject, Class<T> targetType) throws FMWUncheckedException{
		T resp = null;
		try {
			resp = transferProperties(sourceObject, targetType.newInstance());
		} catch (ReflectiveOperationException e) {
			throw new FMWUncheckedException(e.getMessage(), e);
		}
		return resp;
	}
	
	public static <F, T> List<T> transferProperties(Iterable<F> sourceIterable, Class<T> targetType) throws FMWUncheckedException{
		if(sourceIterable == null) return null;
		try {
			List<T> operationResult = new ArrayList<T>();
			for(F item : sourceIterable){
				operationResult.add(transferProperties(item, targetType.newInstance()));
			}
			return operationResult;
		
		} catch (ReflectiveOperationException e) {
			throw new FMWUncheckedException(e.getMessage(), e);
		}
	}
	
	@Target(value = ElementType.FIELD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface UpperTransform{
	}
	
}
