package co.com.binariasystems.fmw.entity.util;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.Entity;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;

public class FMWEntityUtils {
	
	private static String showOpeationsSql;
	private static final String ENTITY_FORM_TITLE_FMT = "entity.{0}.form.title";
	private static final String ENTITY_LABELS_FMT = "entity.{0}.{1}.caption";
	
	
	public static EntityConfigData getEntityConfig(Class entityClazz) throws FMWException{
		EntityConfigurator configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClazz);
		EntityConfigData entityConfigData = configurator.configure();
		return entityConfigData;
	}
	
	
	/**
	 * Genera un String basandose en la confiuracion de una entidad, tomando los valores
	 * de los campos definidos con la propiedad {@link co.com.binariasystems.fmw.entity.SearchTarget#descriptionFields()}  
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
			if(TypeHelper.isBasicType(fieldValue.getClass()) || TypeHelper.isCollectionType(fieldValue.getClass()) 
					|| Enum.class.isAssignableFrom(fieldValue.getClass()))
				resp.append(TypeHelper.objectToString(fieldValue));
			else if(!TypeHelper.isBasicType(fieldValue.getClass()) && fieldValue.getClass().isAnnotationPresent(Entity.class)){//No est tipo basico y es Entidad
				EntityConfigData entityConfigData = getEntityConfig(fieldValue.getClass());
				boolean hasDescriptionFields = entityConfigData.getSearchDescriptionFields() != null && entityConfigData.getSearchDescriptionFields().size() > 0;
				String subFieldValue = null;
				
				if(hasDescriptionFields){
					for(int i = 0; i < entityConfigData.getSearchDescriptionFields().size(); i++){
						String fieldName = entityConfigData.getSearchDescriptionFields().get(i);
						FieldConfigData subFieldCfg = entityConfigData.getFieldsData().get(fieldName);
						if(subFieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(subFieldCfg.getFieldType())){
							subFieldValue = generateStringRepresentationForField(PropertyUtils.getProperty(fieldValue, subFieldCfg.getFieldName()), separator);
						}else{
							subFieldValue = TypeHelper.objectToString(PropertyUtils.getProperty(fieldValue, subFieldCfg.getFieldName()));
						}
						if(StringUtils.isNotBlank(subFieldValue)){
							if(resp.length() > 0)
								resp.append(StringUtils.defaultIfBlank(separator, FMWConstants.WHITE_SPACE));
							resp.append(subFieldValue);
						}
					}
				}else
					resp.append(TypeHelper.objectToString(fieldValue));
			}else
				resp.append(TypeHelper.objectToString(fieldValue));
		}catch(FMWException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex){
			throw new FMWException(ex);
		}
		
		return resp.toString();
	}
	
	public static boolean showOpeationsSql(){
		if(showOpeationsSql == null){
			showOpeationsSql = StringUtils.defaultIfEmpty(IOCHelper.getBean(FMWEntityConstants.ENTITY_OPERATIONS_SHOWSQL_IOC_KEY, String.class), "false").toLowerCase();
		}
		return showOpeationsSql.equals(Boolean.TRUE.toString());
	}
	
	public static String getEntityLabelsFormat(){
		return ENTITY_LABELS_FMT;
	}
	
	public static String getEntityFormTitleFormat(){
		return ENTITY_FORM_TITLE_FMT;
	}
	
	public static MessageFormat createEntityLabelsMessageFormat(){
		return new MessageFormat(getEntityLabelsFormat());
	}
	
	public static MessageFormat createEntityFormTitleMessageFormat(){
		return new MessageFormat(getEntityFormTitleFormat());
	}
	
	
}	
		
		
		
		
		
		