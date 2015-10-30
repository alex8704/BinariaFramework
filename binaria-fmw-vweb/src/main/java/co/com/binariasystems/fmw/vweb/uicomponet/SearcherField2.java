package co.com.binariasystems.fmw.vweb.uicomponet;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class SearcherField2<T> extends CustomField<T>{
	
	private Class<?> entityClazz;
	private Class<T> returnType;
	private Label captionLabel;
	private TextField textfield;
	private TextField descriptionTxt;
	private Button button;
	private VerticalLayout content;
	private HorizontalLayout subcontent;
	
	
	private String caption;
	
	
	private EntityConfigData masterConfigData;
	private EntityConfigurator configurator;
	private EntityCRUDOperationsManager manager;
	
	public SearcherField2(Class<?> entityClazz, Class<T> returnType) {
		this.entityClazz = entityClazz;
		this.returnType = returnType;
	}

	@Override
	protected Component initContent() {
		try{
			initEntityConfig();
			captionLabel = new Label(caption, ContentMode.HTML);
			textfield = new TextField(new ObjectProperty<Object>(null, (Class<Object>)masterConfigData.getSearchFieldData().getFieldType()));
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
			
			content.addComponent(captionLabel);
			
			content = new VerticalLayout();
			content.setWidth(100, Unit.PERCENTAGE);
			content.addComponent(subcontent);
			bindEvents();
		}catch(FMWException ex){
			MessageDialog.showExceptions(ex);
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
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				if(textfield.getPropertyDataSource().equals(event.getProperty()))
					textfieldValueChange();
			}
		};
		addValueChangeListener(valueChangeListener);
		textfield.addValueChangeListener(valueChangeListener);
	}

	protected void textfieldValueChange() {
		
	}

	@Override
	public Class<T> getType() {
		return returnType;
	}

}
