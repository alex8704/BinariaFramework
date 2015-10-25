package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import elemental.json.JsonArray;

public class LinkLabel extends CustomField<String>{
	private MessageFormat textFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.LINK_LABEL_TEMPLATE_TEXT));
	private MessageFormat textDisableFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.LINK_LABEL_DISABLE_TEMPLATE_TEXT));
	private ClickHandler clickHandler;
	private String functionName;
	private Label content;
	
	public LinkLabel() {
		this("");
	}
	
	public LinkLabel(String value) {
		functionName = LinkLabel.class.getSimpleName()+"_fn"+Math.abs(hashCode());
		setValue(value);
	}
	
	@Override
	protected Component initContent() {
		content = new Label(textFmt.format(new Object[]{functionName, StringUtils.defaultString(getValue())}),ContentMode.HTML);
		return content;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
//	@Override
//	public void setValue(String newStringValue) {
//		super.setValue(textFmt.format(new Object[]{functionName, StringUtils.defaultString(newStringValue)}));
//	}
	
	public void setClickHandler(ClickHandler clickHandler){
		this.clickHandler = clickHandler;
		if(this.clickHandler != null){
			JavaScript.getCurrent().addFunction(functionName, new LinkLabelDelegateJS(this.clickHandler));
		}
		
	}
	
	private class LinkLabelDelegateJS implements JavaScriptFunction{
		private ClickHandler handler;
		public LinkLabelDelegateJS(ClickHandler handler){
			this.handler = handler;
		}
		
		@Override
		public void call(JsonArray arguments) {
			handler.handleClick();
		}
		
	}
	
	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		super.valueChange(event);
		if(content != null)
			content.setValue((isEnabled() ? textFmt : textDisableFmt).format(isEnabled() ? new Object[]{functionName, StringUtils.defaultString(getValue())} :new Object[]{StringUtils.defaultString(getValue())}));
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(content != null)
			content.setValue((isEnabled() ? textFmt : textDisableFmt).format(isEnabled() ? new Object[]{functionName, StringUtils.defaultString(getValue())} :new Object[]{StringUtils.defaultString(getValue())}));
	}
	
	public static interface ClickHandler{
		public void handleClick();
	}
	
}
