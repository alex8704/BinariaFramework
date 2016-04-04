package co.com.binariasystems.fmw.vweb.uicomponet;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.criteria.MultipleGroupedCriteria;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.SearchSelectionChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.SearchSelectionChangeListener;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.SearchType;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VCriteria;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VCriteriaUtils;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VRangeCriteria;
import co.com.binariasystems.fmw.vweb.uicomponet.searcher.VSimpleCriteria;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;


public class SearcherField<T> extends CustomField<T> implements SearchSelectionChangeListener<Object>{
	private static final Logger LOGGER = LoggerFactory.getLogger(SearcherField.class);
	private Class<?> entityClazz;
	private Class<T> returnType;
	private TextField textfield;
	private TextField descriptionTxt;
	private Button button;
	private HorizontalLayout content;
	private SearcherResultWindow<Object> searchWindow;
	
	private ObjectProperty<Object> textfieldProperty;
	private ObjectProperty<String> descriptionProperty;
	
	private EntityConfigData<?> masterConfigData;
	
	private Criteria conditions;
	private ValueChangeListener conditionsChangeListener;
	private ValueChangeListener valueChangeListener;
	
	private boolean omitSearchCall;
	private boolean initialized;
	private boolean readOnly;
	
	public SearcherField(Class<?> entityClazz) {
		this(entityClazz, null);
	}
	
	public SearcherField(Class<?> entityClazz, String caption) {
		this(entityClazz, (Class<T>) entityClazz, caption);
	}
	
	public SearcherField(Class<?> entityClazz, Class<T> returnType, String caption) {
		this.entityClazz = entityClazz;
		this.returnType = returnType;
		if(caption != null)
			setCaption(caption);
		
	}

	
	@Override
	protected Component initContent() {
		initComponents();
		return content;
	}
	
	@SuppressWarnings("unchecked")
	private void initComponents(){
		try{
			initEntityConfig();
			textfieldProperty = new ObjectProperty<Object>(null, (Class<Object>)masterConfigData.getSearchFieldData().getFieldType());
			descriptionProperty = new ObjectProperty<String>(null, String.class);
			textfield = new TextField(textfieldProperty);
			descriptionTxt = new TextField(descriptionProperty);
			button = new Button();
			textfield.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
			textfield.setNullRepresentation("");
			textfield.setValidationVisible(false);
			textfield.setImmediate(true);
			descriptionTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
			descriptionTxt.setNullRepresentation("");
			descriptionTxt.setValidationVisible(false);
			
			content = new HorizontalLayout();
			content.setWidth(100, Unit.PERCENTAGE);
			textfield.setWidth(120, Unit.PIXELS);
			textfield.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			descriptionTxt.setReadOnly(true);
			descriptionTxt.setWidth(100, Unit.PERCENTAGE);
			button.setIcon(FontAwesome.SEARCH);
			content.addComponent(textfield);
			content.addComponent(descriptionTxt);
			content.addComponent(button);
			content.setExpandRatio(descriptionTxt, 1.0f);
			
			textfield.setReadOnly(readOnly);
			button.setEnabled(!readOnly);
			
			searchWindow = new SearcherResultWindow<Object>((Class<Object>) entityClazz);
			initialized = true;//Debe estar en este lugar
			setInvalidCommitted(true);
			bindEvents();
			fireFirstTimeValueChange();
		}catch(FMWException ex){
			MessageDialog.showExceptions(ex, LOGGER);
		}
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if(initialized){
			textfield.setReadOnly(readOnly);
			button.setEnabled(!readOnly);
		}
	}
	
	private void initEntityConfig() throws FMWException{
		masterConfigData = EntityConfigurationManager.getInstance().getConfigurator(entityClazz).configure();
	}
	
	private void bindEvents(){
		valueChangeListener = new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if(textfield.getPropertyDataSource().equals(event.getProperty()))
					onTextfieldValueChange(event);
				else if(event.getProperty().equals(SearcherField.this.getPropertyDataSource()))
					onInternalValueChange(event);
			}
		};
		
		conditionsChangeListener = new ValueChangeListener() {
			@Override public void valueChange(Property.ValueChangeEvent event) {
				onConditionsChange();
			}
		};
		
		ClickListener buttonClickListener = new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {onButtonClick();}
		};
		
		addValueChangeListener(valueChangeListener);
		textfieldProperty.addValueChangeListener(valueChangeListener);
		button.addClickListener(buttonClickListener);
		searchWindow.setSelectionChangeListener(this);
		applyConditionsPropertyListener(conditions);
		searchWindow.setConditions(VCriteriaUtils.castVCriteria(conditions));
		if(getPropertyDataSource() instanceof AbstractProperty)
			((AbstractProperty)getPropertyDataSource()).addValueChangeListener(valueChangeListener);
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
		return entityClazz.equals(returnType) ? extractFieldValue(originalValue, masterConfigData.getPkFieldData()) : originalValue;
	}
	
	@SuppressWarnings("unchecked")
	private T extractReturnValue(Object originalValue){
		return entityClazz.equals(returnType) ? (T)originalValue : (T)extractFieldValue(originalValue, masterConfigData.getPkFieldData());
	}
	
	private Object extractFieldValue(Object originalValue, FieldConfigData fieldInfo){
		if(originalValue == null) return null;
		try {
			return PropertyUtils.getProperty(originalValue, fieldInfo.getFieldName());
		} catch (ReflectiveOperationException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}
		return null;	
	}
	
	/**
	 * Metodo util para mostrar el valor actual del componente
	 * al vincularlo por primera vez a la sesion del usuario
	 * Corrige Bug de  CustomFields que no son inicializados hasta que
	 * son atados (attach) a la sesion.
	 */
	private void fireFirstTimeValueChange(){
		if(getPropertyDataSource() != null && getPropertyDataSource().getValue() != null)
			selectionChange(new SearchSelectionChangeEvent(null, getPropertyDataSource().getValue(), SearchType.PK, false));
	}

	@Override
	public void selectionChange(SearchSelectionChangeEvent<Object> event) {
		omitSearchCall = true;
		String description = "";
		Object newValue = event.getNewValue();
		try{
			if(event.getSearchType().equals(SearchType.PK))
				textfieldProperty.setValue(event.isReset() ? null : extractFieldValue(newValue, masterConfigData.getSearchFieldData()));
			else {
				newValue = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				textfieldProperty.setValue(event.isReset() ? null : extractFieldValue(newValue, masterConfigData.getSearchFieldData()));
			}
			setValue(event.isReset() ? null : extractReturnValue(newValue));
			description = FMWEntityUtils.generateStringRepresentationForField(newValue, FMWConstants.PIPE);
		} catch (FMWException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}finally{
			omitSearchCall = false;
			descriptionProperty.setValue(description);
		}
	}
	
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
		if(newDataSource instanceof AbstractProperty && initialized)
			((AbstractProperty)newDataSource).addValueChangeListener(valueChangeListener);
	}
	
	
	public SearcherField<T> setCriteria(Criteria newConditios){
		freeConditionsPropertyListener(conditions);
		conditions = newConditios;
		applyConditionsPropertyListener(conditions);
		if(initialized)
			searchWindow.setConditions(VCriteriaUtils.castVCriteria(conditions));
		return this;
	}
	
	private void freeConditionsPropertyListener(Criteria oldCriteria) {
		if(oldCriteria == null || !initialized) return;
		if(oldCriteria instanceof MultipleGroupedCriteria){
			for(Criteria criteria : ((MultipleGroupedCriteria)oldCriteria).getCriteriaCollection())
				freeConditionsPropertyListener(criteria);
		}else{
			if(oldCriteria instanceof VCriteria){
				if(oldCriteria instanceof VSimpleCriteria && ((VSimpleCriteria<?>)oldCriteria).getProperty() instanceof AbstractProperty)
					((AbstractProperty<?>)((VSimpleCriteria<?>)oldCriteria).getProperty()).removeValueChangeListener(conditionsChangeListener);
				if(oldCriteria instanceof VRangeCriteria && ((VRangeCriteria<?>)oldCriteria).getMinProperty() instanceof AbstractProperty && ((VRangeCriteria<?>)oldCriteria).getMaxProperty() instanceof AbstractProperty){
					((AbstractProperty<?>)((VRangeCriteria<?>)oldCriteria).getMinProperty()).removeValueChangeListener(conditionsChangeListener);
					((AbstractProperty<?>)((VRangeCriteria<?>)oldCriteria).getMaxProperty()).removeValueChangeListener(conditionsChangeListener);
				}
			}
		}
	}
	
	private void applyConditionsPropertyListener(Criteria newCriteria) {
		if(newCriteria == null || !initialized) return;
		if(newCriteria instanceof MultipleGroupedCriteria){
			for(Criteria criteria : ((MultipleGroupedCriteria)newCriteria).getCriteriaCollection())
				applyConditionsPropertyListener(criteria);
		}else{
			if(newCriteria instanceof VCriteria){
				if(newCriteria instanceof VSimpleCriteria && ((VSimpleCriteria<?>)newCriteria).getProperty() instanceof AbstractProperty)
					((AbstractProperty<?>)((VSimpleCriteria<?>)newCriteria).getProperty()).addValueChangeListener(conditionsChangeListener);
				if(newCriteria instanceof VRangeCriteria && ((VRangeCriteria<?>)newCriteria).getMinProperty() instanceof AbstractProperty && ((VRangeCriteria<?>)newCriteria).getMaxProperty() instanceof AbstractProperty){
					((AbstractProperty<?>)((VRangeCriteria<?>)newCriteria).getMinProperty()).addValueChangeListener(conditionsChangeListener);
					((AbstractProperty<?>)((VRangeCriteria<?>)newCriteria).getMaxProperty()).addValueChangeListener(conditionsChangeListener);
				}
			}
		}
	}
	
	private void onConditionsChange(){
		searchWindow.setConditions(VCriteriaUtils.castVCriteria(conditions));
		if(getValue() != null)
			searchWindow.search(extractPKValue(getValue()), SearchType.PK);
		
	}

}
