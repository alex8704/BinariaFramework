package co.com.binariasystems.fmw.vweb.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.reflec.TypeHelper;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.Renderer;

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
		return new NumberRenderer(new DecimalFormat(format));
	}
	
	
	public static Renderer<Date> obtainRendererForDate(Class<Date> clazz){
		String format = (Timestamp.class.isAssignableFrom(clazz))
				? FMWConstants.TIMESTAMP_DEFAULT_FORMAT
				: (Time.class.isAssignableFrom(clazz) ? FMWConstants.MEDIANE_TIME_FORMAT : FMWConstants.DATE_DEFAULT_FORMAT);
		
		return new DateRenderer(new SimpleDateFormat(format, VWebUtils.getCurrentUserLocale()));
	}
	
	
	public static void main(String[] args) {
		
	}
	
	public static class ActionLinkInfo{
		private String actionId;
		private String caption;
		private String functionName;
		public String getActionId() {
			return actionId;
		}
		public void setActionId(String actionId) {
			this.actionId = actionId;
		}
		public String getCaption() {
			return caption;
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		public String getFunctionName() {
			return functionName;
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
		
		
	}
	
	public static class ActionIconInfo{
		private String iconPath;
		private String altText;
		private String actionId;
		private String functionName;
		public String getIconPath() {
			return iconPath;
		}
		public void setIconPath(String iconPath) {
			this.iconPath = iconPath;
		}
		public String getAltText() {
			return altText;
		}
		public void setAltText(String altText) {
			this.altText = altText;
		}
		public String getActionId() {
			return actionId;
		}
		public void setActionId(String actionId) {
			this.actionId = actionId;
		}
		public String getFunctionName() {
			return functionName;
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
	}
	
	
	public static class ActionLinkValueGenerator<String> extends PropertyValueGenerator<String>{
		private ActionLinkInfo[] actionLinkInfo;
		public ActionLinkValueGenerator(ActionLinkInfo... actionLinkInfo){
			this.actionLinkInfo = actionLinkInfo;
		}

		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			return null;
		}
		
		@Override
		public Class getType() {
			return null;
		}
		
	}
	
	public static class ActionIconValueGenerator<String> extends PropertyValueGenerator<String>{
		private ActionIconInfo[] actionIconInfo;
		
		public ActionIconValueGenerator(ActionIconInfo... actionIconInfo){
			this.actionIconInfo = actionIconInfo;
		}

		@Override
		public String getValue(Item item, Object itemId, Object propertyId) {
			return null;
		}
		
		@Override
		public Class getType() {
			return null;
		}
	}
}
