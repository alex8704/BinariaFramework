package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.com.binariasystems.fmw.annotation.Dependency;
import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.codec.Base64;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.mvp.annotation.NoConventionString;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.AddressValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DateRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DoubleRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.EmailValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.IntRangeValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.NullValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.RegExpValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.StringLengthValidator;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Validatable;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;

public class MVPUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(MVPUtils.class);
	private static final Class<?>[] dependencyAnnotations = { Dependency.class, Autowired.class };

	public static MessageBundleManager createMessageBundleManager(ViewInfo viewInfo) {
		MessageBundleManager resp = null;
		if (!StringUtils.isEmpty(viewInfo.getMessages())) {
			resp = MessageBundleManager.forPath(viewInfo.getMessages(), true, viewInfo.getViewClass());
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	public static void copyHomonymFieldsFromViewToController(ViewInfo viewInfo, Object sourceView, Object targetController) throws ViewInstantiationException, IllegalAccessException {
		Set<Field> annotated = ReflectionUtils.getFields(viewInfo.getControllerInfo().getControllerClass(), ReflectionUtils.withAnnotation(ViewField.class));
		Field sourceField = null;
		String sourceName = null;
		Object sourceValue = null;
		ViewField annotationInfo = null;
		boolean forceAccess = true;
		for (Field field : annotated) {
			annotationInfo = field.getAnnotation(ViewField.class);
			sourceName = StringUtils.defaultIfEmpty(annotationInfo.value(), field.getName());
			sourceField = FieldUtils.getField(viewInfo.getViewClass(), sourceName, forceAccess);
			if (sourceField == null){
				if(!annotationInfo.isViewReference())
					throw new ViewInstantiationException("Cannot find the @" + ViewField.class.getSimpleName()
							+ " source value for " + viewInfo.getControllerInfo().getControllerClass().getName() + "."+ field.getName() + " on view class " + viewInfo.getViewClass().getName());
				FieldUtils.writeField(field, targetController, sourceView, forceAccess);
				continue;
			}
			if (!field.getType().isAssignableFrom(sourceField.getType()))
				throw new ViewInstantiationException("Cannot bind the @" + ViewField.class.getSimpleName()
						+ " source value for " + viewInfo.getControllerInfo().getControllerClass().getName() + "." + field.getName() + ", because are incopatible types");
			sourceValue = FieldUtils.readField(sourceField, sourceView, forceAccess);
			if (sourceValue != null)
				FieldUtils.writeField(field, targetController, sourceValue, forceAccess);
		}
	}
	
	public static void applyConventionStringsForView(Object viewInstance, String messagesFilePath) {
		ViewInfo viewInfo = new ViewInfo();
		viewInfo.setMessages(messagesFilePath);
		viewInfo.setViewStringsByConventions(true);
		viewInfo.setViewClass(viewInstance.getClass());
		applyConventionStringsForView(viewInstance, viewInfo, createMessageBundleManager(viewInfo));
	}

	public static void applyConventionStringsForView(Object viewInstance, ViewInfo viewInfo, MessageBundleManager messageManager) {
		Object fieldValue = null;
		boolean forceAccess = true;
		String localizedCaption = "";
		String localizedDescription = "";
		String localizedTitle = "";

		if (viewInfo.isViewStringsByConventions() && messageManager != null && viewInstance instanceof FormPanel) {
			localizedTitle = LocaleMessagesUtil.conventionTitle(viewInfo.getViewClass(), messageManager);
			((FormPanel) viewInstance).setTitle(localizedTitle);
		}

		for (Field viewField : viewInfo.getViewClass().getDeclaredFields()) {
			if (VWebUtils.isVField(viewField.getType())) {
				try {
					fieldValue = FieldUtils.readField(viewField, viewInstance, forceAccess);
				} catch (IllegalAccessException e) {
					LOGGER.error(e.toString(), e);
				}
				if (fieldValue == null)
					continue;
				localizedCaption = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager,
						viewField.getName());
				localizedDescription = LocaleMessagesUtil.conventionDescription(viewInfo.getViewClass(), messageManager,
						viewField.getName());
				if (viewInfo.isViewStringsByConventions() && messageManager != null) {
					if (FormPanel.class.isAssignableFrom(viewField.getType()) && fieldValue != viewInstance && permitConventionStringCaption(viewField)) {
						localizedTitle = LocaleMessagesUtil.conventionTitle(viewInfo.getViewClass(), messageManager);
						((FormPanel) fieldValue).setTitle(localizedTitle);
					}
					else if (Panel.class.isAssignableFrom(viewField.getType()) && fieldValue != viewInstance && permitConventionStringCaption(viewField)) {
						((Panel) fieldValue).setCaption(localizedCaption);
					}
					else if (TreeMenu.class.isAssignableFrom(viewField.getType()) && permitConventionStringCaption(viewField))
						((TreeMenu) fieldValue).setTitle(localizedCaption);
					else if (Label.class.isAssignableFrom(viewField.getType()) && permitConventionStringCaption(viewField))
						((Label) fieldValue).setValue(localizedCaption);
					else {
						((AbstractComponent) fieldValue).setCaption(permitConventionStringCaption(viewField) ? localizedCaption : null);
						((AbstractComponent) fieldValue).setDescription(permitConventionStringDescription(viewField) ? StringUtils.defaultIfEmpty(localizedDescription, localizedCaption) : null);
					}

					if (Table.class.isAssignableFrom(viewField.getType())
							&& ((Table) fieldValue).getColumnHeaderMode().equals(ColumnHeaderMode.EXPLICIT) && permitConventionStringCaption(viewField)) {
						String tableColumnTitle = null;
						for (Object propertyId : ((Table) fieldValue).getContainerPropertyIds()) {
							tableColumnTitle = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager, viewField.getName() + "." + propertyId);
							((Table) fieldValue).setColumnHeader(propertyId, tableColumnTitle);
						}
					} else if (Grid.class.isAssignableFrom(viewField.getType()) && permitConventionStringCaption(viewField)) {
						String tableColumnTitle = null;
						for (Column column : ((Grid) fieldValue).getColumns()) {
							tableColumnTitle = LocaleMessagesUtil.conventionCaption(viewInfo.getViewClass(), messageManager, viewField.getName() + "." + column.getPropertyId());
							((Grid) fieldValue).getColumn(column.getPropertyId()).setHeaderCaption(tableColumnTitle);
						}
					}
				}
			}
		}
	}
	
	public static void applyValidatorsForView(Object viewInstance) throws ParseException{
		ViewInfo viewInfo = new ViewInfo();
		viewInfo.setViewStringsByConventions(true);
		viewInfo.setViewClass(viewInstance.getClass());
		applyValidatorsForView(viewInstance, viewInfo);
	}
	
	public static void applyValidatorsForView(Object viewInstance, ViewInfo viewInfo) throws ParseException {
		Object fieldValue = null;
		boolean forceAccess = true;
		String fieldCaption = null;

		for (Field viewField : viewInfo.getViewClass().getDeclaredFields()) {
			if (VWebUtils.isVField(viewField.getType())) {
				try {
					fieldValue = FieldUtils.readField(viewField, viewInstance, forceAccess);
				} catch (IllegalAccessException e) {
					LOGGER.error(e.toString(), e);
				}
				if (fieldValue == null) continue;
				fieldCaption = ((Component)fieldValue).getCaption();
				
				if (Validatable.class.isAssignableFrom(viewField.getType())) {
					Annotation[] fieldAnnotations = viewField.getAnnotations();
					for (Annotation annotation : fieldAnnotations) {
						if (annotation instanceof NullValidator)
							((Validatable) fieldValue).addValidator(ValidationUtils.nullValidator(StringUtils.defaultIfEmpty(((NullValidator) annotation).fieldCaption(), fieldCaption)));
						else if (annotation instanceof DateRangeValidator) {
							String min = StringUtils.defaultString(((DateRangeValidator) annotation).min());
							String max = StringUtils.defaultString(((DateRangeValidator) annotation).max());
							String format = StringUtils.defaultIfEmpty(((DateRangeValidator) annotation).max(),
									FMWConstants.TIMESTAMP_SECONDS_FORMAT);

							Date minDate = min.equals(DateRangeValidator.CURRENT_DATE) ? new Date() : (min.equals("") ? null : new SimpleDateFormat(format).parse(min));
							Date maxDate = max.equals(DateRangeValidator.CURRENT_DATE) ? new Date() : (max.equals("") ? null : new SimpleDateFormat(format).parse(max));

							((Validatable) fieldValue).addValidator(ValidationUtils.dateRangeValidator(StringUtils.defaultIfEmpty(((DateRangeValidator) annotation).fieldCaption(), fieldCaption), minDate, maxDate));
						} else if (annotation instanceof DoubleRangeValidator){
							((Validatable) fieldValue).addValidator(ValidationUtils.doubleRangeValidator(
									StringUtils.defaultIfEmpty(((DoubleRangeValidator) annotation).fieldCaption(), fieldCaption), ((DoubleRangeValidator) annotation).min(), ((DoubleRangeValidator) annotation).max()));
							applyFieldRangeConstraintIfPossyble(fieldValue, ((DoubleRangeValidator) annotation).max());
						}else if (annotation instanceof IntRangeValidator){
							((Validatable) fieldValue).addValidator(ValidationUtils.integerRangeValidator(
									StringUtils.defaultIfEmpty(((IntRangeValidator) annotation).fieldCaption(), fieldCaption), ((IntRangeValidator) annotation).min(), ((IntRangeValidator) annotation).max()));
							applyFieldRangeConstraintIfPossyble(fieldValue, ((DoubleRangeValidator) annotation).max());
						}else if (annotation instanceof EmailValidator)
							((Validatable) fieldValue).addValidator(ValidationUtils.emailValidator(StringUtils.defaultIfEmpty(((EmailValidator) annotation).fieldCaption(), fieldCaption)));
						else if (annotation instanceof StringLengthValidator){
							((Validatable) fieldValue).addValidator(ValidationUtils.stringLengthRangeValidator(
									StringUtils.defaultIfEmpty(((StringLengthValidator) annotation).fieldCaption(), fieldCaption), ((StringLengthValidator) annotation).min(), ((StringLengthValidator) annotation).max()));
							applyFieldLengthConstraintIfPossyble(fieldValue, ((StringLengthValidator) annotation).max());
						}else if (annotation instanceof RegExpValidator)
							((Validatable) fieldValue).addValidator(ValidationUtils.regexpValidator(
									StringUtils.defaultIfEmpty(((RegExpValidator) annotation).fieldCaption(), fieldCaption), ((RegExpValidator) annotation).expression(), ((RegExpValidator) annotation).example()));
						else if (annotation instanceof AddressValidator && AddressEditorField.class.isAssignableFrom(viewField.getType()))
							((Validatable) fieldValue).addValidator(ValidationUtils.addressValidator(StringUtils.defaultIfEmpty(((AddressValidator) annotation).fieldCaption(), fieldCaption),
									((AddressEditorField) fieldValue).getFieldsToPropertyMapping()));
					}
				}
			}
		}
	}
	
	private static void applyFieldLengthConstraintIfPossyble(Object fieldValue, int maxLength){
		if( maxLength <= 0 || maxLength == Integer.MAX_VALUE || !(fieldValue instanceof AbstractTextField) ) return;
		((AbstractTextField)fieldValue).setMaxLength(maxLength);
	}
	
	private static void applyFieldRangeConstraintIfPossyble(Object fieldValue, double maxValue){
		if(maxValue <= 0 || maxValue >= Double.MAX_VALUE || !(fieldValue instanceof AbstractTextField)) return;
		int maxLength = String.valueOf((int)maxValue + 1).length();
		((AbstractTextField)fieldValue).setMaxLength(maxLength);
	}
	
	private static boolean permitConventionStringCaption(Field field){
		return !field.isAnnotationPresent(NoConventionString.class)
				|| field.getAnnotation(NoConventionString.class).permitCaption();
	}
	
	private static boolean permitConventionStringDescription(Field field){
		return !field.isAnnotationPresent(NoConventionString.class)
				|| field.getAnnotation(NoConventionString.class).permitDescription();
	}

	@SuppressWarnings("unchecked")
	public static void injectIOCProviderDependencies(Object targetObject, Class<?> targetClazz) throws FMWException {
		boolean forceAccess = true;
		Set<Field> annotatedFields = null;
		Set<Method> annotatedMethods = null;
		
		for (Class<?> annot : dependencyAnnotations) {
			annotatedFields = ReflectionUtils.getFields(targetClazz, ReflectionUtils.withAnnotation((Class<? extends Annotation>) annot));
			annotatedMethods = ReflectionUtils.getMethods(targetClazz, ReflectionUtils.withAnnotation((Class<? extends Annotation>) annot));
			try {
				for (Field field : annotatedFields) {
					FieldUtils.writeField(field, targetObject, IOCHelper.getBean(field.getType()), forceAccess);
				}

				for (Method method : annotatedMethods) {
					Class<?> paramTypes[] = method.getParameterTypes();
					if (paramTypes.length != 1)
						throw new FMWException("You have annotated the Method " + method.getName() + " with @"+ annot.getSimpleName() + ", "
								+ "but this method did not match the required one Parameter (exactly one) for Dependency Injection");

					MethodUtils.getAccessibleMethod(method).invoke(targetObject, IOCHelper.getBean(paramTypes[0]));
				}
			} catch (IllegalArgumentException | ReflectiveOperationException ex) {
				throw new FMWException(ex);
			}
		}

	}
	
	public static void navigateTo(String targetURI){
		Page.getCurrent().setUriFragment(encodeURI(targetURI));
	}
	
	public static String encodeURI(String targetURI){
		String transformedPath = targetURI;
		if(hasRequestParameters(targetURI)){
			String pathInfo = targetURI.substring(targetURI.indexOf("?")+1);
			String encodedParams = StringUtils.isEmpty(pathInfo) ? "" : Base64.byteArrayToBase64(pathInfo.getBytes());
			transformedPath = targetURI.substring(0, targetURI.indexOf("?") + 1) + encodedParams ;
		}
		return transformedPath;
	}
	
	public static boolean hasRequestParameters(String targetURI){
		int urlinfoIndex = StringUtils.defaultString(targetURI).indexOf("?");
		String pathInfo = (urlinfoIndex >= 0) ? targetURI.substring(urlinfoIndex+1) : null;
		return pathInfo != null && 
				!ViewProvider.AUTHENTICATION_VIEW_PARAM_IDENTIFIER.equals(pathInfo) &&
				!ViewProvider.DASHBOARD_VIEW_PARAM_IDENTIFIER.equals(pathInfo) &&
				!ViewProvider.FORBIDDEN_VIEW_PARAM_IDENTIFIER.equals(pathInfo) &&
				!ViewProvider.RESNOTFOUND_VIEW_PARAM_IDENTIFIER.equals(pathInfo);
	}
}
