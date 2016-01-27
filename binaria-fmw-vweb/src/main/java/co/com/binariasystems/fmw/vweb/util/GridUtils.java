package co.com.binariasystems.fmw.vweb.util;

import static co.com.binariasystems.fmw.util.StringUtils.quote;
import static co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants.SEPARATOR_NBSP;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.resources.resources;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.JsonArray;

public class GridUtils {
	
	@SuppressWarnings("unchecked")
	public static <T>  Renderer<T> obtainRendererForType(Class<T> clazz){
		if(TypeHelper.isDateOrTimeType(clazz))
			return (Renderer<T>) obtainRendererForDate((Class<Date>)clazz);
		if(TypeHelper.isNumericType(clazz)){
			return (Renderer<T>) obtainRendererForNumber((Class<Number>)clazz);
		}
		return null;
	}
	
	public static Renderer<Number> obtainRendererForNumber(Class<Number> clazz){
		String format = TypeHelper.isFloatingPointNumber(clazz) ?
				FMWConstants.FLOAT_POINT_NUMBER_DEFAULT_FORMAT : FMWConstants.INT_NUMBER_DEFAULT_FORMAT;
		return new NumberRenderer(new DecimalFormat(format, new DecimalFormatSymbols(VWebUtils.getCurrentUserLocale())));
	}
	
	
	public static Renderer<Date> obtainRendererForDate(Class<Date> clazz){
		String format = (Timestamp.class.isAssignableFrom(clazz))
				? FMWConstants.TIMESTAMP_DEFAULT_FORMAT
				: (Time.class.isAssignableFrom(clazz) ? FMWConstants.MEDIANE_TIME_FORMAT : FMWConstants.DATE_DEFAULT_FORMAT);
		
		return new DateRenderer(new SimpleDateFormat(format, VWebUtils.getCurrentUserLocale()));
	}
	
	public static class ActionLinkInfo{
		private String actionId;
		private String caption;
		
		public ActionLinkInfo(String actionId, String caption) {
			this.actionId = actionId;
			this.caption = caption;
		}
		public String getActionId() {
			return StringUtils.defaultIfBlank(actionId, "-");
		}
		public void setActionId(String actionId) {
			this.actionId = actionId;
		}
		public String getCaption() {
			return StringUtils.defaultIfBlank(caption, "-");
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		
	}
	
	public static class ActionIconInfo{
		private String iconPath;
		private String disabledIconPath;
		private String altText;
		private String actionId;
		
		public ActionIconInfo(String iconPath, String disabledIconPath, String altText, String actionId) {
			this.iconPath = iconPath;
			this.disabledIconPath = disabledIconPath;
			this.altText = altText;
			this.actionId = actionId;
		}
		
		public String getIconPath() {
			return StringUtils.defaultString(iconPath);
		}
		public void setIconPath(String iconPath) {
			this.iconPath = iconPath;
		}
		public String getAltText() {
			return StringUtils.defaultString(altText);
		}
		public void setAltText(String altText) {
			this.altText = altText;
		}
		public String getActionId() {
			return StringUtils.defaultIfBlank(actionId, "-");
		}
		public void setActionId(String actionId) {
			this.actionId = actionId;
		}
		public String getDisabledIconPath() {
			return StringUtils.defaultString(disabledIconPath, getIconPath());
		}
		public void setDisabledIconPath(String disabledIconPath) {
			this.disabledIconPath = disabledIconPath;
		}
		
	}
	
	public static interface ActionEnableValidor{
		public boolean enableAction(Item item, String actionId);
	}
	
	public static interface ActionHandler{
		public void handleAction(String selectedId, String actionId);
	}
	
	private static class ActionDelegateJS implements JavaScriptFunction{
		private ActionHandler handler;
		public ActionDelegateJS(ActionHandler handler){
			this.handler = handler;
		}
		
		@Override
		public void call(JsonArray arguments) {
			handler.handleAction(arguments.getString(0), arguments.getString(1));
		}
		
	}
	
	
	public static class ActionLinkValueGenerator extends PropertyValueGenerator<String>{
		private ActionLinkInfo[] actionLinkInfo;
		private MessageFormat actionLinkFmt;
		private MessageFormat disabledActionLinkFmt;
		private Object idProperty;
		private ActionEnableValidor enableValidator;
		private ActionHandler actionHandler;
		private String functionName;
		
		public ActionLinkValueGenerator(Object idProperty, ActionLinkInfo... actionLinkInfo){
			this(idProperty, null, actionLinkInfo);
		}
		
		public ActionLinkValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionLinkInfo... actionLinkInfo){
			this(idProperty, enableValidator, null, actionLinkInfo);
		}
		
		public ActionLinkValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionHandler actionHandler, ActionLinkInfo... actionLinkInfo){
			this(idProperty, enableValidator, actionHandler, null, actionLinkInfo);
		}
		
		public ActionLinkValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionHandler actionHandler, String functionName, ActionLinkInfo... actionLinkInfo){
			this.idProperty = idProperty;
			this.actionLinkInfo = actionLinkInfo;
			this.enableValidator = enableValidator;
			this.setFunctionName(functionName);
			this.setActionHandler(actionHandler);
			actionLinkFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.ACTION_LINK_TEMPLATE_TEXT));
			disabledActionLinkFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.ACTION_LINK_DISALE_TEMPLATE_TEXT));
		}

		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			if(propertyId == null) return null;
			StringBuilder builder = new StringBuilder();
			if(actionLinkInfo != null){
				for(ActionLinkInfo info : actionLinkInfo){
					builder.append(builder.length() == 0 ? "<nav>" : SEPARATOR_NBSP)
					.append(generateHtmlActionInfo(info, item, propertyId));
				}
				builder.append("</nav>");
			}
			return StringUtils.defaultIfEmpty(builder.toString(), "-");
		}
		
		@SuppressWarnings("unchecked")
		private String generateHtmlActionInfo(ActionLinkInfo info, Item item, Object propertyId){
			boolean enabled = enableValidator == null ? true : enableValidator.enableAction(item, info.getActionId());
			MessageFormat fmt = enabled ? actionLinkFmt : disabledActionLinkFmt;
			Property<Object> itemProperty = item.getItemProperty(idProperty != null ? idProperty : propertyId);
			String selectedId = (itemProperty != null && itemProperty.getValue() != null) ? itemProperty.getValue().toString() : "";
			Object[] args = enabled ? new Object[]{getFunctionName(), quote(selectedId), quote(info.getActionId()), info.getCaption()} : 
			new Object[]{info.getCaption()};
			return fmt.format(args);
		}
		
		@Override
		public Class<String> getType() {
			return String.class;
		}
		
		public ActionEnableValidor getEnableValidator() {
			return enableValidator;
		}

		public void setEnableValidator(ActionEnableValidor enableValidator) {
			this.enableValidator = enableValidator;
		}
		
		public String getFunctionName() {
			return StringUtils.defaultIfBlank(functionName, "return false;");
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}

		public ActionHandler getActionHandler() {
			return actionHandler;
		}

		public void setActionHandler(ActionHandler actionHandler) {
			this.actionHandler = actionHandler;
			if(this.actionHandler != null && StringUtils.isNotBlank(functionName)){
				JavaScript.getCurrent().addFunction(getFunctionName(), new ActionDelegateJS(this.actionHandler));
			}
		}
		
	}
	
	public static class ActionIconValueGenerator extends PropertyValueGenerator<String>{
		private ActionIconInfo[] actionIconInfo;
		private MessageFormat actionIconFmt;
		private MessageFormat disabledActionIconFmt;
		private Object idProperty;
		private ActionEnableValidor enableValidator;
		private ActionHandler actionHandler;
		private String functionName;
		
		public ActionIconValueGenerator(Object idProperty, ActionIconInfo... actionIconInfo){
			this(idProperty, null, actionIconInfo);
		}
		
		public ActionIconValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionIconInfo... actionIconInfo){
			this(idProperty, enableValidator, null, actionIconInfo);
		}
		
		public ActionIconValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionHandler actionHandler, ActionIconInfo... actionIconInfo){
			this(idProperty, enableValidator, actionHandler, null, actionIconInfo);
		}
		
		public ActionIconValueGenerator(Object idProperty, ActionEnableValidor enableValidator, ActionHandler actionHandler, String functionName, ActionIconInfo... actionIconInfo){
			this.idProperty = idProperty;
			this.actionIconInfo = actionIconInfo;
			this.enableValidator = enableValidator;
			this.setFunctionName(functionName);
			this.setActionHandler(actionHandler);
			actionIconFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.ACTION_ICON_TEMPLATE_TEXT));
			disabledActionIconFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.ACTION_ICON_DISALE_TEMPLATE_TEXT));
		}

		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			if(propertyId == null) return null;
			StringBuilder builder = new StringBuilder();
			if(actionIconInfo != null){
				for(ActionIconInfo info : actionIconInfo){
					builder.append(builder.length() == 0 ? "<nav>" : SEPARATOR_NBSP)
					.append(generateHtmlActionInfo(info, item, propertyId));
				}
				builder.append("</nav>");
			}
			return StringUtils.defaultIfEmpty(builder.toString(), "-");
		}
		
		@SuppressWarnings("unchecked")
		private String generateHtmlActionInfo(ActionIconInfo info, Item item, Object propertyId){
			boolean enabled = enableValidator == null ? true : enableValidator.enableAction(item, info.getActionId());
			MessageFormat fmt = enabled ? actionIconFmt : disabledActionIconFmt;
			Property<Object> itemProperty = item.getItemProperty(idProperty != null ? idProperty : propertyId);
			String selectedId = (itemProperty != null && itemProperty.getValue() != null) ? itemProperty.getValue().toString() : "";
			Object[] args = enabled ? new Object[]{getFunctionName(), quote(selectedId), quote(info.getActionId()), info.getIconPath(), info.getAltText()} : 
			new Object[]{info.getDisabledIconPath(), info.getAltText()};
			return fmt.format(args);
		}
		
		@Override
		public Class<String> getType() {
			return String.class;
		}
		
		public ActionEnableValidor getEnableValidator() {
			return enableValidator;
		}

		public void setEnableValidator(ActionEnableValidor enableValidator) {
			this.enableValidator = enableValidator;
		}
		
		public String getFunctionName() {
			return StringUtils.defaultIfBlank(functionName, "return false;");
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}

		public ActionHandler getActionHandler() {
			return actionHandler;
		}

		public void setActionHandler(ActionHandler actionHandler) {
			this.actionHandler = actionHandler;
			if(this.actionHandler != null && StringUtils.isNotBlank(functionName)){
				JavaScript.getCurrent().addFunction(getFunctionName(), new ActionDelegateJS(this.actionHandler));
			}
		}
	}
	
	public static class YesNoPropertyValueGenerator extends PropertyValueGenerator<String>{
		private MessageFormat textFormat;
		private String yesIconPath;
		private String noIconPath;
		
		public YesNoPropertyValueGenerator() {
			this(resources.getAbsoluteImageFilePath("yes.png"), resources.getAbsoluteImageFilePath("no.png"));
		}
		
		public YesNoPropertyValueGenerator(String yesIconPath, String noIconPath) {
			this.yesIconPath = yesIconPath;
			this.noIconPath = noIconPath;
			textFormat = new MessageFormat(VWebCommonConstants.YES_NO_COLUMN_TEMPLATE);
		}

		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			if(propertyId == null) return null;
			Property<Boolean> value = item.getItemProperty(propertyId);
			return textFormat.format(new Object[]{value.getValue() ? yesIconPath : noIconPath});
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}
	}
	
	public static class GenericStringPropertyGenerator extends PropertyValueGenerator<String>{

		private String defaultIfNull;
		private String separatorCompositeObjects;
		
		public GenericStringPropertyGenerator(){
			this(null);
		}
		
		public GenericStringPropertyGenerator(String defaultIfNull){
			this.defaultIfNull = StringUtils.defaultString(defaultIfNull, FMWConstants.DEFAULT_NULL_REPRESENTATION);
		}
		
		public GenericStringPropertyGenerator(String defaultIfNull, String separatorCompositeObjects){
			this.defaultIfNull = StringUtils.defaultString(defaultIfNull, "-");
			this.separatorCompositeObjects = StringUtils.defaultString(separatorCompositeObjects, FMWConstants.WHITE_SPACE);
		}
		
		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			if(propertyId == null) return null;
			Object value = item.getItemProperty(propertyId).getValue();
			try {
				return value == null ? defaultIfNull : FMWEntityUtils.generateStringRepresentationForField(value, separatorCompositeObjects);
			} catch (FMWException e) {
				Throwable cause = FMWExceptionUtils.prettyMessageException(e);
				throw new FMWUncheckedException(cause.getMessage(), cause);
			}
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}
		
	}
	
	public static class SimpleStyleInfo{
		public String styleName;
		public Object targetPropertyId;
		
		public SimpleStyleInfo(Object targetPropertyId, String styleName) {
			this.styleName = styleName;
			this.targetPropertyId = targetPropertyId;
		}
		public String getStyleName() {
			return styleName;
		}
		public void setStyleName(String styleName) {
			this.styleName = styleName;
		}
		public Object getTargetPropertyId() {
			return targetPropertyId;
		}
		public void setTargetPropertyIds(Object targetPropertyId) {
			this.targetPropertyId = targetPropertyId;
		}
		
		
	}
	
	public static class SimpleCellStyleGenerator implements CellStyleGenerator{
		SimpleStyleInfo[] styleInfo;
		public SimpleCellStyleGenerator(SimpleStyleInfo... styleInfo){
			this.styleInfo = styleInfo;
		}
		
		@Override
		public String getStyle(CellReference cellReference) {
			if(styleInfo == null) return null;
			
			String resp = null;
			for(SimpleStyleInfo info : styleInfo){
				if(info.getTargetPropertyId().equals(cellReference.getPropertyId())){
					resp = info.getStyleName();
					break;
				}
			}
			return resp;
		}
		
	}
}
