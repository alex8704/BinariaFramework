package co.com.binariasystems.fmw.entity.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.Auditable;
import co.com.binariasystems.fmw.entity.validator.EntityValidator;

/**
 * @author Alexander Castro O.
 */

public class EntityConfigData<T> implements Serializable {
	private Class<T> entityClass;
	private Class<? extends EntityValidator> validationClass;
	private String table;
	private Map<String, FieldConfigData> fieldsData = new HashMap<String, FieldConfigData>();
	private String pkFieldName;
	private String searchFieldName;
	private List<String> searchDescriptionFields;
	private List<String> gridColumnFields;
	private String titleKey;
	private EnumKeyProperty enumKeyProperty;
	private Map<String, String> fieldLabelMappings;
	private boolean deleteEnabled;
	private PKGenerationStrategy pkGenerationStrategy;

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Map<String, FieldConfigData> getFieldsData() {
		return fieldsData;
	}

	public FieldConfigData getFieldData(String fieldName) {
		return fieldsData.get(fieldName);
	}

	public void setFieldsData(Map<String, FieldConfigData> fieldsData) {
		this.fieldsData = fieldsData;
	}

	public FieldConfigData getSearchFieldData() {
		return fieldsData.get(getSearchFieldName());
	}

	public FieldConfigData getPkFieldData() {
		return fieldsData.get(getPkFieldName());
	}

	public String getSearchFieldName() {
		return searchFieldName;
	}

	public void setSearchFieldName(String searchFieldName) {
		this.searchFieldName = searchFieldName;
	}

	public Class<? extends EntityValidator> getValidationClass() {
		return validationClass;
	}

	public void setValidationClass(
			Class<? extends EntityValidator> validationClass) {
		this.validationClass = validationClass;
	}

	public List<String> getSearchDescriptionFields() {
		if (searchDescriptionFields == null)
			searchDescriptionFields = new LinkedList<String>();
		return searchDescriptionFields;
	}
	
	public List<String> getGridColumnFields() {
		if (gridColumnFields == null)
			gridColumnFields = new LinkedList<String>();
		return gridColumnFields;
	}

	public String getPkFieldName() {
		return pkFieldName;
	}

	public void setPkFieldName(String pkFieldName) {
		this.pkFieldName = pkFieldName;
	}

	public String getTitleKey() {
		return titleKey;
	}

	public EnumKeyProperty getEnumKeyProperty() {
		return enumKeyProperty;
	}

	public Map<String, String> getFieldLabelMappings() {
		return fieldLabelMappings;
	}

	public boolean isDeleteEnabled() {
		return deleteEnabled;
	}

	public PKGenerationStrategy getPkGenerationStrategy() {
		return pkGenerationStrategy;
	}

	public void setPkGenerationStrategy(PKGenerationStrategy pkGenerationStrategy) {
		this.pkGenerationStrategy = pkGenerationStrategy;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public void setEnumKeyProperty(EnumKeyProperty enumKeyProperty) {
		this.enumKeyProperty = enumKeyProperty;
	}

	public void setFieldLabelMappings(Map<String, String> fieldLabelMappings) {
		this.fieldLabelMappings = fieldLabelMappings;
	}

	public void setDeleteEnabled(boolean deleteEnabled) {
		this.deleteEnabled = deleteEnabled;
	}



	public static class AuditableEntityConfigData<T> extends EntityConfigData<T> {
		private Auditable auditableInfo;
		
		public AuditableEntityConfigData(Auditable auditableInfo){
			this.auditableInfo = auditableInfo;
		}

		public static boolean isAuditField(String fieldName,Auditable auditableInfo) {
			if (fieldName == null || auditableInfo == null)
				return false;
			if (auditableInfo.creationUserField() != null
					&& auditableInfo.creationUserField().equals(fieldName))
				return true;
			if (auditableInfo.modificationUserField() != null
					&& auditableInfo.modificationUserField().equals(fieldName))
				return true;
			if (auditableInfo.creationDateField() != null
					&& auditableInfo.creationDateField().equals(fieldName))
				return true;
			if (auditableInfo.modificationDateField() != null
					&& auditableInfo.modificationDateField().equals(fieldName))
				return true;
			return false;
		}

		public FieldConfigData getCreationUserFieldCfg() {
			return getFieldData(auditableInfo.creationUserField());
		}

		public FieldConfigData getModificationUserFieldCfg() {
			return getFieldData(auditableInfo.modificationUserField());
		}

		public FieldConfigData getCreationDateFieldCfg() {
			return getFieldData(auditableInfo.creationDateField());
		}

		public FieldConfigData getModificationDateFieldCfg() {
			return getFieldData(auditableInfo.modificationDateField());
		}

	}

	public static class FieldConfigData implements Serializable {
		private Class<?> fieldType;
		private String fieldName;
		private String columnName;
		private String setterMethodName;
		private String getterMethodName;
		private boolean mandatory = true;
		private boolean enumType;
		private boolean auditoryField;
		private Listable[] fixedValues;
		private EntityConfigUIControl fieldUIControl;

		public Class<?> getFieldType() {
			return fieldType;
		}

		public void setFieldType(Class<?> fieldType) {
			this.fieldType = fieldType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getSetterMethodName() {
			return setterMethodName;
		}

		public void setSetterMethodName(String setterMethodName) {
			this.setterMethodName = setterMethodName;
		}

		public String getGetterMethodName() {
			return getterMethodName;
		}

		public void setGetterMethodName(String getterMethodName) {
			this.getterMethodName = getterMethodName;
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public void setMandatory(boolean mandatory) {
			this.mandatory = mandatory;
		}

		public boolean isEnumType() {
			return enumType;
		}

		public void setEnumType(boolean enumType) {
			this.enumType = enumType;
		}

		public Listable[] getFixedValues() {
			return fixedValues;
		}

		public void setFixedValues(Listable[] fixedValues) {
			this.fixedValues = fixedValues;
		}

		public EntityConfigUIControl getFieldUIControl() {
			return fieldUIControl;
		}

		public void setFieldUIControl(EntityConfigUIControl fieldUIControl) {
			this.fieldUIControl = fieldUIControl;
		}

		public boolean isRelationField() {
			return this instanceof RelationFieldConfigData;
		}

		public boolean isAuditoryField() {
			return auditoryField;
		}

		public void setAuditoryField(boolean auditoryField) {
			this.auditoryField = auditoryField;
		}

	}

	public static class RelationFieldConfigData extends FieldConfigData {
		private Class<? extends Object> relationEntityClass;
		private String queryAlias;

		public Class<? extends Object> getRelationEntityClass() {
			return relationEntityClass;
		}

		public void setRelationEntityClass(
				Class<? extends Object> relationEntityClass) {
			this.relationEntityClass = relationEntityClass;
		}

		public String getQueryAlias() {
			return queryAlias;
		}

		public void setQueryAlias(String queryAlias) {
			this.queryAlias = queryAlias;
		}

	}

	public static class FieldFixedValue implements Listable {
		private String pk;
		private String description;

		public String getPK() {
			return pk;
		}

		public void setPK(String pk) {
			this.pk = pk;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}
}
