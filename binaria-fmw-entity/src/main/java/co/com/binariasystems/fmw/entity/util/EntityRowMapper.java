package co.com.binariasystems.fmw.entity.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.cfg.EnumKeyProperty;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;

/*
 * Clase usada como soporte por {@link MasterCRUDDAOImpl}, para la persistencia de datos
 * 
 * @author Alexander Castro O.
 */

public class EntityRowMapper<T> implements RowMapper<T>{
	private EntityConfigData<T> entityConfigData;
	
	public EntityRowMapper(EntityConfigData<T> entityConfigData){
		this.entityConfigData = entityConfigData;
	}
	
	public T mapRow(ResultSet rs, int rowIndex) throws SQLException {
		T bean = (T) newInstanceOf(entityConfigData.getEntityClass()); 
		for(String fieldName : entityConfigData.getFieldNames()){
			FieldConfigData fieldCfg = entityConfigData.getFieldData(fieldName);
			
			try {
				PropertyUtils.setProperty(bean, fieldName, mapEntityField(fieldCfg, null, rs, true));
			} catch (ReflectiveOperationException e) {
				throw new FMWUncheckedException("Cannot set value for field '"+fieldCfg.getFieldName()+"' of entity "+entityConfigData.getEntityClass(), e);
			} catch(Exception e){
				if(e instanceof RuntimeException)
					throw (RuntimeException)e;
				if(e instanceof SQLException)
					throw (SQLException)e;
				throw new FMWUncheckedException("Cannot set value for field '"+fieldCfg.getFieldName()+"' of entity "+entityConfigData.getEntityClass(), e);
			}
		}
		return bean;
	}
	
	
	@SuppressWarnings("unchecked")
	private Object mapEntityField(FieldConfigData fieldCfg, String aliasPrefix, ResultSet rs, boolean includeForeigns) throws Exception{
		Object fieldValue = null;
		String columnAlias = StringUtils.defaultIfEmpty(aliasPrefix, "") + fieldCfg.getFieldName();
		if(fieldCfg.isEnumType() && entityConfigData.getEnumKeyProperty() == EnumKeyProperty.NAME){
			if(!StringUtils.isEmpty(rs.getString(columnAlias)))
				fieldValue = Enum.valueOf((Class)fieldCfg.getFieldType(), rs.getString(columnAlias));
		}else if(fieldCfg.isEnumType() && entityConfigData.getEnumKeyProperty() == EnumKeyProperty.ORDINAL){
			if(!StringUtils.isEmpty(rs.getString(columnAlias)))
				fieldValue = TypeHelper.enumFromOrdinal(rs.getInt(columnAlias), (Class)fieldCfg.getFieldType());
		}
		else if(fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())){
			Object fieldBean = newInstanceOf(fieldCfg.getFieldType());
			EntityConfigurator<?> fieldEntityConfigurator = EntityConfigurationManager.getInstance().getConfigurator(fieldCfg.getFieldType());
			EntityConfigData<?> fieldEntityConfigData = null;
			try {
				fieldEntityConfigData = fieldEntityConfigurator.configure();
			} catch (Exception e) {
				throw new FMWUncheckedException("Cannot set value for field '"+fieldCfg.getFieldName()+"' of entity "+entityConfigData.getEntityClass(), e);
			}
			
			if(!includeForeigns){
				FieldConfigData fieldBeanConfig = fieldEntityConfigData.getFieldData(fieldEntityConfigData.getPkFieldName());
				PropertyUtils.setNestedProperty(fieldBean, fieldEntityConfigData.getPkFieldName(), determineTypeAndGetValueFromRs(rs, columnAlias, fieldBeanConfig.getFieldType()));
			}else{
				String recursivePrefix = fieldCfg.getFieldName()+"_";
				Object recursiveValue = null;
				for(String fieldName : fieldEntityConfigData.getFieldNames()){
					recursiveValue = mapEntityField(fieldEntityConfigData.getFieldData(fieldName), recursivePrefix, rs, false);
					PropertyUtils.setProperty(fieldBean, fieldName, recursiveValue);
				}
			}
			fieldValue = fieldBean;
		}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType())){
			String listablePk = rs.getString(columnAlias);
			if(fieldCfg.getFixedValues() != null && !StringUtils.isEmpty(listablePk)){
				for(Listable fixedValue : fieldCfg.getFixedValues())
					if(fixedValue.getPK().equals(listablePk)){
						fieldValue = fixedValue;
						break;
					}
			}
		}
		else
			fieldValue =  determineTypeAndGetValueFromRs(rs, columnAlias, fieldCfg.getFieldType());
		return fieldValue;
	}
	
	private <C> C newInstanceOf(Class<C> clazz){
		C bean = null; 
		try {
			bean = clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new FMWUncheckedException("Cannot create instance of class "+entityConfigData.getEntityClass(), e);
		}
		return bean;
	}
	
	private Object determineTypeAndGetValueFromRs(ResultSet rs, String columnName, Class<?> expectedType) throws SQLException{
		Object obj = null;
		if(Boolean.TYPE.isAssignableFrom(expectedType) || Boolean.class.isAssignableFrom(expectedType))
			obj = rs.getBoolean(columnName);
		else if(Byte.TYPE.isAssignableFrom(expectedType) || Byte.class.isAssignableFrom(expectedType))
			obj = rs.getByte(columnName);
		else if(Short.TYPE.isAssignableFrom(expectedType) || Short.class.isAssignableFrom(expectedType))
			obj = rs.getShort(columnName);
		else if(Integer.TYPE.isAssignableFrom(expectedType) || Integer.class.isAssignableFrom(expectedType))
			obj = rs.getInt(columnName);
		else if(Long.TYPE.isAssignableFrom(expectedType) || Long.class.isAssignableFrom(expectedType))
			obj = rs.getLong(columnName);
		else if(Float.TYPE.isAssignableFrom(expectedType) || Float.class.isAssignableFrom(expectedType))
			obj = rs.getFloat(columnName);
		else if(Double.TYPE.isAssignableFrom(expectedType) || Double.class.isAssignableFrom(expectedType))
			obj = rs.getDouble(columnName);
		else if(BigInteger.class.isAssignableFrom(expectedType))
			obj = rs.getBigDecimal(columnName);
		else if(BigDecimal.class.isAssignableFrom(expectedType))
			obj = rs.getBigDecimal(columnName);
		else if(Timestamp.class.isAssignableFrom(expectedType))
			obj = rs.getTimestamp(columnName);
		else if(java.sql.Date.class.isAssignableFrom(expectedType))
			obj = rs.getDate(columnName);
		else if(Time.class.isAssignableFrom(expectedType))
			obj = rs.getTime(columnName);
		else if(Date.class.isAssignableFrom(expectedType))
			obj = rs.getDate(columnName);
		else
			obj = rs.getString(columnName);
		
		return obj;
	}
	
}
