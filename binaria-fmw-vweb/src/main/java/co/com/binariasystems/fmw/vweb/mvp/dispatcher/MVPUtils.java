package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validatable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DateRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DoubleRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.EmailValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.IntRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.NullValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.RegExpValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.StringLengthValidator;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherField;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

public class MVPUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(MVPUtils.class);
	
	public static MessageBundleManager createMessageBundleManager(ViewInfo viewInfo){
		MessageBundleManager resp = null;
		if(!StringUtils.isEmpty(viewInfo.getMessages())){
			resp = MessageBundleManager.forPath(viewInfo.getMessages(), viewInfo.getViewClass());
		}
		return resp;
	}
	
	
	public static void copyHomonymFieldsFromViewToController(ViewInfo viewInfo, Object sourceView, Object targetController) throws ViewInstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
		.filterInputsBy(new FilterBuilder().includePackage(viewInfo.getControllerInfo().getControllerClass().getName()))
      .setUrls(ClasspathHelper.forPackage(viewInfo.getControllerInfo().getControllerClass().getPackage().getName()))
      .setScanners(new SubTypesScanner(), new FieldAnnotationsScanner()));
		
		Set<Field> annotated = reflections.getFieldsAnnotatedWith(ViewField.class);
		Field sourceField = null;
		String sourceName = null;
		Object sourceValue = null;
		boolean forceAccess = true;
		for(Field field : annotated){
			sourceName = StringUtils.defaultIfEmpty(field.getAnnotation(ViewField.class).value(), field.getName());
			sourceField = FieldUtils.getField(viewInfo.getViewClass(), sourceName, forceAccess);
			if(sourceField == null) throw new ViewInstantiationException("Cannot find the @"+ViewField.class.getSimpleName()+" source value for "
			+viewInfo.getControllerInfo().getControllerClass().getName()+"."+field.getName()+" on view class "+viewInfo.getViewClass().getName());
			if(!field.getType().isAssignableFrom(sourceField.getType()))
				throw new ViewInstantiationException("Cannot bind the @"+ViewField.class.getSimpleName()+" source value for"+
						viewInfo.getControllerInfo().getControllerClass().getName()+"."+field.getName()+", because are incopatible types");
			sourceValue = FieldUtils.readField(sourceField, sourceView, forceAccess);
			if(sourceValue != null)
				FieldUtils.writeField(field, targetController, sourceValue, forceAccess);
		}
	}
	
	public static void applyConventionStringsAndValidationsForView(Object viewInstance, ViewInfo viewInfo, MessageBundleManager messageManager) throws ParseException{
		Object fieldValue = null;
		boolean forceAccess = true;
		String localizedCaption = "";
		String localizedDescription = "";
		String localizedTitle = "";
		
		if(viewInfo.isViewStringsByConventions() && messageManager != null && viewInstance instanceof UIForm){
			localizedTitle = LocaleMessagesUtil.conventionTitle(viewInfo.getViewClass(), messageManager);
			((UIForm)viewInstance).setTitle(localizedTitle);
		}
		
		for(Field viewField : viewInfo.getViewClass().getDeclaredFields()){
			if(VWebUtils.isVField(viewField.getType())){
				try {
					fieldValue = FieldUtils.readField(viewField, viewInstance, forceAccess);
				} catch (IllegalAccessException e) {
					LOGGER.error(e.toString(), e);
				}
				if(fieldValue == null) continue;
				localizedCaption = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager, viewField.getName());
				localizedDescription = LocaleMessagesUtil.conventionDescription(viewInfo.getViewClass(), messageManager, viewField.getName());
				if(viewInfo.isViewStringsByConventions() && messageManager != null){
					if(SearcherField.class.isAssignableFrom(viewField.getType())){
						((SearcherField)fieldValue).setCaption(localizedCaption);
						((SearcherField)fieldValue).setDescription(StringUtils.defaultIfEmpty(localizedDescription, localizedCaption));
					}else if(UIForm.class.isAssignableFrom(viewField.getType()) && fieldValue != viewInstance){
						localizedTitle = LocaleMessagesUtil.conventionTitle(viewInfo.getViewClass(), messageManager);
						((UIForm)fieldValue).setTitle(localizedTitle);
					}
					else if(TreeMenu.class.isAssignableFrom(viewField.getType()))
						((TreeMenu)fieldValue).setTitle(localizedCaption);
					else if(Label.class.isAssignableFrom(viewField.getType()))
						((Label)fieldValue).setValue(localizedCaption);
					else{
						((AbstractComponent)fieldValue).setCaption(localizedCaption);
						((AbstractComponent)fieldValue).setDescription(StringUtils.defaultIfEmpty(localizedDescription, localizedCaption));
					}
					
					if(Table.class.isAssignableFrom(viewField.getType()) && ((Table)fieldValue).getColumnHeaderMode().equals(ColumnHeaderMode.EXPLICIT)){
						String tableColumnTitle = null;
						for(Object propertyId : ((Table)fieldValue).getContainerPropertyIds()){
							tableColumnTitle = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager, viewField.getName()+"."+propertyId);
							((Table)fieldValue).setColumnHeader(propertyId, tableColumnTitle);
						}
					}else if(Grid.class.isAssignableFrom(viewField.getType())){
						String tableColumnTitle = null;
						for(Column column : ((Grid)fieldValue).getColumns()){
							tableColumnTitle = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager, viewField.getName()+"."+column.getPropertyId());
							((Grid)fieldValue).getColumn(column.getPropertyId()).setHeaderCaption(tableColumnTitle);
						}
					}
				}
				if(Validatable.class.isAssignableFrom(viewField.getType())){
					Annotation[] fieldAnnotations = viewField.getAnnotations();
					for(Annotation annotation : fieldAnnotations){
						if(annotation instanceof NullValidator)
							((Validatable)fieldValue).addValidator(ValidationUtils.nullValidator(StringUtils.defaultIfEmpty(((NullValidator)annotation).fieldCaption(), localizedCaption)));
						else if(annotation instanceof DateRangeValidator){
							String min = StringUtils.defaultString(((DateRangeValidator)annotation).min());
							String max = StringUtils.defaultString(((DateRangeValidator)annotation).max());
							String format = StringUtils.defaultIfEmpty(((DateRangeValidator)annotation).max(), FMWConstants.TIMESTAMP_SECONDS_FORMAT);
							
							Date minDate = min.equals(DateRangeValidator.CURRENT_DATE) ? new Date() : (min.equals("") ? null : new SimpleDateFormat(format).parse(min));
							Date maxDate = max.equals(DateRangeValidator.CURRENT_DATE) ? new Date() : (max.equals("") ? null : new SimpleDateFormat(format).parse(max));

							((Validatable)fieldValue).addValidator(
									ValidationUtils.dateRangeValidator(
											StringUtils.defaultIfEmpty(((DateRangeValidator)annotation).fieldCaption(), localizedCaption), 
											minDate, 
											maxDate)
							);
						}else if(annotation instanceof DoubleRangeValidator)
							((Validatable)fieldValue).addValidator(
									ValidationUtils.doubleRangeValidator(
											StringUtils.defaultIfEmpty(((DoubleRangeValidator)annotation).fieldCaption(), localizedCaption), 
											((DoubleRangeValidator)annotation).min(), 
											((DoubleRangeValidator)annotation).max())
							);
						else if(annotation instanceof IntRangeValidator)
							((Validatable)fieldValue).addValidator(
									ValidationUtils.integerRangeValidator(
											StringUtils.defaultIfEmpty(((IntRangeValidator)annotation).fieldCaption(), localizedCaption), 
											((IntRangeValidator)annotation).min(), 
											((IntRangeValidator)annotation).max())
							);
						else if(annotation instanceof EmailValidator)
							((Validatable)fieldValue).addValidator(
									ValidationUtils.emailValidator(StringUtils.defaultIfEmpty(((EmailValidator)annotation).fieldCaption(), localizedCaption))
							);
						else if(annotation instanceof StringLengthValidator)
							((Validatable)fieldValue).addValidator(
									ValidationUtils.stringLengthRangeValidator(
											StringUtils.defaultIfEmpty(((StringLengthValidator)annotation).fieldCaption(), localizedCaption), 
											((StringLengthValidator)annotation).min(), 
											((StringLengthValidator)annotation).max())
							);
						else if(annotation instanceof RegExpValidator)
							((Validatable)fieldValue).addValidator(
									ValidationUtils.regexpValidator(
											StringUtils.defaultIfEmpty(((RegExpValidator)annotation).fieldCaption(), localizedCaption), 
											((RegExpValidator)annotation).expression(), 
											((RegExpValidator)annotation).example()
											)
							);
					}
				}
			}
		}
	}
}
