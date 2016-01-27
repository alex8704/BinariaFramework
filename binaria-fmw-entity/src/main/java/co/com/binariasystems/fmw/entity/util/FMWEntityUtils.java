package co.com.binariasystems.fmw.entity.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;

public class FMWEntityUtils {
	
	private static String showOpeationsSql;
	
	
	public static EntityConfigData<?> getEntityConfig(Class<?> entityClazz) throws FMWException{
		EntityConfigData<?> entityConfigData = EntityConfigurationManager.getInstance().getConfigurator(entityClazz).configure();
		return entityConfigData;
	}
	
	
	/**
	 * Genera un String basandose en la confiuracion de una entidad, tomando los valores
	 * de los campos definidos con la propiedad {@link co.com.binariasystems.fmw.entity.annot.SearcherConfig#descriptionFields()}  
	 * 
	 * @param fieldValue
	 * @param fieldCfg
	 * @return
	 * @throws Exception
	 */
	public static String generateStringRepresentationForField(Object fieldValue) throws Exception{
		return generateStringRepresentationForField(fieldValue, FMWConstants.WHITE_SPACE);
	}
	
	public static String generateStringRepresentationForField(Object fieldValue, String separator) throws FMWException{
		if(fieldValue == null)
			return "";
		StringBuilder resp = new StringBuilder();
		
		try{
			if(fieldValue instanceof Listable)
				resp.append(((Listable) fieldValue).getDescription());
			else if(isEntityClass(fieldValue.getClass())){
				EntityConfigData<?> entityConfigData = getEntityConfig(fieldValue.getClass());
				String subFieldValue = null;
				
				for(String fieldName : entityConfigData.getSearchDescriptionFields()){
					FieldConfigData subFieldCfg = entityConfigData.getFieldData(fieldName);
					subFieldValue = generateStringRepresentationForField(PropertyUtils.getProperty(fieldValue, subFieldCfg.getFieldName()), separator);
					if(StringUtils.isNotBlank(subFieldValue)){
						resp.append((resp.length() > 0) ? StringUtils.defaultIfBlank(separator, FMWConstants.WHITE_SPACE) : "");
						resp.append(subFieldValue);
					}
				}
				resp.append(entityConfigData.getSearchDescriptionFields().size() > 0 ? "" : TypeHelper.objectToString(fieldValue));
			}else	//BasicTypes, Enums, Collections
				resp.append(TypeHelper.objectToString(fieldValue));
		}catch(ReflectiveOperationException ex){
			Throwable cause = FMWExceptionUtils.prettyMessageException(ex);
			throw new FMWException(cause.getMessage(), cause);
		}
		
		return resp.toString();
	}
	
	public static boolean isEntityClass(Class<?> clazz){
		return TypeHelper.isNotBasicType(clazz) && clazz.getAnnotation(Entity.class) != null;
	}
	
	public static boolean isValidControlForField(FieldConfigData fieldInfo){
		boolean resp = false;
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.TEXTFIELD || fieldInfo.getFieldUIControl() == EntityConfigUIControl.PASSWORDFIELD){
			resp = TypeHelper.isNumericType(fieldInfo.getFieldType()) || CharSequence.class.isAssignableFrom(fieldInfo.getFieldType()) ||
					Character.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.TEXTAREA){
			resp = CharSequence.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.DATEFIELD){
			resp = Date.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.RADIO){
			resp = fieldInfo.isEnumType() || (fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0);
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.COMBOBOX){
			resp = fieldInfo.isEnumType() || fieldInfo instanceof RelationFieldConfigData ||
					(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0);
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.SEARCHBOX){
			resp = fieldInfo instanceof RelationFieldConfigData;
		}
		if(fieldInfo.getFieldUIControl() == EntityConfigUIControl.CHECKBOX){
			resp = Boolean.class.isAssignableFrom(fieldInfo.getFieldType());
		}
	
		return resp;
	}
	
	/**
	 * Metodo usado internamente anter de crear los componentes graficos, para definir
	 * el orden de generacion de cada campo teniendo en cuenta un mecanismo de prioridades
	 * que permite ubicar de la mejor manera los elementos segun el tipo de campo a generar
	 * 
	 * @param fieldsDataMap
	 * @param PKFieldName
	 * @return
	 */
	public static List<FieldConfigData> sortByUIControlTypePriority(Map<String, FieldConfigData> fieldsDataMap, String PKFieldName){
		List<FieldConfigData> resp = new ArrayList<EntityConfigData.FieldConfigData>();
		int index = 0;
		for(String fieldName : fieldsDataMap.keySet()){
			FieldConfigData fieldCfgdData = fieldsDataMap.get(fieldName);
			if(fieldCfgdData.isAuditoryField()) continue;
			if(fieldName.equals(PKFieldName))
				resp.add(0, fieldCfgdData);
			else{
				for(index = 0; index < resp.size(); index++)
					if(fieldCfgdData.getFieldUIControl().getPriority() < resp.get(index).getFieldUIControl().getPriority()) break;
				resp.add(index, fieldCfgdData);
			}
		}
		
		return resp;
	}
	
	public static boolean showOpeationsSql(){
		if(showOpeationsSql == null){
			showOpeationsSql = StringUtils.defaultIfEmpty(IOCHelper.getBean(FMWEntityConstants.ENTITY_OPERATIONS_SHOWSQL_IOC_KEY, String.class), "false").toLowerCase();
		}
		return showOpeationsSql.equals(Boolean.TRUE.toString());
	}
	
	public static String getEntityLabelsFormat(){
		return FMWEntityConstants.ENTITY_CONVENTION_LABELS_FMT;
	}
	
	public static String getEntityFormTitleFormat(){
		return FMWEntityConstants.ENTITY_CONVENTION_FORM_TITLE_FMT;
	}
	
	public static MessageFormat createEntityLabelsMessageFormat(){
		return new MessageFormat(getEntityLabelsFormat());
	}
	
	public static MessageFormat createEntityFormTitleMessageFormat(){
		return new MessageFormat(getEntityFormTitleFormat());
	}
	
	
}	
		
		
		
		
		
		