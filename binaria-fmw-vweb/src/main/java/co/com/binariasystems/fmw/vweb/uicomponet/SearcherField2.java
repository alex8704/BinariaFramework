package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow2.SearchSelectionChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow2.SearchSelectionChangeListener;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow2.SearchType;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class SearcherField2<T> extends CustomField<T> implements SearchSelectionChangeListener<Object>{
	private static final Logger LOGGER = LoggerFactory.getLogger(SearcherField2.class);
	private Class<?> entityClazz;
	private Class<T> returnType;
	private Label captionLabel;
	private TextField textfield;
	private TextField descriptionTxt;
	private Button button;
	private VerticalLayout content;
	private HorizontalLayout subcontent;
	private SearcherResultWindow2<Object> searchWindow;
	
	private String caption;
	private ObjectProperty<Object> textfieldProperty;
	
	private EntityConfigData masterConfigData;
	private EntityConfigurator configurator;
	private EntityCRUDOperationsManager manager;
	
	private boolean omitSearchCall;
	
	public SearcherField2(Class<?> entityClazz) {
		this.entityClazz = entityClazz;
		this.returnType = (Class<T>) entityClazz;
	}
	
	public SearcherField2(Class<?> entityClazz, Class<T> returnType) {
		this.entityClazz = entityClazz;
		this.returnType = returnType;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Component initContent() {
		try{
			initEntityConfig();
			textfieldProperty = new ObjectProperty<Object>(null, (Class<Object>)masterConfigData.getSearchFieldData().getFieldType());
			captionLabel = new Label(caption, ContentMode.HTML);
			textfield = new TextField(textfieldProperty);
			descriptionTxt = new TextField(new ObjectProperty<String>(null, String.class));
			button = new Button();
			textfield.setNullRepresentation("");
			textfield.setValidationVisible(false);
			textfield.setImmediate(true);
			descriptionTxt.setNullRepresentation("");
			descriptionTxt.setValidationVisible(false);
			
			subcontent = new HorizontalLayout();
			subcontent.setWidth(100, Unit.PERCENTAGE);
			textfield.setWidth(120, Unit.PIXELS);
			textfield.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			descriptionTxt.setReadOnly(true);
			descriptionTxt.setWidth(100, Unit.PERCENTAGE);
			button.setIcon(FontAwesome.SEARCH);
			subcontent.addComponent(textfield);
			subcontent.addComponent(descriptionTxt);
			subcontent.addComponent(button);
			subcontent.setExpandRatio(descriptionTxt, 1.0f);
			
			searchWindow = new SearcherResultWindow2<Object>((Class<Object>) entityClazz);
			content = new VerticalLayout();
			content.setWidth(100, Unit.PERCENTAGE);
			content.addComponent(captionLabel);
			content.addComponent(subcontent);
			bindEvents();
		}catch(FMWException ex){
			MessageDialog.showExceptions(ex, LOGGER);
		}
		return content;
	}
	
	
	private void initEntityConfig() throws FMWException{
		configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClazz);
		masterConfigData = configurator.configure();
		manager = EntityCRUDOperationsManager.getInstance(entityClazz);
	}
	
	private void bindEvents(){
		ValueChangeListener valueChangeListener = new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if(textfield.getPropertyDataSource().equals(event.getProperty()))
					onTextfieldValueChange(event);
				else if(event.getProperty().equals(SearcherField2.this.getPropertyDataSource()))
					onInternalValueChange(event);
			}
		};
		
		ClickListener buttonClickListener = new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {onButtonClick();}
		};
		
		addValueChangeListener(valueChangeListener);
		textfieldProperty.addValueChangeListener(valueChangeListener);
		button.addClickListener(buttonClickListener);
	}
	
	private void onTextfieldValueChange(Property.ValueChangeEvent event) {
		if(!omitSearchCall)
			searchWindow.search(event.getProperty().getValue(), SearchType.FILTER);
	}
	
	private void onInternalValueChange(Property.ValueChangeEvent event) {
		if(!omitSearchCall)
			searchWindow.search(extractPKValue(event.getProperty().getValue()), SearchType.PK);
	}
	
	private void onButtonClick(){
		searchWindow.search(textfieldProperty.getValue(), SearchType.BUTTON);
	}

	@Override
	public Class<T> getType() {
		return returnType;
	}
	
	private Object extractPKValue(Object originalValue){
		try {
			return entityClazz.equals(returnType) ? PropertyUtils.getProperty(originalValue, masterConfigData.getPkFieldName()) : originalValue;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}
		return originalValue;	
	}
	
	@SuppressWarnings("unchecked")
	private T extractReturnValue(Object originalValue){
		try {
			return entityClazz.equals(returnType) ? (T)originalValue : (T)PropertyUtils.getProperty(originalValue, masterConfigData.getPkFieldName());
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}
		return null;	
	}

	@Override
	public void selectionChange(SearchSelectionChangeEvent<Object> event) {
		omitSearchCall = true;
		String description = "";
		try{
			if(event.getSearchType().equals(SearchType.PK) && event.getNewValue() != null){
				textfieldProperty.setValue(PropertyUtils.getProperty(event.getNewValue(), masterConfigData.getSearchFieldName()));
				description = FMWEntityUtils.generateStringRepresentationForField(event.getNewValue(), FMWConstants.PIPE);
			}else {
				Object newValue = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				textfieldProperty.setValue(PropertyUtils.getProperty(newValue, masterConfigData.getSearchFieldName()));
				setValue(extractReturnValue(newValue));
				description = FMWEntityUtils.generateStringRepresentationForField(event.getNewValue(), FMWConstants.PIPE);
			}
			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | FMWException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}finally{
			omitSearchCall = false;
			descriptionTxt.setValue(description);
		}
	}

}
