package co.com.binariasystems.fmw.vweb.util;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherField;
import co.com.binariasystems.fmw.vweb.util.converter.DateToTimestampConverter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class EntityConfigUtils {
	public static final int TEXTFIELD_MAX_LENGTH = 100;
	public static final int TEXTAREA_MAX_LENGTH = 4000;
	public static final float BUTTONS_WIDTH = 100.0f;
	public static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	
	
	public static String getFieldCaptionText(FieldConfigData fieldInfo, EntityConfigData<?> entityConfig, MessageFormat labelsFmt, MessageBundleManager messages){
		String captionKey = StringUtils.defaultIfEmpty(fieldInfo.getUiLabel(), labelsFmt.format(new String[]{entityConfig.getEntityClass().getSimpleName(), fieldInfo.getFieldName()}));
		return LocaleMessagesUtil.getLocalizedMessage(messages, captionKey);
	}
	
	public static Component createComponentForField(FieldConfigData fieldInfo, EntityConfigData<?> entityConfig, MessageFormat labelsFmt, MessageBundleManager messages) throws FMWException{
		Component resp = null;
		EntityConfigUIControl controlType = fieldInfo.getFieldUIControl();
		String caption = getFieldCaptionText(fieldInfo, entityConfig, labelsFmt, messages);
		try{
			if(controlType == EntityConfigUIControl.TEXTFIELD){
				TextField widget = new TextField(caption);
				widget.setImmediate(true);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
				widget.setNullRepresentation("");
				widget.setInvalidCommitted(true);
				if(TypeHelper.isNumericType(fieldInfo.getFieldType()))
					widget.setConverter(fieldInfo.getFieldType());
				if(!fieldInfo.isOmmitUpperTransform())
					widget.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.PASSWORDFIELD){
				PasswordField widget = new PasswordField(caption);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
	
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				widget.setNullRepresentation("");
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.TEXTAREA){
				TextArea widget = new TextArea(caption);
				widget.setMaxLength(TEXTAREA_MAX_LENGTH);
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				widget.setNullRepresentation("");
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.DATEFIELD){
				DateField widget = new DateField(caption);
				widget.setDateFormat(FMWConstants.DATE_DEFAULT_FORMAT);
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.COMBOBOX){
				ComboBox widget = new ComboBox(caption);
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.RADIO){
				OptionGroup widget = new OptionGroup(caption);
				widget.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
				widget.setInvalidCommitted(true);
				widget.setValidationVisible(true);
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.CHECKBOX){
				CheckBox widget = new CheckBox(caption);
				widget.setInvalidCommitted(true);
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.SEARCHBOX){
				SearcherField<?> widget = new SearcherField<>(((RelationFieldConfigData)fieldInfo).getRelationEntityClass(), fieldInfo.getFieldType(), caption);
				widget.setRequired(fieldInfo.isMandatory());
				widget.setInvalidCommitted(true);
				resp = widget;
			}
			
			if(resp instanceof AbstractSelect){
				if(fieldInfo.isEnumType()){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					Enum<?>[] vals = (Enum<?>[]) fieldInfo.getFieldType().getEnumConstants();
					for(Enum<?> value : vals){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, TypeHelper.objectToString(value));
					}
				}else if(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Listable value : fieldInfo.getFixedValues()){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, value.getDescription());
					}
				}else{
					EntityCRUDOperationsManager<?> auxManager = EntityCRUDOperationsManager.getInstance(((RelationFieldConfigData)fieldInfo).getRelationEntityClass());
					List<?> fieldValues = auxManager.searchWithoutPaging(null);
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType(), fieldValues);
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Object value : fieldValues){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, FMWEntityUtils.generateStringRepresentationForField(value, FMWConstants.PIPE));
					}
				}
			}
		}catch(ReflectiveOperationException ex){
			throw new FMWException(ex.getMessage(), ex);
		}
		return resp;
	}
	
	public static Component createComponentForCrudField(FieldConfigData fieldInfo, EntityConfigData<?> entityConfig, MessageFormat labelsFmt, MessageBundleManager messages) throws FMWException{
		Component resp = null;
		EntityConfigUIControl controlType = fieldInfo.getFieldUIControl();
		String caption = getFieldCaptionText(fieldInfo, entityConfig, labelsFmt, messages);
		try{

			if(controlType == EntityConfigUIControl.TEXTFIELD){
				TextField widget = new TextField(caption);
				widget.setImmediate(true);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
				
				widget.setRequired(fieldInfo.getFieldName().equals(entityConfig.getPkFieldName()) ? false : fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(!fieldInfo.getFieldName().equals(entityConfig.getPkFieldName()));
				widget.setInvalidCommitted(true);
				widget.setReadOnly(fieldInfo.getFieldName().equals(entityConfig.getPkFieldName()));
				widget.setNullRepresentation("");
				if(TypeHelper.isNumericType(fieldInfo.getFieldType()))
					widget.setConverter(fieldInfo.getFieldType());
				if(!fieldInfo.isOmmitUpperTransform())
					widget.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.PASSWORDFIELD){
				PasswordField widget = new PasswordField(caption);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
				
				widget.setRequired(fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				widget.setNullRepresentation("");
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.TEXTAREA){
				TextArea widget = new TextArea(caption);
				widget.setMaxLength(TEXTAREA_MAX_LENGTH);
				
				widget.setRequired(fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				widget.setNullRepresentation("");
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.DATEFIELD){
				DateField widget = new DateField(caption);
				widget.setDateFormat(FMWConstants.DATE_DEFAULT_FORMAT);
				if(Timestamp.class.isAssignableFrom(fieldInfo.getFieldType()))
					widget.setConverter(new DateToTimestampConverter());
				
				widget.setRequired(fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.COMBOBOX){
				ComboBox widget = new ComboBox(caption);
				
				widget.setRequired(fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.RADIO){
				OptionGroup widget = new OptionGroup(caption);
				
				widget.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
				widget.setRequired(fieldInfo.isMandatory());
				widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
				widget.setValidationVisible(true);
				widget.setInvalidCommitted(true);
				
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.CHECKBOX){
				CheckBox widget = new CheckBox(caption);
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.SEARCHBOX){
				SearcherField<?> widget = new SearcherField<>(((RelationFieldConfigData)fieldInfo).getRelationEntityClass(), fieldInfo.getFieldType(), caption);
				widget.setRequired(fieldInfo.isMandatory());
				resp = widget;
			}
			
			if(resp instanceof AbstractSelect){
				if(fieldInfo.isEnumType()){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					
					Enum<?>[] vals = (Enum<?>[]) fieldInfo.getFieldType().getEnumConstants();
					for(Enum<?> value : vals){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, TypeHelper.objectToString(value));
					}
				}else if(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Listable value : fieldInfo.getFixedValues()){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, value.getDescription());
					}
				}else{
					EntityCRUDOperationsManager<?> auxManager = EntityCRUDOperationsManager.getInstance(((RelationFieldConfigData)fieldInfo).getRelationEntityClass());
					List<?> fieldValues = auxManager.searchWithoutPaging(null);
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType(), fieldValues);
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Object value : fieldValues){
						((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(value, FMWEntityUtils.generateStringRepresentationForField(value, FMWConstants.PIPE));
					}
				}
			}
		}catch(ReflectiveOperationException ex){
			throw new FMWException(ex);
		}
		return resp;
	}
	
	public static MessageBundleManager createMessageManagerEntityConfig(EntityConfigData<?> entityConfig){
		String path = StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME);
		path = entityConfig.getMessagesFilePath() != null ? entityConfig.getMessagesFilePath() : path;
		Class loaderClazz = entityConfig.getMessagesFilePath() != null ? entityConfig.getEntityClass() : IOCHelper.getBean(FMWConstants.DEFAULT_LOADER_CLASS, Class.class);
		return MessageBundleManager.forPath(path, true, loaderClazz);
	}
}
