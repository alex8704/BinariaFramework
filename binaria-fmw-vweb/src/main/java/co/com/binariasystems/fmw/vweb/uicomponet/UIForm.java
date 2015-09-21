package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

/*
 * Clase que representa un formulario para capturar/mostrar informacion
 * al usuario final, permite flexibilidad para la disposicion de los diferentes
 * componentes Graficos de la Interfaz de Usuario, tiene opciones para el control
 * de navegacion entre los controles de la interfaz, validacion de formulario, entre
 * otras opciones
 * 
 * @author Alexander Castro O.
 */

public class UIForm extends HorizontalLayout{
	public static final int FIRST = 0x01;
	public static final int LAST = 0x02;
	protected Panel mainPanel;
	protected VerticalLayout layout;
	protected HorizontalLayout currentRow;
	protected float currentRowExpandRatio = 0.0f;
	protected List<Component> childComponents = new LinkedList<Component>();
	protected MessageFormat requiredFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.UIFORM_REQUIRED_ERROR));
	protected Button submitButton;
	protected Button resetButton;
	protected Component firstComponent;
	protected Component lastComponent;
	protected boolean navigationEventsApplied;
	
	public UIForm(){
		this(null, 90, Unit.PERCENTAGE);
	}
	
	public UIForm(String title, float width, Unit widthUnit){
		//Layout Principal
		mainPanel = new Panel(title);
		layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		mainPanel.setContent(layout);
		addComponent(mainPanel);
		setExpandRatio(mainPanel, 1.0f);
		setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
		setWidth(width, widthUnit);
		setHeight(100, Unit.PERCENTAGE);
	}
	
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(100.0f, Unit.PERCENTAGE);
		mainPanel.setWidth(width, unit);
	}
	
	@Override
	public void setWidth(String width) {
		super.setWidth("100%");
		mainPanel.setWidth(width);
	}
	
	
	public UIForm add(Component component) {
		add(component, FIRST | LAST, 100, Alignment.TOP_LEFT);
		return this;
	}
	
	public UIForm add(Component component, Alignment alignment) {
		return add(component, FIRST | LAST, 100, Alignment.TOP_LEFT);
	}

	public UIForm add(Component component, float widthPercentage) {
		return add(component, FIRST | LAST, widthPercentage);
	}
	
	public UIForm add(Component component, float widthPercentage, Alignment alignment) {
		return add(component, FIRST | LAST, widthPercentage, Alignment.TOP_LEFT);
	}

	public UIForm add(Component component, int flags, float widthPercentage) {
		return add(component, flags, widthPercentage, Alignment.TOP_LEFT);
	}
	
	public UIForm add(Component component, int flags, float widthPercentage, Alignment alignment) {
		boolean isFirst = (flags & FIRST) != 0;
		boolean isLast = (flags & LAST) != 0;
		
		validateComponentAlreadyAdded(component);

		if (isFirst) {
			currentRow = new HorizontalLayout();
			currentRow.setSpacing(true);
			currentRow.setWidth(100, Unit.PERCENTAGE);
			currentRowExpandRatio = 0.0f;
			layout.addComponent(currentRow);
		}
		float availableExpandRatio = 1.0f - currentRowExpandRatio;
		float expandRatio = widthPercentage / 100.0f;
		float modExpandRatio = (availableExpandRatio - expandRatio) * 100;
		//if (modExpandRatio < 0 || (isLast && modExpandRatio < 1))
		if (isLast && (modExpandRatio < 1.0f && modExpandRatio > 0.0f))
			expandRatio = availableExpandRatio;

		currentRow.addComponent(component);
		if(component.getWidth() == Sizeable.SIZE_UNDEFINED)
			component.setWidth(100.0f, Unit.PERCENTAGE);
		if(widthPercentage > 0)
			currentRow.setExpandRatio(component, expandRatio);
		currentRowExpandRatio = currentRowExpandRatio + expandRatio;
		if (isLast && modExpandRatio >= 1) {
			Label fillLabel = new Label();
			currentRow.addComponent(fillLabel);
			currentRow.setExpandRatio(fillLabel, availableExpandRatio - expandRatio);
		}
		
		currentRow.setComponentAlignment(component, (alignment != null) ? alignment : Alignment.TOP_LEFT);
		
		
		addChildComponent(component);
		applyFormNavigationEvents(component);
		return this;
	}
	
	public UIForm addCentered(Component... components){
		return addCentered(Sizeable.SIZE_UNDEFINED, null, components);
	}
	
	public UIForm addCentered(float componentsWidth, Unit componentsWidthUnit, Component... components){	
		if(components == null || components.length == 0){
			add(new Label("&nbsp;", ContentMode.HTML));
			return this;
		}
		
		boolean useProvidedWidth = (componentsWidth > 0);
		Unit unit = componentsWidthUnit;
		if(useProvidedWidth)
			unit = (unit != null) ? unit : Unit.PIXELS;
		int flags = 0;
		boolean center = false;
		for(int i = 0; i < components.length; i++){
			flags = 0;
			if(i == 0) flags = flags| FIRST;
			if(i == components.length - 1) flags = flags | LAST;
			
			if(useProvidedWidth && components[i].getWidth() == SIZE_UNDEFINED)
				components[i].setWidth(componentsWidth, unit);
			center = ((flags & FIRST) != 0 && (flags & LAST) != 0) || flags == 0;
			Alignment align = center ? Alignment.TOP_CENTER : ((flags & FIRST) != 0 ? Alignment.TOP_RIGHT : Alignment.TOP_LEFT);
			float widthPercentage = flags == 0.0f ? 0.0f : 100.0f;
			add(components[i], flags, widthPercentage, align);
		}
		
		return this;
		
	}

	public UIForm addSectionSeparator(String text) {
		Label sectionSeparator = new Label(text, ContentMode.HTML);
		sectionSeparator.addStyleName("section-separator");
		add(sectionSeparator);
		return this;
	}
	
	private void addChildComponent(Component component){
		if(ComponentContainer.class.isAssignableFrom(component.getClass())){
			Iterator<Component> compIt = ((ComponentContainer)component).iterator();
			for(;compIt.hasNext();)
				addChildComponent(compIt.next());
		}
		else if(isControlClass(component.getClass())){
			childComponents.add(component);
			if(firstComponent == null)
				firstComponent = component;
			if(!(component instanceof Button))
				lastComponent = component;
		}
			
	}
	
	private void validateComponentAlreadyAdded(Component component){
		if(ComponentContainer.class.isAssignableFrom(component.getClass())){
			Iterator<Component> compIt = ((ComponentContainer)component).iterator();
			for(;compIt.hasNext();)
				validateComponentAlreadyAdded(compIt.next());
		}
		else if(isControlClass(component.getClass()) && childComponents.contains(component))
			throw new IllegalArgumentException("Component '"+component.getCaption()+"' of type "+component.getClass().getSimpleName()+"is already added on this container");
	}

	private void applyFormNavigationEvents(Component component) {
		if(component instanceof AbstractField){
			//((AbstractField)component).setRequiredError(requiredFmt.format(new Object[]{}));
		}
			
	}
	
	public static boolean isControlClass(Class<?> fieldClass){
        return (Field.class.isAssignableFrom(fieldClass) || 
                Label.class.isAssignableFrom(fieldClass) ||
                Link.class.isAssignableFrom(fieldClass) ||
                Upload.class.isAssignableFrom(fieldClass) ||
                Button.class.isAssignableFrom(fieldClass));
    }

	public String getTitle() {
		return mainPanel.getCaption();
	}

	public UIForm setTitle(String title) {
		mainPanel.setCaption(title);
		return this;
	}
	
	public UIForm setSubmitButton(Button arg){
		if(!childComponents.contains(arg))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(resetButton != null && arg.equals(resetButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		submitButton = arg;
		mainPanel.addAction(new ClickShortcut(submitButton, KeyCode.F1));
		return this;
	}

	public UIForm setResetButton(Button arg){
		if(!childComponents.contains(arg))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(submitButton != null && arg.equals(submitButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		resetButton = arg;
		mainPanel.addAction(new ClickShortcut(resetButton, KeyCode.F4));
		
		return this;
	}

	public boolean validate(){
		boolean hasErrors = false;
		List<InvalidValueException> exs = new ArrayList<Validator.InvalidValueException>();
		for(Component comp : childComponents){
			if(comp instanceof AbstractField){
				AbstractField field = ((AbstractField)comp);
				try{
				field.validate();
				}catch (InvalidValueException ex) {
					exs.add(ex);
					hasErrors = true;
				}
			}	
		}
		if(hasErrors)
			showInvalidValueMessages(exs);
		return !hasErrors;
	}
	
	public void showInvalidValueMessages(List<InvalidValueException> exs){
		StringBuilder msgBuilder = new StringBuilder()
		.append("<ul class='err-msgs-applist'>");
		for(InvalidValueException cause: exs){
			if(!StringUtils.isEmpty(cause.getMessage()))
				msgBuilder.append("<li>").append(cause.getMessage()).append("</li>");
			for(InvalidValueException subex : cause.getCauses())
				msgBuilder.append("<li>").append(subex.getMessage()).append("</li>");
		}
		msgBuilder.append("</ul>");
		MessageDialog.showValidationErrors(null, msgBuilder.toString());
	}
	
	public void initFocus(){
		Component focusComp = null;
		for(Component comp : childComponents){
			if(comp instanceof Field && ((Field)comp).isEnabled() && !((Field)comp).isReadOnly() ){
				focusComp = comp;
				break;
			}
		}
		if(focusComp != null)
			((Field)focusComp).focus();
		
		if(navigationEventsApplied)
			return;
		
		final Component firstFocusComp = focusComp;
		
		
		//Navegacion con Tecla ARROW_DOWN
		mainPanel.addAction(new ShortcutListener("ControlsNavDown@"+mainPanel.hashCode(), KeyCode.ARROW_DOWN, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(isArrowDownNavigableField(target.getClass())){
					boolean go = true;
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i = targetIdx + 1; i < childComponents.size() && go; i++){
						Component comp = childComponents.get(i);
						if(comp instanceof Button && ((Button)comp).getData() != null && ((Button)comp).getData().equals(SearcherField.ACTIONBUTTON_DATA_PREFIX))
							continue;//No hacer nada cuando se llega a un boton de buscador
//						if(comp instanceof TextField && ((TextField)comp).getData() instanceof SearchTextField)
//							((SearchTextField)((TextField)comp).getData()).handleEnterPress((TextField)comp);//Lanzar evento en los Buscadores
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
		
		//Navegacion con Tecla ARROW_UP
		mainPanel.addAction(new ShortcutListener("ControlsNavUp@"+mainPanel.hashCode(), KeyCode.ARROW_UP, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(isArrowUpNavigableField(target.getClass())){
					boolean go = true;
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i= targetIdx - 1; i >= 0 && go; i--){
						Component comp = childComponents.get(i);
						if(comp instanceof Button && ((Button)comp).getData() != null && ((Button)comp).getData().equals(SearcherField.ACTIONBUTTON_DATA_PREFIX))
							continue;//No hacer nada cuando se llega a un boton de buscador
//						if(comp instanceof TextField && ((TextField)comp).getData() instanceof SearchTextField)
//							((SearchTextField)((TextField)comp).getData()).handleEnterPress((TextField)comp);//Lanzar evento en los Buscadores
						if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
							((Focusable)comp).focus();
							go = !go;
						}
					}
				}
			}
		});
		
		//Navegacion con Tecla ENTER
		mainPanel.addAction(new ShortcutListener("EnterPress@"+mainPanel.hashCode(), KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(target instanceof TextField && ((TextField)target).getData() instanceof SearcherField)
					((SearcherField)((TextField)target).getData()).handleEnterPress((TextField)target);//Lanzar evento en los Buscadores
				else if(target == lastComponent && lastComponent.isEnabled() && !lastComponent.isReadOnly() && submitButton != null &&
						(target instanceof TextField || target instanceof PasswordField || target instanceof DateField))
					submitButton.click();
				
				if(isEnterNavigableField(target.getClass())){
					boolean go = true;
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i = targetIdx + 1; i < childComponents.size() && go; i++){
						Component comp = childComponents.get(i);
						if(comp instanceof Button && ((Button)comp).getData() != null && ((Button)comp).getData().equals(SearcherField.ACTIONBUTTON_DATA_PREFIX))
							continue;//No hacer nada cuando se llega a un boton de buscador
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
		navigationEventsApplied = true;
	}
	
	private boolean isArrowUpNavigableField(Class clazz){
		return (isArrowDownNavigableField(clazz) || DateField.class.isAssignableFrom(clazz));
	}
	
	private boolean isArrowDownNavigableField(Class clazz){
		return (Button.class.isAssignableFrom(clazz) || CheckBox.class.isAssignableFrom(clazz) || 
				TextField.class.isAssignableFrom(clazz) || PasswordField.class.isAssignableFrom(clazz) || Upload.class.isAssignableFrom(clazz));
	}
	
	private boolean isEnterNavigableField(Class clazz){
		return (DateField.class.isAssignableFrom(clazz) || AbstractSelect.class.isAssignableFrom(clazz) ||
		Slider.class.isAssignableFrom(clazz) || ProgressBar.class.isAssignableFrom(clazz) ||
		isArrowDownNavigableField(clazz) || OptionGroup.class.isAssignableFrom(clazz));
	}
	
	protected String contextPath(){
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	}
    
    public List<Component> getChilds(){
    	return childComponents;
    }
}
