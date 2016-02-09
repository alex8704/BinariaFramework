package co.com.binariasystems.fmw.entity.cfg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.dto.ComparableListableDTO;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.annot.CRUDViewConfig;
import co.com.binariasystems.fmw.entity.annot.Column;
import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.entity.annot.FieldValue;
import co.com.binariasystems.fmw.entity.annot.ForeignKey;
import co.com.binariasystems.fmw.entity.annot.Ignore;
import co.com.binariasystems.fmw.entity.annot.Key;
import co.com.binariasystems.fmw.entity.annot.Nullable;
import co.com.binariasystems.fmw.entity.annot.Relation;
import co.com.binariasystems.fmw.entity.annot.SearcherConfig;
import co.com.binariasystems.fmw.entity.annot.ViewFieldConfig;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditableEntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.entity.validator.EntityValidator;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;

/*
 * Implementacion por defecto de la interface {@link EntityConfigurator}
 * 
 * @see EntityConfigurator
 * @author Alexander Castro O.
 */

public class DefaultEntityConfigurator<T> implements EntityConfigurator<T>{
	private Class<T> entityClazz;
	private int keyCount = 0;
	private EntityConfigData<T> entityConfigData;
	
	
	protected DefaultEntityConfigurator(){
	}
	
	@SuppressWarnings("unchecked")
	public DefaultEntityConfigurator(String entityClazzName){
		try{
			this.entityClazz = (Class<T>) Class.forName(entityClazzName);
		}catch(ClassNotFoundException ex){
			throw new FMWUncheckedException(ex);
		}
		
	}
	
	public DefaultEntityConfigurator(Class<T> entityClazz){
		this.entityClazz = entityClazz;
	}

	
	public EntityConfigData<T> configure() throws FMWException {
		keyCount = 0;
		if(entityConfigData != null) 
			return entityConfigData;
		entityConfigData = isAuditableEntity() ? 
				new AuditableEntityConfigData<T>(entityClazz.getAnnotation(CRUDViewConfig.class).creationUserField(),
				entityClazz.getAnnotation(CRUDViewConfig.class).creationDateField(),
				entityClazz.getAnnotation(CRUDViewConfig.class).modificationUserField(),
				entityClazz.getAnnotation(CRUDViewConfig.class).modificationDateField()) 
				: new EntityConfigData<T>();
		entityConfigData.setEntityClass(entityClazz);
		entityConfigData.setTable(entityClazz.getSimpleName().toLowerCase());
		for(Annotation annot : entityClazz.getAnnotations()){
			if(annot instanceof Entity){
				entityConfigData.setTable(StringUtils.defaultIfEmpty(((Entity)annot).table(), entityConfigData.getTable()));
				entityConfigData.setEnumKeyProperty(((Entity)annot).enumKeyProperty());
				entityConfigData.setPkGenerationStrategy(((Entity)annot).pkGenerationStrategy());
			}if(annot instanceof SearcherConfig){
				for(String descriptionField : ((SearcherConfig)annot).descriptionFields())
					entityConfigData.getSearchDescriptionFields().add(descriptionField);
				for(String gridField : ((SearcherConfig)annot).gridColumnFields())
					entityConfigData.getGridColumnFields().add(gridField);
				entityConfigData.setSearchFieldName(((SearcherConfig)annot).searchField());
			}
			
			if(annot instanceof CRUDViewConfig){
				for(String descriptionField : ((CRUDViewConfig)annot).searcherConfig().descriptionFields())
					entityConfigData.getSearchDescriptionFields().add(descriptionField);
				for(String gridField : ((CRUDViewConfig)annot).searcherConfig().gridColumnFields())
					entityConfigData.getGridColumnFields().add(gridField);
				entityConfigData.setSearchFieldName(((CRUDViewConfig)annot).searcherConfig().searchField());
				entityConfigData.setTitleKey(((CRUDViewConfig)annot).titleKey());
				if(!EntityValidator.class.equals(((CRUDViewConfig)annot).validationClass()))
					entityConfigData.setValidationClass(((CRUDViewConfig)annot).validationClass());
				entityConfigData.setDeleteEnabled(((CRUDViewConfig)annot).deleteEnabled());
				if(StringUtils.isNotEmpty(((CRUDViewConfig)annot).messagesFilePath()))
					entityConfigData.setMessagesFilePath(((CRUDViewConfig)annot).messagesFilePath());
			}
		}
		
		Class<?> currentClass = entityClazz;
		while(!currentClass.equals(Object.class)){
			processEntityClazzConfig(currentClass, entityConfigData);
			currentClass = currentClass.getSuperclass();
		}
		
		if(StringUtils.isEmpty(entityConfigData.getPkFieldName()))
			throw new FMWException("Cannot found the Primary Key Field, for Entity entity "+entityClazz.getName());
		
		if(keyCount > 1) 
			throw new FMWException("Entity class "+entityClazz.getName()+" must have only and only one(1) field annotated with @"+Key.class.getSimpleName());
		
		if(StringUtils.isEmpty(entityConfigData.getSearchFieldName()))
			entityConfigData.setSearchFieldName(entityConfigData.getPkFieldName());
		
		return entityConfigData;
	}
	
	
	private void processEntityClazzConfig(Class<?> clazz, EntityConfigData<T> entityConfigData) throws FMWException{
		Field[] declaredFields = clazz.getDeclaredFields();
		for(Field field : declaredFields){
			FieldConfigData fieldCfg = null;
			if(field.isAnnotationPresent(Ignore.class) || TypeHelper.isCollectionType(field.getType()) || 
					Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) ||
					(!field.getType().isEnum() && !TypeHelper.isBasicType(field.getType()) && !field.isAnnotationPresent(ForeignKey.class)
							&& !field.isAnnotationPresent(Relation.class)))
				continue;
			if(field.isAnnotationPresent(Relation.class) || field.isAnnotationPresent(ForeignKey.class))
				fieldCfg = new RelationFieldConfigData();
			else
				fieldCfg = new FieldConfigData();
			
			fieldCfg.setAuditoryField((entityConfigData instanceof AuditableEntityConfigData) && (isAuditoryField(field.getName())));
			fieldCfg.setMandatory(!field.isAnnotationPresent(Nullable.class));
			for(Annotation annot : field.getAnnotations()){
				//Manejo informacion para campos que representan claves foraneas (hacen referencias a otras entidades)
				if(annot instanceof ForeignKey){
					if(TypeHelper.isBasicType(field.getType())){
						if(((ForeignKey)annot).entityClazz() == null)//Un campo anotado como ForeignKey de tipo Basico Java debe especificar un entityClazz
							throw new FMWException("The basic standard type field "+clazz.getName()+"."+field.getName()+" must declare a entityClazz");
						((RelationFieldConfigData)fieldCfg).setRelationEntityClass(((ForeignKey)annot).entityClazz());
					}else{
						Class<?> relationClazz = field.getType();
						((RelationFieldConfigData)fieldCfg).setRelationEntityClass(relationClazz);
					}
				}
				
				//Manejo informacion para campos que representan claves foraneas (hacen referencias a otras entidades)
				if(annot instanceof Relation){
					//La anotacion @Relation solo se puede usar cuando el campo es del tipo de la entidad foranea
					if(TypeHelper.isBasicType(field.getType()))
						throw new FMWException("Cannot determine entityClass of @"+Relation.class.getSimpleName()+" field "+clazz.getName()+"."+field.getName());
					Class<?> relationClazz = field.getType();
					((RelationFieldConfigData)fieldCfg).setRelationEntityClass(relationClazz);
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Relation)annot).column(), fieldCfg.getColumnName()));
				}
				
				//Representa clave primaria de una entidad
				if(annot instanceof Key){
					entityConfigData.setPkFieldName(field.getName());
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Key)annot).column(), field.getName()));
					keyCount += 1;
				}
				
				//Se valida si el campo tiene la anotacion @Column para los casos en que la columna
				//mapeada se llame diferente al campo de la clase Java
				if(annot instanceof Column){
					fieldCfg.setColumnName(StringUtils.defaultIfEmpty(((Column)annot).name(), field.getName()));
				}
				
				if(annot instanceof ViewFieldConfig){
					fieldCfg.setOmmitUpperTransform(((ViewFieldConfig)annot).ommitUpperTransform());
					fieldCfg.setUiLabel(((ViewFieldConfig)annot).uiLabel());
					fieldCfg.setFieldUIControl(((ViewFieldConfig)annot).uiControl());
					if(((ViewFieldConfig)annot).fixedValues().length > 0 && !Listable.class.isAssignableFrom(field.getType()))
						throw new FMWException("Only "+Listable.class.getName()+" fields allow @"+FieldValue.class.getSimpleName()+" annotation");
					fieldCfg.setFixedValues(new ComparableListableDTO[((ViewFieldConfig)annot).fixedValues().length]);
					for(int i = 0; i < ((ViewFieldConfig)annot).fixedValues().length; i++){
						FieldValue val = ((ViewFieldConfig)annot).fixedValues()[i];
						ComparableListableDTO fixedVal = new ComparableListableDTO(val.pk(), val.description());
						fieldCfg.getFixedValues()[i] = fixedVal;
					}
				}
				
				
			}
			
			//Configura automaticamente campos tipo enumeracion
			if(field.getType().isEnum()){
				fieldCfg.setEnumType(true);
			}
			//Me aseguro que si el campo no esta anotado, al menos configure valores por defecto
			ensureFieldConfig(field, fieldCfg, entityConfigData);
			
			if(field.getName().equals(entityConfigData.getPkFieldName()) && fieldCfg instanceof RelationFieldConfigData)//No se puede tener una PK y anotarla como ForeignKey
				throw new FMWException("Field "+clazz.getName()+"."+field.getName()+" has annotated at same time with @"+Key.class.getSimpleName()+" and @"+ForeignKey.class.getSimpleName()+" and these are mutualy exclusive annotations");
			
			entityConfigData.addFieldData(fieldCfg.getFieldName(), fieldCfg);
		}
	}
	
	
	private void ensureFieldConfig(Field field, FieldConfigData fieldCfg, EntityConfigData<T> entityConfigData) throws FMWException{
		fieldCfg.setFieldName(field.getName());
		fieldCfg.setFieldType(field.getType());
		fieldCfg.setColumnName(StringUtils.defaultIfEmpty(fieldCfg.getColumnName(), field.getName()));
		
		if(fieldCfg instanceof RelationFieldConfigData)
			((RelationFieldConfigData)fieldCfg).setQueryAlias(generateRelationFieldQueryAlias(fieldCfg));
		
		if(fieldCfg.isAuditoryField())return;
		
		if(fieldCfg.getFieldUIControl() != null && fieldCfg.getFieldUIControl() != EntityConfigUIControl.DEFAULT){
			if(!FMWEntityUtils.isValidControlForField(fieldCfg))
				throw new FMWException("Cannot create UIControl of type "+fieldCfg.getFieldUIControl().name()+" for "+fieldCfg.getFieldType()+" field "+fieldCfg.getFieldName());
		}else{
			if(fieldCfg instanceof RelationFieldConfigData){// Buscador Por defecto
				fieldCfg.setFieldUIControl(EntityConfigUIControl.SEARCHBOX);
			}
			else if(TypeHelper.isNumericType(fieldCfg.getFieldType()) || CharSequence.class.isAssignableFrom(fieldCfg.getFieldType()) ||
					Character.class.isAssignableFrom(fieldCfg.getFieldType())){//TextField por defecto
				fieldCfg.setFieldUIControl(EntityConfigUIControl.TEXTFIELD);
			}
			else if(Date.class.isAssignableFrom(fieldCfg.getFieldType())){//DateField por defecto
				fieldCfg.setFieldUIControl(EntityConfigUIControl.DATEFIELD);
			}
			else if(fieldCfg.isEnumType() || (fieldCfg.getFixedValues() != null && fieldCfg.getFixedValues().length > 0)){//ComboBox por defecto
				fieldCfg.setFieldUIControl(EntityConfigUIControl.COMBOBOX);
			}
			else if(Boolean.class.isAssignableFrom(fieldCfg.getFieldType())){//CheckBox siempre
				fieldCfg.setFieldUIControl(EntityConfigUIControl.CHECKBOX);
			}
		}
	}
	
	public Class<T> getEntityClazz() {
		return entityClazz;
	}
	
	public void setEntityClass(Class<T> entityClazz){
		this.entityClazz = entityClazz;
		entityConfigData = null;
	}

	
	public Class<T> getEntityClass() {
		return entityClazz;
	}
	
	private boolean isAuditableEntity(){
		return entityClazz.isAnnotationPresent(CRUDViewConfig.class) && entityClazz.getAnnotation(CRUDViewConfig.class).isAuditable();
	}
	
	private boolean isAuditoryField(String fieldName){
		if(!(entityConfigData instanceof AuditableEntityConfigData))
			return false;
		CRUDViewConfig config = entityClazz.getAnnotation(CRUDViewConfig.class);
		return fieldName.equals(config.creationUserField()) ||
				fieldName.equals(config.creationDateField()) ||
				fieldName.equals(config.modificationUserField()) ||
				fieldName.equals(config.modificationDateField());
	}
	
	private String generateRelationFieldQueryAlias(FieldConfigData fieldCfg){
		return "as_"+fieldCfg.getFieldName() + "_" + entityConfigData.getFieldNames().size();
	}
	

}
