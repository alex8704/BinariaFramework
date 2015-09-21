package co.com.binariasystems.fmw.vweb.uicomponet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.criteria.MultipleGroupedCriteria;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VCriteria;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VCriteriaUtils;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VRangeCriteria;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VSimpleCriteria;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

public class SearcherField extends VerticalLayout implements ValueChangeListener{
	private HorizontalLayout content;
	private Label captionLabel;
	private TextField textfield;
	private TextField descriptionTxt;
	private Button button;
	private Class<? extends Object> entityClass;
	private Property<Object> propertyDs;
	private Object lastTextFieldValue;
	private SearcherResultWindow resultWindow;
	private Object currentValue;
	private Criteria conditions;
	
	private EntityConfigData masterConfigData;
	private EntityConfigurator configurator;
	private FieldConfigData pkFieldCfg;
	private FieldConfigData searchFieldCfg;
	private EntityCRUDOperationsManager manager;
	private String captiontxt;
	private boolean pkSearch = true;
	private boolean basicJavaType;
	public static final String ACTIONBUTTON_DATA_PREFIX = SearcherField.class.getSimpleName()+"@ActionButton";
	
	
	private SearcherField(){}
	
	public SearcherField(Class<? extends Object> entityClass, String caption){
		super();
		this.entityClass = entityClass;
		this.captiontxt = caption; 
		setWidth(100, Unit.PERCENTAGE);
		//addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		//setSpacing(true);
		try {
			initComponents();
			bindEvents();
		} catch (Exception ex) {
			handleExceptions(ex);
		}
		
	}
	
	private void initComponents() throws Exception{
		configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClass);
		masterConfigData = configurator.configure();
		manager = EntityCRUDOperationsManager.getInstance(entityClass);
		pkFieldCfg = masterConfigData.getFieldsData().get(masterConfigData.getPkFieldName());
		searchFieldCfg = masterConfigData.getFieldsData().get(masterConfigData.getSearchFieldName());
		
		captionLabel = new Label(captiontxt, ContentMode.HTML);
		textfield = new TextField(new ObjectProperty(null, determinePropertyType(masterConfigData.getFieldsData().get(masterConfigData.getSearchFieldName()).getFieldType())));
		descriptionTxt = new TextField(new ObjectProperty<String>(null, String.class));
		button = new Button();
		textfield.setNullRepresentation("");
		textfield.setValidationVisible(false);
		descriptionTxt.setNullRepresentation("");
		descriptionTxt.setValidationVisible(false);
		
		content = new HorizontalLayout();
		content.setWidth(100, Unit.PERCENTAGE);
		textfield.setWidth(120, Unit.PIXELS);
		//textfield.setInvalidCommitted(true);
		textfield.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
		descriptionTxt.setReadOnly(true);
		descriptionTxt.setWidth(100, Unit.PERCENTAGE);
		button.setIcon(FontAwesome.SEARCH);
		content.addComponent(textfield);
		content.addComponent(descriptionTxt);
		content.addComponent(button);
		content.setExpandRatio(descriptionTxt, 1.0f);
		
		addComponent(captionLabel);
		addComponent(content);
		
		resultWindow = new SearcherResultWindow(entityClass);
		
		textfield.setData(this);
		button.setData(ACTIONBUTTON_DATA_PREFIX);
	}
	
	public SearcherField setCaptiontxt(String captiontxt) {
		this.captiontxt = captiontxt;
		captionLabel.setValue(captiontxt);
		return this;
	}
	
	@Override
	public void setCaption(String caption) {
		setCaptiontxt(caption);
	}
	
	@Override
	public void setDescription(String description) {
		textfield.setDescription(description);
	}
	
	private void bindEvents(){
		textfield.addBlurListener(new BlurListener() {
			public void blur(BlurEvent event) {
				//Cuando el Textfiel pierde el foco se verifica si fue borrado el valor, para limpiar el propertyDS
				if(StringUtils.isEmpty(textfield.getValue())){
					if(propertyDs != null){
						try{
							pkSearch = false;
							if(propertyDs != null)
								propertyDs.setValue(null);
						}finally{
							pkSearch = true;
						}
					}
					else{
						lastTextFieldValue = null;
						textfield.setValue(null);
						descriptionTxt.getPropertyDataSource().setValue(null);
						currentValue = null;
					}
						
				}else{
					try{
						refreshDisplayValues();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}	
			}
		});
		
		resultWindow.addCloseListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				pkSearch = false;
				Object value = null;
				currentValue = resultWindow.getSelectedValue();
				try{
					if(currentValue != null && basicJavaType)
						value = PropertyUtils.getProperty(currentValue, pkFieldCfg.getFieldName());
					else if(currentValue != null)
						value = currentValue;
					if(value != null && propertyDs != null)
						propertyDs.setValue(value);
					refreshDisplayValues();
				}catch(Exception ex){
					handleExceptions(ex);
				}finally{
					pkSearch = true;
				}
			}
		});
		
		button.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				resultWindow.openSearch(textfield.getPropertyDataSource().getValue(), true);
			}
		});
	}
	
	public void handleEnterPress(TextField sourceTf) {
		if(sourceTf != textfield)
			return;
		if(textfield.getPropertyDataSource().getValue() != null){
			if(StringUtils.isEmpty(textfield.getValue())) return;
			if(textfield.getPropertyDataSource().getValue() != null && textfield.getPropertyDataSource().getValue().equals(lastTextFieldValue)) return;
			
			descriptionTxt.getPropertyDataSource().setValue(null);
			lastTextFieldValue = textfield.getPropertyDataSource().getValue();
			boolean wasOpened = resultWindow.openSearch(lastTextFieldValue, false);
			if(!wasOpened){
				pkSearch = false;
				Object value = null;
				currentValue = resultWindow.getSelectedValue();
				try{
					if(currentValue != null && basicJavaType)
						value = PropertyUtils.getProperty(currentValue, pkFieldCfg.getFieldName());
					else if(currentValue != null)
						value = currentValue;
					if(propertyDs != null)
						propertyDs.setValue(value);
					refreshDisplayValues();
				}catch(Exception ex){
					handleExceptions(ex);
				}finally{
					pkSearch = true;
				}
			}
		}
	}

	public SearcherField setPropertyDataSource(Property<Object> propertyDs){
		if(this.propertyDs!= null && this.propertyDs.equals(propertyDs))
			return this;
		this.propertyDs = propertyDs;
		if(this.propertyDs != null)
			basicJavaType = TypeHelper.isBasicType(this.propertyDs.getType());
		if(TypeHelper.isNumericType(this.propertyDs.getType()))
			textfield.setConverter(this.propertyDs.getType());
		if(this.propertyDs != null && this.propertyDs instanceof AbstractProperty){
			((AbstractProperty)this.propertyDs).addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					textfield.getPropertyDataSource().setValue(null);
					descriptionTxt.getPropertyDataSource().setValue(null);
					lastTextFieldValue = textfield.getValue();
					if(pkSearch){
						handlePropertyDataSourceChange();
						if(event.getProperty().getValue() != null)
							try {
								refreshDisplayValues();
							} catch (Exception ex) {
								handleExceptions(ex);
							}
					}
				}
			});
		}
		return this;
	}
	
	public SearcherField setRequired(boolean required){
		if(required){
			captionLabel.setValue(captiontxt+"<span aria-hidden=\"true\" class=\"v-required-field-indicator\">*</span>");
			textfield.addValidator(new NullValidator(ValidationUtils.requiredErrorFor(captiontxt), false));
			textfield.setRequiredError(ValidationUtils.requiredErrorFor(captiontxt));
		}
		return this;
	}
	
	public void setReadOnly(boolean readOnly){
		textfield.setReadOnly(readOnly);
		button.setEnabled(!readOnly);
	}
	
	public void setRequiredError(String requiredError){
		textfield.setRequiredError(requiredError);
	}
	
	
	private void handlePropertyDataSourceChange(){
		if(propertyDs.getValue() != null){
			Object paramValue = null;
			try{
				if(basicJavaType)
					paramValue = propertyDs.getValue();
				else
					paramValue = PropertyUtils.getProperty(propertyDs.getValue(), pkFieldCfg.getFieldName());
				resultWindow.openSearchByPk(paramValue);
				currentValue = resultWindow.getSelectedValue();
			}catch(Exception ex){
				handleExceptions(ex);
			}
		}
		
	}
	
	private void refreshDisplayValues() throws Exception{
		if(currentValue != null){
			textfield.getPropertyDataSource().setValue(PropertyUtils.getProperty(currentValue, searchFieldCfg.getFieldName()));
			descriptionTxt.getPropertyDataSource().setValue(FMWEntityUtils.generateStringRepresentationForField(currentValue, FMWConstants.PIPE));
			lastTextFieldValue = textfield.getPropertyDataSource().getValue();
		}else{
			textfield.setValue(lastTextFieldValue != null ? lastTextFieldValue.toString() : null);
			descriptionTxt.getPropertyDataSource().setValue(lastTextFieldValue != null ? descriptionTxt.getPropertyDataSource().getValue() : null);
		}
	}
	
	
	private Class determinePropertyType(Class clazz){
		if(!clazz.isPrimitive()) return clazz;
		Class resp = null;
		if(Byte.TYPE.isAssignableFrom(clazz))
			resp = Byte.class;
		else if(Short.TYPE.isAssignableFrom(clazz))
			resp = Short.class;
		else if(Integer.TYPE.isAssignableFrom(clazz))
			resp = Integer.class;
		else if(Long.TYPE.isAssignableFrom(clazz))
			resp = Long.class;
		else if(Float.TYPE.isAssignableFrom(clazz))
			resp = Float.class;
		else if(Double.TYPE.isAssignableFrom(clazz))
			resp = Double.class;
		else if(Character.TYPE.isAssignableFrom(clazz))
			resp = Character.class;
		else if(Boolean.TYPE.isAssignableFrom(clazz))
			resp = Boolean.class;
		else
			resp = clazz;
		return resp;
	}
	
	public Object getValue(){
		return propertyDs != null ? propertyDs.getValue() : currentValue;
	}
	
	public SearcherField setValue(Object newValue){
		if(propertyDs != null)
			propertyDs.setValue(newValue);
		else{
			currentValue = newValue;
			lastTextFieldValue = null;
			try {
				refreshDisplayValues();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return this;
	}
	
	public SearcherField setCriteria(Criteria newConditios){
		if(conditions != null){
			freeConditionsPropertyListener(conditions);
		}
		
		conditions = newConditios;
		
		if(conditions != null){
			applyConditionsPropertyListener(conditions);
		}
		
		resultWindow.setConditions(VCriteriaUtils.castVCriteria(conditions));
		return this;
	}
	
	private void freeConditionsPropertyListener(Criteria oldCriteria) {
		if(oldCriteria instanceof MultipleGroupedCriteria){
			for(Criteria criteria : ((MultipleGroupedCriteria)oldCriteria).getCriteriaCollection()){
				freeConditionsPropertyListener(criteria);
			}
		}else{
			if(oldCriteria instanceof VCriteria){
				if(oldCriteria instanceof VSimpleCriteria && ((VSimpleCriteria)oldCriteria).getProperty() instanceof ObjectProperty)
					((ObjectProperty)((VSimpleCriteria)oldCriteria).getProperty()).removeValueChangeListener(this);
				if(oldCriteria instanceof VRangeCriteria && ((VRangeCriteria)oldCriteria).getMinProperty() instanceof ObjectProperty && ((VRangeCriteria)oldCriteria).getMaxProperty() instanceof ObjectProperty){
					((ObjectProperty)((VRangeCriteria)oldCriteria).getMinProperty()).removeValueChangeListener(this);
					((ObjectProperty)((VRangeCriteria)oldCriteria).getMaxProperty()).removeValueChangeListener(this);
				}
			}
		}
	}
	
	private void applyConditionsPropertyListener(Criteria newCriteria) {
		if(newCriteria instanceof MultipleGroupedCriteria){
			for(Criteria criteria : ((MultipleGroupedCriteria)newCriteria).getCriteriaCollection()){
				freeConditionsPropertyListener(criteria);
			}
		}else{
			if(newCriteria instanceof VCriteria){
				if(newCriteria instanceof VSimpleCriteria && ((VSimpleCriteria)newCriteria).getProperty() instanceof ObjectProperty)
					((ObjectProperty)((VSimpleCriteria)newCriteria).getProperty()).addValueChangeListener(this);
				if(newCriteria instanceof VRangeCriteria && ((VRangeCriteria)newCriteria).getMinProperty() instanceof ObjectProperty && ((VRangeCriteria)newCriteria).getMaxProperty() instanceof ObjectProperty){
					((ObjectProperty)((VRangeCriteria)newCriteria).getMinProperty()).addValueChangeListener(this);
					((ObjectProperty)((VRangeCriteria)newCriteria).getMaxProperty()).addValueChangeListener(this);
				}
			}
		}
	}
	
	@Override
	public void valueChange(ValueChangeEvent event) {
		if(currentValue != null){
			try{
				resultWindow.setConditions(VCriteriaUtils.castVCriteria(conditions));
				descriptionTxt.getPropertyDataSource().setValue(null);
				textfield.getPropertyDataSource().setValue(null);
				descriptionTxt.getPropertyDataSource().setValue(null);
				lastTextFieldValue = textfield.getValue();
				lastTextFieldValue = textfield.getPropertyDataSource().getValue();
				
				Object paramValue = PropertyUtils.getProperty(currentValue, pkFieldCfg.getFieldName());
				resultWindow.openSearchByPk(paramValue);
				
				pkSearch = false;
				Object value = null;
				currentValue = resultWindow.getSelectedValue();
				
				if(currentValue != null && basicJavaType)
					value = PropertyUtils.getProperty(currentValue, pkFieldCfg.getFieldName());
				else if(currentValue != null)
					value = currentValue;
				if(propertyDs != null)
					propertyDs.setValue(value);
				refreshDisplayValues();
			}catch(Exception ex){
				handleExceptions(ex);
			}finally{
				pkSearch = true;
			}
			
		}
	}

	private void handleExceptions(Throwable ex){
		MessageDialog.showExceptions(ex);
		ex.printStackTrace();
	}
}
