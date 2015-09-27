package co.com.binariasystems.fmw.entity.cfg;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.dto.BasicListableDTO;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.Auditable;
import co.com.binariasystems.fmw.entity.Column;
import co.com.binariasystems.fmw.entity.Entity;
import co.com.binariasystems.fmw.entity.FieldValue;
import co.com.binariasystems.fmw.entity.FieldValues;
import co.com.binariasystems.fmw.entity.ForeignKey;
import co.com.binariasystems.fmw.entity.Ignore;
import co.com.binariasystems.fmw.entity.Key;
import co.com.binariasystems.fmw.entity.Nullable;
import co.com.binariasystems.fmw.entity.Relation;
import co.com.binariasystems.fmw.entity.SearchField;
import co.com.binariasystems.fmw.entity.SearchTarget;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditableEntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.validator.EntityValidator;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.reflec.TypeHelper;

/*
 * Implementacion por defecto de la interface {@link EntityConfigurator}
 * 
 * @see EntityConfigurator
 * @author Alexander Castro O.
 */

public class DefaultEntityConfigurator implements EntityConfigurator{
	private Class<? extends Serializable> entityClazz;
	private int keyCount = 0;
	private int searchKeyCount = 0;
	private EnumKeyProperty enumKeyProperty = EnumKeyProperty.NAME;
	private Map<String, String> fieldLabelMappings = new HashMap<String, String>();
	private Map<String, EntityConfigUIControl> fieldUIControlMappings = new HashMap<String, EntityConfigUIControl>();
	private boolean deleteEnabled = true;
	private PKGenerationStrategy pKGenerationStrategy = PKGenerationStrategy.MAX_QUERY;
	private String titleKey;
	private EntityConfigData entityConfigData;
	
	
	protected DefaultEntityConfigurator(){
	}
	
	public DefaultEntityConfigurator(String entityClazzName){
		try{
			this.entityClazz = (Class<? extends Serializable>) Class.forName(entityClazzName);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		
	}
	
	public DefaultEntityConfigurator(Class<? extends Serializable> entityClazz){
		this.entityClazz = entityClazz;
	}

	
	public EntityConfigData configure() throws Exception{
		keyCount = 0;
		searchKeyCount = 0;
		if(entityConfigData != null) 
			return entityConfigData;
		
		entityConfigData = entityClazz.isAnnotationPresent(Auditable.class) ? new AuditableEntityConfigData() : new EntityConfigData();
		entityConfigData.setEntityClass(entityClazz);
		entityConfigData.setTable(entityClazz.getSimpleName().toLowerCase());
		for(Annotation annot : entityClazz.getAnnotations()){
			if(annot instanceof Entity){
				entityConfigData.setTable(StringUtils.defaultIfEmpty(((Entity)annot).table(), entityConfigData.getTable()));
				if(!((Entity)annot).validationClass().equals(EntityValidator.class))
					entityConfigData.setValidationClass(((Entity)annot).validationClass());
			}if(annot instanceof SearchTarget){
				if(((SearchTarget)annot).descriptionFields() != null){
					for(int i = 0; i < ((SearchTarget)annot).descriptionFields().length; i++)
						entityConfigData.getSearchDescriptionFields().add(((SearchTarget)annot).descriptionFields()[i]);
				}
			}
		}
		
		Class currentClass = entityClazz;
		while(!currentClass.equals(Object.class)){
			processEntityClazzConfig(currentClass, entityConfigData);
			currentClass = currentClass.getSuperclass();
		}
		
		if(StringUtils.isEmpty(entityConfigData.getPkFieldName()))
			throw new Exception("Cannot found the Primary Key Field, for Entity entity "+entityClazz.getName());
		
		if(keyCount > 1) 
			throw new Exception("Entity class "+entityClazz.getName()+" must have only and only one(1) field annotated with @"+Key.class.getSimpleName());
		if(searchKeyCount > 1) 
			throw new Exception("Entity class "+entityClazz.getName()+" must have only and only one(1) field annotated with @"+SearchField.class.getSimpleName());
		
		if(StringUtils.isEmpty(entityConfigData.getSearchFieldName()))
			entityConfigData.setSearchFieldName(entityConfigData.getPkFieldName());
		return entityConfigData;
	}
	
	
	private void processEntityClazzConfig(Class clazz, EntityConfigData entityConfigData) throws Exception{
		Field[] declaredFields = clazz.getDeclaredFields();
		for(Field field : declaredFields){
			FieldConfigData fieldCfg = null;
			if(field.isAnnotationPresent(Ignore.class) || TypeHelper.isCollectionType(field.getType()) || Modifier.isStatic(field.getModifiers()) || 
					(!field.getType().isEnum() && !TypeHelper.isBasicType(field.getType()) && !field.isAnnotationPresent(ForeignKey.class)
							&& !field.isAnnotationPresent(Relation.class) && !field.isAnnotationPresent(FieldValues.class)))
				continue;
			if(entityConfigData instanceof AuditableEntityConfigData && AuditableEntityConfigData.isAuditField(field.getName(), (Auditable)clazz.getAnnotation(Auditable.class)))
				fieldCfg = new AuditFieldConfigData();
			else if(field.isAnnotationPresent(Relation.class) || field.isAnnotationPresent(ForeignKey.class))
				fieldCfg = new RelationFieldConfigData();
			else
				fieldCfg = new FieldConfigData();
			
			for(Annotation annot : field.getAnnotations()){
				//Manejo informacion para campos que representan claves foraneas (hacen referencias a otras entidades)
				if(annot instanceof ForeignKey){
					if(TypeHelper.isBasicType(field.getType())){
						if(((ForeignKey)annot).entityClazz() == null)//Un campo anotado como ForeignKey de tipo Basico Java debe especificar un entityClazz
							throw new Exception("The basic standard type field "+clazz.getName()+"."+field.getName()+" must declare a entityClazz");
						((RelationFieldConfigData)fieldCfg).setRelationEntityClass(((ForeignKey)annot).entityClazz());
					}else{
						Class relationClazz = field.getType();
						((RelationFieldConfigData)fieldCfg).setRelationEntityClass(relationClazz);
					}
				}
				
				//Manejo informacion para campos que representan claves foraneas (hacen referencias a otras entidades)
				if(annot instanceof Relation){
					//La anotacion @Relation solo se puede usar cuando el campo es del tipo de la entidad foranea
					if(TypeHelper.isBasicType(field.getType()))
						throw new Exception("Cannot determine entityClass of @"+Relation.class.getSimpleName()+" field "+clazz.getName()+"."+field.getName());
					Class relationClazz = field.getType();
					((RelationFieldConfigData)fieldCfg).setRelationEntityClass(relationClazz);
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Relation)annot).column(), fieldCfg.getColumnName()));
				}
				
				//Representa clave primaria de una entidad
				if(annot instanceof Key){
					entityConfigData.setPkFieldName(field.getName());
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Key)annot).column(), field.getName()));
					keyCount += 1;
				}
				
				//Campos con conjunto de valores predefinidos, para campos de seleccion
				if(annot instanceof FieldValues){
					if(!Listable.class.isAssignableFrom(field.getType()))
						throw new FMWException("Only "+Listable.class.getName()+" fields allow @"+FieldValues.class.getSimpleName()+" annotation");
					fieldCfg.setFixedValues(new BasicListableDTO[((FieldValues)annot).value().length]);
					for(int i = 0; i < ((FieldValues)annot).value().length; i++){
						FieldValue val = ((FieldValues)annot).value()[i];
						BasicListableDTO fixedVal = new BasicListableDTO();
						fixedVal.setPK(val.pk());
						fixedVal.setDescription(val.description());
						fieldCfg.getFixedValues()[i] = fixedVal;
					}
				}
				
				if(annot instanceof SearchField){
					entityConfigData.setSearchFieldName(field.getName());
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((SearchField)annot).column(), fieldCfg.getColumnName()));
					searchKeyCount += 1;
				}
				
				fieldCfg.setMandatory(!(annot instanceof Nullable));
				
				//Se valida si el campo tiene la anotacion @Column para los casos en que la columna
				//mapeada se llame diferente al campo de la clase Java
				if(annot instanceof Column){
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Column)annot).name(), field.getName()));
				}
			}
			
			//Configura automaticamente campos tipo enumeracion
			if(field.getType().isEnum()){
				fieldCfg.setEnumType(true);
			}
			//Me aseguro que si el campo no esta anotado, al menos configure valores por defecto
			ensureFieldConfig(field, fieldCfg, entityConfigData);
			
			if(field.getName().equals(entityConfigData.getPkFieldName()) && fieldCfg instanceof RelationFieldConfigData)//No se puede tener una PK y anotarla como ForeignKey
				throw new Exception("Field "+clazz.getName()+"."+field.getName()+" has annotated at same time with @"+Key.class.getSimpleName()+" and @"+ForeignKey.class.getSimpleName()+" and these are mutualy exclusive annotations");
			
			entityConfigData.getFieldsData().put(fieldCfg.getFieldName(), fieldCfg);
		}
	}
	
	
	private void ensureFieldConfig(Field field, FieldConfigData fieldCfg, EntityConfigData entityConfigData) throws Exception{
		fieldCfg.setFieldName(field.getName());
		fieldCfg.setFieldType(field.getType());
//		fieldCfg.setSetterMethodName(TypeHelper.extractSetterMethodName(field, clazz));
//		fieldCfg.setGetterMethodName(TypeHelper.extractGetterMethodName(field, clazz));
//		if(StringUtils.isEmpty(fieldCfg.getGetterMethodName()) || StringUtils.isEmpty(fieldCfg.getSetterMethodName()))
//			throw new Exception("Setter or Getter method not found for field "+clazz.getName()+"."+field.getName());
		
		fieldCfg.setColumnName(StringUtils.defaultIfEmpty(fieldCfg.getColumnName(), field.getName()));
		
		if(fieldCfg instanceof RelationFieldConfigData)
			((RelationFieldConfigData)fieldCfg).setQueryAlias(generateRelationFieldQueryAlias(fieldCfg));
		
		if(fieldCfg instanceof AuditFieldConfigData)
			return;
		
		EntityConfigUIControl fieldUIControl = getFieldUIControlMappings().get(fieldCfg.getFieldName());
		if(fieldUIControl != null){
			if(!isValidControlForField(fieldUIControl, fieldCfg))
				throw new Exception("Cannot create UIControl of type "+fieldUIControl.name()+" for "+fieldCfg.getFieldType()+" field "+fieldCfg.getFieldName());
		}else{
			if(fieldCfg instanceof RelationFieldConfigData){// Buscador Por defecto
				fieldUIControl = EntityConfigUIControl.SEARCHBOX;
			}
			else if(TypeHelper.isNumericType(fieldCfg.getFieldType()) || CharSequence.class.isAssignableFrom(fieldCfg.getFieldType()) ||
					Character.class.isAssignableFrom(fieldCfg.getFieldType())){//TextField por defecto
				fieldUIControl = EntityConfigUIControl.TEXTFIELD;
			}
			else if(Date.class.isAssignableFrom(fieldCfg.getFieldType())){//DateField por defecto
				fieldUIControl = EntityConfigUIControl.DATEFIELD;
			}
			else if(fieldCfg.isEnumType() || (fieldCfg.getFixedValues() != null && fieldCfg.getFixedValues().length > 0)){//ComboBox por defecto
				fieldUIControl = EntityConfigUIControl.COMBOBOX;
			}
			else if(Boolean.class.isAssignableFrom(fieldCfg.getFieldType())){//CheckBox siempre
				fieldUIControl = EntityConfigUIControl.CHECKBOX;
			}
		}
		fieldCfg.setFieldUIControl(fieldUIControl);
	}
	private boolean isValidControlForField(EntityConfigUIControl controlType, FieldConfigData fieldInfo){
		boolean resp = false;
		if(controlType == EntityConfigUIControl.TEXTFIELD || controlType == EntityConfigUIControl.PASSWORDFIELD){
			resp = TypeHelper.isNumericType(fieldInfo.getFieldType()) || CharSequence.class.isAssignableFrom(fieldInfo.getFieldType()) ||
					Character.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(controlType == EntityConfigUIControl.TEXTAREA){
			resp = CharSequence.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(controlType == EntityConfigUIControl.DATEFIELD){
			resp = Date.class.isAssignableFrom(fieldInfo.getFieldType());
		}
		if(controlType == EntityConfigUIControl.RADIO){
			resp = fieldInfo.isEnumType() || (fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0);
		}
		if(controlType == EntityConfigUIControl.COMBOBOX){
			resp = fieldInfo.isEnumType() || fieldInfo instanceof RelationFieldConfigData ||
					(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0);
		}
		if(controlType == EntityConfigUIControl.SEARCHBOX){
			resp = fieldInfo instanceof RelationFieldConfigData;
		}
		if(controlType == EntityConfigUIControl.CHECKBOX){
			resp = Boolean.class.isAssignableFrom(fieldInfo.getFieldType());
		}
	
		return resp;
	}

	
	public EnumKeyProperty getEnumKeyProperty() {
		return enumKeyProperty;
	}

	
	public Map<String, String> getFieldLabelMappings() {
		return fieldLabelMappings;
	}

	
	public Map<String, EntityConfigUIControl> getFieldUIControlMappings() {
		return fieldUIControlMappings;
	}
	
	
	public PKGenerationStrategy getPKGenerationStrategy() {
		return pKGenerationStrategy;
	}
	
	
	public boolean isDeleteEnabled() {
		return deleteEnabled;
	}
	
	public Class<? extends Serializable> getEntityClazz() {
		return entityClazz;
	}

	public void setEnumKeyProperty(EnumKeyProperty enumKeyProperty) {
		this.enumKeyProperty = enumKeyProperty;
	}

	public void setFieldLabelMappings(Map<String, String> fieldLabelMappings) {
		this.fieldLabelMappings = fieldLabelMappings;
	}

	public void setFieldUIControlMappings(
			Map<String, EntityConfigUIControl> fieldUIControlMappings) {
		this.fieldUIControlMappings = fieldUIControlMappings;
	}

	public void setDeleteEnabled(boolean deleteEnabled) {
		this.deleteEnabled = deleteEnabled;
	}
	
	public void setPKGenerationStrategy(PKGenerationStrategy pKGenerationStrategy) {
		this.pKGenerationStrategy = pKGenerationStrategy;
	}
	
	
	public String getTitleKey() {
		return titleKey;
	}
	
	public void setTitleKey(String titleKey){
		this.titleKey = titleKey;
	}
	
	public void setEntityClass(Class<? extends Serializable> entityClazz){
		this.entityClazz = entityClazz;
		entityConfigData = null;
	}

	
	public Class<? extends Serializable> getEntityClass() {
		return entityClazz;
	}
	
	private String generateRelationFieldQueryAlias(FieldConfigData fieldCfg){
		return "as_"+fieldCfg.getFieldName() + "_" + entityConfigData.getFieldsData().size();
	}
	

}