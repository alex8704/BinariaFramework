package co.com.binariasystems.fmw.vweb.util;

import java.lang.reflect.InvocationTargetException;
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
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherField;

import com.vaadin.data.Item;
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

public class EntityConfigUtils {
	public static final int TEXTFIELD_MAX_LENGTH = 100;
	public static final int TEXTAREA_MAX_LENGTH = 4000;
	public static final float BUTTONS_WIDTH = 100.0f;
	public static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	
	
	public static String getFieldCaptionText(FieldConfigData fieldInfo, EntityConfigData entityConfig, MessageFormat labelsFmt, MessageBundleManager messages){
		String captionKey = entityConfig.getFieldLabelMappings().get(fieldInfo.getFieldName());
		captionKey = StringUtils.defaultString(captionKey, labelsFmt.format(new String[]{entityConfig.getEntityClass().getSimpleName(), fieldInfo.getFieldName()}));
		return LocaleMessagesUtil.getLocalizedMessage(messages, captionKey);
	}
	
	public static Component createComponentForField(FieldConfigData fieldInfo, EntityConfigData entityConfig, MessageFormat labelsFmt, MessageBundleManager messages) throws FMWException{
		Component resp = null;
		EntityConfigUIControl controlType = fieldInfo.getFieldUIControl();
		String caption = getFieldCaptionText(fieldInfo, entityConfig, labelsFmt, messages);
		try{
			if(controlType == EntityConfigUIControl.TEXTFIELD){
				TextField widget = new TextField(caption);
				widget.setImmediate(true);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
				widget.setNullRepresentation("");
				if(TypeHelper.isNumericType(fieldInfo.getFieldType()))
					widget.setConverter(fieldInfo.getFieldType());
				
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.PASSWORDFIELD){
				PasswordField widget = new PasswordField(caption);
				widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
	
				widget.setValidationVisible(true);
				widget.setNullRepresentation("");
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.TEXTAREA){
				TextArea widget = new TextArea(caption);
				widget.setMaxLength(TEXTAREA_MAX_LENGTH);
				widget.setValidationVisible(true);
				widget.setNullRepresentation("");
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.DATEFIELD){
				DateField widget = new DateField(caption);
				widget.setDateFormat(FMWConstants.DATE_DEFAULT_FORMAT);
				widget.setValidationVisible(true);
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.COMBOBOX){
				ComboBox widget = new ComboBox(caption);
				widget.setValidationVisible(true);
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.RADIO){
				OptionGroup widget = new OptionGroup(caption);
				widget.setValidationVisible(true);
				resp = widget;
				
			}
			else if(controlType == EntityConfigUIControl.CHECKBOX){
				CheckBox widget = new CheckBox(caption);
				resp = widget;
			}
			else if(controlType == EntityConfigUIControl.SEARCHBOX){
				SearcherField widget = new SearcherField(((RelationFieldConfigData)fieldInfo).getRelationEntityClass(), caption);
				widget.setRequired(fieldInfo.isMandatory());
				resp = widget;
			}
			
			if(resp instanceof AbstractSelect){
				if(fieldInfo.isEnumType()){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					Enum[] vals = (Enum[]) fieldInfo.getFieldType().getEnumConstants();
					
					for(int i = 0; i < vals.length; i++){
						Item item = ((AbstractSelect)resp).addItem(vals[i]);
						((AbstractSelect)resp).setItemCaption(item, TypeHelper.objectToString(vals[i]));
					}
				}else if(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0){
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Listable value : fieldInfo.getFixedValues()){
						Item item = ((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(item, value.getDescription());
					}
				}else{
					EntityCRUDOperationsManager auxManager = EntityCRUDOperationsManager.getInstance(((RelationFieldConfigData)fieldInfo).getRelationEntityClass());
					List<Object> fieldValues = auxManager.searchWithoutPaging(null);
					BeanItemContainer<?> itemContainer = new BeanItemContainer(fieldInfo.getFieldType(), fieldValues);
					((AbstractSelect)resp).setContainerDataSource(itemContainer);
					for(Object value : fieldValues){
						Item item = ((AbstractSelect)resp).addItem(value);
						((AbstractSelect)resp).setItemCaption(item, FMWEntityUtils.generateStringRepresentationForField(value, FMWConstants.PIPE));
					}
				}
			}
		}catch(NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | InstantiationException ex){
			throw new FMWException(ex.getMessage(), ex);
		}
		return resp;
	}
}