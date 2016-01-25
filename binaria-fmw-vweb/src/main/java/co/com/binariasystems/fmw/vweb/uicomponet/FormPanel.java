package co.com.binariasystems.fmw.vweb.uicomponet;

import static co.com.binariasystems.fmw.util.CommonUtils.ln;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class FormPanel extends HorizontalLayout{
	private Panel content;
	private PanelGroup panelGroup;
	private Dimension fixedWidth = Dimension.percent(100);
	private int columns;
	private String title;
	
	private Button submitButton;
	private Button resetButton;
	
	private MessageFormat validationErrorListFmt;
	private MessageFormat validationErrorItem;
	private boolean navigationEventApplieds;
	private Field<?> firstFocusComp;
	
	
	public FormPanel(){
		this(1);
	}
	
	public FormPanel(int columns){
		this(columns, null);
	}
	
	public FormPanel(String title){
		this(1, title);
	}
	
	public FormPanel(int columns, String title){
		super();
		this.columns = columns <= 0 ? 1 : columns;
		this.title = title;
		
		initContent();
	}
	
	private void initContent(){
		content = title != null ? new Panel(title) : new Panel();
		panelGroup = new PanelGroup(columns);
		validationErrorListFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.VALIDATION_ERRORLIST_TEMPLATE));
		validationErrorItem = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.VALIDATION_ERRORITEM_TEMPLATE));
		
		content.setCaptionAsHtml(true);
		content.setContent(panelGroup);
		
		addComponent(content);
		
		setWidth(fixedWidth.value, fixedWidth.unit);
		setComponentAlignment(content, Alignment.TOP_CENTER);
		setExpandRatio(content, 1.0f);
		setMargin(true);
	}
	
	public FormPanel setColumnExpandRatio(int columnIndex, float ratio){
		panelGroup.setColumnExpandRatio(columnIndex, ratio);
		return this;
	}
	
	public FormPanel add(Component component){
		panelGroup.add(component);
		return this;
	}
	
	public FormPanel add(Component component, Dimension width){
		panelGroup.add(component, width);
		return this;
	}
	
	public FormPanel add(Component component, Alignment align){
		panelGroup.add(component, align);
		return this;
	}
	
	public FormPanel add(Component component, Alignment align, Dimension width){
		panelGroup.add(component, align, width);
		return this;
	}
	
	public FormPanel add(Component component, int colSpan){
		panelGroup.add(component, colSpan);
		return this;
	}
	
	public FormPanel add(Component component, int colSpan, Alignment align){
		panelGroup.add(component, colSpan, align);
		return this;
	}
	
	public FormPanel add(Component component, int colSpan, Dimension width){
		panelGroup.add(component, colSpan, width);
		return this;
	}
	
	public FormPanel add(Component component, int colSpan, Alignment align, Dimension width){
		panelGroup.add(component, colSpan, align, width);
		return this;
	}
	
	public FormPanel addCenteredOnNewRow(Component... components){
		panelGroup.addCenteredOnNewRow(components);
		return this;
	}
	
	public FormPanel addEmptyRow(){
		panelGroup.addEmptyRow();
		return this;
	}
	
	public FormPanel addCenteredOnNewRow(Dimension uniformWidth, Component... components){
		panelGroup.addCenteredOnNewRow(uniformWidth, components);
		return this;
	}
	
	public FormPanel add(Component component, int colSpan, Alignment align, Dimension width, Dimension height){
		panelGroup.add(component, colSpan, align, width, height);
		return this;
	}
	
	public FormPanel setSubmitButton(Button button){
		if(!panelGroup.childComponents.contains(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(resetButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		submitButton = button;
		content.addAction(new ClickShortcut(submitButton, KeyCode.F1));
		return this;
	}

	public FormPanel setResetButton(Button button){
		if(!panelGroup.childComponents.contains(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(submitButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		resetButton = button;
		content.addAction(new ClickShortcut(resetButton, KeyCode.F4));
		return this;
	}
	
	
	public void initFocus(){
		if(!navigationEventApplieds)
			applyNavigationActions();
		if(firstFocusComp != null)
			firstFocusComp.focus();
	}
	
	public boolean isValid(){
		try{
			validate();
			return true;
		}catch(FormValidationException ex){
			MessageDialog.showValidationErrors(null, ex.getMessage());
			return false;
		}
	}
	
	public void validate() throws FormValidationException{
		List<InvalidValueException> exceptions = new ArrayList<Validator.InvalidValueException>();
		for(Component comp : panelGroup.childComponents){
			if(comp instanceof AbstractField){
				AbstractField<?> field = ((AbstractField<?>)comp);
				try{field.validate();}catch (InvalidValueException ex) {exceptions.add(ex);}
			}	
		}
		if(!exceptions.isEmpty()){
			throw buildFormValidationException(exceptions);
		}
	}
	
	
	private FormValidationException buildFormValidationException(List<InvalidValueException> exceptions){
		StringBuilder itemsBuilder = new StringBuilder();
		List<String> validationMessages = new LinkedList<String>();
		for(InvalidValueException cause: exceptions){
			itemsBuilder.append(StringUtils.isEmpty(cause.getHtmlMessage()) ? "" : validationErrorItem.format(new Object[]{cause.getHtmlMessage()})).append(ln());
			validationMessages.add(cause.getHtmlMessage());
			for(InvalidValueException subex : cause.getCauses()){
				itemsBuilder.append(StringUtils.isEmpty(subex.getHtmlMessage()) ? "" : validationErrorItem.format(new Object[]{subex.getHtmlMessage()})).append(ln());
				validationMessages.add(subex.getHtmlMessage());
			}
		}
		return new FormValidationException(validationErrorListFmt.format(new Object[]{itemsBuilder.toString()}), validationMessages);
	}
	
	private void applyNavigationActions(){
		for(int i = 0; i < panelGroup.childComponents.size() && firstFocusComp == null; i++){
			if(panelGroup.childComponents.get(i) instanceof Field && 
					((Field<?>)panelGroup.childComponents.get(i)).isEnabled() 
					&& !((Field<?>)panelGroup.childComponents.get(i)).isReadOnly() )
				firstFocusComp = (Field<?>)panelGroup.childComponents.get(i);
		}
		
		VWebUtils.applyNavigationActions(content, panelGroup, submitButton);	
		navigationEventApplieds = !navigationEventApplieds;
	}
	
	public FormPanel setTitle(String title){
		content.setCaption(title);
		return this;
	}
	
	protected void setColumns(int columns){
		this.columns = columns <= 0 ? 1 : columns;
		panelGroup.setColumns(columns);
	}
	
	@Override
	public void setCaption(String caption) {
		content.setCaption(caption);
	}
	
	public FormPanel setWidth(Dimension width){
		content.setWidth(width.value, width.unit);
		return this;
	}
	
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(fixedWidth.value, fixedWidth.unit);
		content.setWidth(width, unit);
	}
	
	@Override
	public void setWidth(String width) {
		super.setWidth("100%");
		content.setWidth(width);
	}
}
