package co.com.binariasystems.fmw.vweb.uicomponet;

import static co.com.binariasystems.fmw.util.CommonUtils.ln;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class PanelGrid extends HorizontalLayout{
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
	
	
	public PanelGrid(int columns){
		this(columns, null);
	}
	
	public PanelGrid(String title){
		this(1, title);
	}
	
	public PanelGrid(int columns, String title){
		super();
		this.columns = columns <= 0 ? 1 : columns;
		this.title = title;
		
		initContent();
	}
	
	private void initContent(){
		content = new Panel(title);
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
	
	public PanelGrid setColumnExpandRatio(int columnIndex, float ratio){
		panelGroup.setColumnExpandRatio(columnIndex, ratio);
		return this;
	}
	
	public PanelGrid add(Component component){
		panelGroup.add(component);
		return this;
	}
	
	public PanelGrid add(Component component, Dimension width){
		panelGroup.add(component, width);
		return this;
	}
	
	public PanelGrid add(Component component, Alignment align){
		panelGroup.add(component, align);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan){
		panelGroup.add(component, colSpan);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align){
		panelGroup.add(component, colSpan, align);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Dimension width){
		panelGroup.add(component, colSpan, width);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align, Dimension width){
		panelGroup.add(component, colSpan, align, width);
		return this;
	}
	
	public PanelGrid addCenteredOnNewRow(Component... components){
		panelGroup.addCenteredOnNewRow(components);
		return this;
	}
	
	public PanelGrid addEmptyRow(){
		panelGroup.addEmptyRow();
		return this;
	}
	
	public PanelGrid addCenteredOnNewRow(Dimension uniformWidth, Component... components){
		panelGroup.addCenteredOnNewRow(uniformWidth, components);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align, Dimension width, Dimension height){
		panelGroup.add(component, colSpan, align, width, height);
		return this;
	}
	
	public PanelGrid setSubmitButton(Button button){
		if(!panelGroup.containsButton(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(resetButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		submitButton = button;
		content.addAction(new ClickShortcut(submitButton, KeyCode.F1));
		return this;
	}

	public PanelGrid setResetButton(Button button){
		if(!panelGroup.containsButton(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(submitButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		resetButton = button;
		content.addAction(new ClickShortcut(resetButton, KeyCode.F4));
		return this;
	}
	
	
	public void initFocus(){
		
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
			itemsBuilder.append(StringUtils.isEmpty(cause.getHtmlMessage()) ? "" : validationErrorItem.format(new Object[]{})).append(ln());
			validationMessages.add(cause.getHtmlMessage());
			for(InvalidValueException subex : cause.getCauses()){
				itemsBuilder.append(StringUtils.isEmpty(subex.getHtmlMessage()) ? "" : validationErrorItem.format(new Object[]{subex.getHtmlMessage()})).append(ln());
				validationMessages.add(subex.getHtmlMessage());
			}
		}
		return new FormValidationException(validationErrorListFmt.format(new Object[]{itemsBuilder.toString()}), validationMessages);
	}
	
	
	@Override
	public void attach() {
		super.attach();
		if(!navigationEventApplieds){
			applyNavigationEvents();
		}
	}
	
	private void applyNavigationEvents(){
		for(int i = 0; i < panelGroup.childComponents.size() && firstFocusComp == null; i++){
			if(panelGroup.childComponents.get(i) instanceof Field && 
					((Field<?>)panelGroup.childComponents.get(i)).isEnabled() 
					&& !((Field<?>)panelGroup.childComponents.get(i)).isReadOnly() )
				firstFocusComp = (Field<?>)panelGroup.childComponents.get(i);
		}
		
		//Navegacion con Tecla ARROW_DOWN
				content.addAction(new ShortcutListener("ControlsNavDown@"+content.hashCode(), KeyCode.ARROW_DOWN, null) {
					@Override
					public void handleAction(Object sender, Object target) {
						if(VWebUtils.isArrowDownNavigableField(target.getClass())){
							boolean go = true;
							int targetIdx = panelGroup.childComponents.indexOf(target);
							if(targetIdx < 0)return;
							
							for(int i = targetIdx + 1; i < panelGroup.childComponents.size() && go; i++){
								Component comp = panelGroup.childComponents.get(i);
								if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
									((Focusable)comp).focus();
									go = !go;
								}
							}
							if(go && firstFocusComp != null)
								firstFocusComp.focus();
						}
					}
				});
				
				//Navegacion con Tecla ARROW_UP
				content.addAction(new ShortcutListener("ControlsNavUp@"+mainPanel.hashCode(), KeyCode.ARROW_UP, null) {
					@Override
					public void handleAction(Object sender, Object target) {
						if(VWebUtils.isArrowUpNavigableField(target.getClass())){
							boolean go = true;
							int targetIdx = panelGroup.childComponents.indexOf(target);
							if(targetIdx < 0)return;
							
							for(int i= targetIdx - 1; i >= 0 && go; i--){
								Component comp = panelGroup.childComponents.get(i);
								if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
									((Focusable)comp).focus();
									go = !go;
								}
							}
						}
					}
				});
				
				//Navegacion con Tecla ENTER
				content.addAction(new ShortcutListener("EnterPress@"+mainPanel.hashCode(), KeyCode.ENTER, null) {
					@Override
					public void handleAction(Object sender, Object target) {
//						if(target instanceof TextField && ((TextField)target).getData() instanceof SearcherField)
//							((SearcherField)((TextField)target).getData()).handleEnterPress((TextField)target);//Lanzar evento en los Buscadores
						if(target == lastComponent && lastComponent.isEnabled() && !lastComponent.isReadOnly() && submitButton != null &&
								(target instanceof TextField || target instanceof PasswordField || target instanceof DateField))
							submitButton.click();
						
						if(isEnterNavigableField(target.getClass())){
							boolean go = true;
							int targetIdx = childComponents.indexOf(target);
							if(targetIdx < 0)return;
							
							for(int i = targetIdx + 1; i < childComponents.size() && go; i++){
								Component comp = childComponents.get(i);
//								if(comp instanceof Button && SearcherField.ACTIONBUTTON_DATA_PREFIX.equals(((Button)comp).getData()))
//									continue;//No hacer nada cuando se llega a un boton de buscador
								if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
									((Focusable)comp).focus();
									go = !go;
								}
							}
							
							if(go && firstFocusComp != null)
								((Field)firstFocusComp).focus();
						}
						
					}
				});
				navigationEventApplieds = !navigationEventApplieds;
	}
	
	public PanelGrid setTitle(String title){
		content.setCaption("");
		return this;
	}
	
	@Override
	public void setCaption(String caption) {
		content.setCaption(caption);
	}
	
	public PanelGrid setWidth(Dimension width){
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
