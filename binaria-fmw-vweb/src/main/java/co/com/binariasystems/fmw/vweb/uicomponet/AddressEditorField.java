package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.util.ObjectUtils;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.addresseditor.AddressEditorParametersProvider;
import co.com.binariasystems.fmw.vweb.uicomponet.addresseditor.BuiltInAddressEditorParametersProvider;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;


/**
 * Generador de direcciones parametrizadas, basadas en la norma establecida por la
 * Direccion Impuestos y Aduana Nacional (DIAN), permite total parametrizacion e
 * internacionalizacion en base a la configuracion regional del usuario final, soporte
 * inicial para ingles (USA) y español latinoamerica
 * 
 * @author Alexander Castro O.
 *
 */
public class AddressEditorField<T> extends CustomField<T> implements UIConstants, VWebCommonConstants{
	private static final Logger LOGGER = LoggerFactory.getLogger(AddressEditorField.class);
	private GridLayout content;
	private HorizontalLayout generatorPanel;
	private Label mainViaLbl;
	private NativeSelect mainViaTypeCmb;
	private TextField mainViaNumTxt;
	private TextField mainViaLetterTxt;
	private NativeSelect mainViaBisCmb;
	private TextField mainViaBisLetterTxt;
	private NativeSelect mainViaQuadrantCmb;
	private NativeSelect mainViaComplementCmb;
	private TextField mainViaComplementTxt;
	
	private Label secondaryViaLbl;
	private TextField secondaryViaNumTxt;
	private TextField secondaryViaLetterTxt;
	
	private Label complementaryViaLbl;
	private TextField complementaryViaNumTxt;
	private NativeSelect complementaryViaQuadrantCmb;
	private NativeSelect complementaryViaComplementCmb;
	private TextField complementaryViaComplementTxt;
	
	private TextField generatedAddressTxt;
	
	private AddressEditorParametersProvider parametersProvider;
	private Class<T> valueType;
	
	private BeanItem<T> beanItem;
	private ObjectProperty<String> generatedAddressProperty = new ObjectProperty<String>("", String.class);
	private ValueChangeListener valueChangeListener;
	private boolean ommitDsUpdate;
	private AddressFieldsToDTOMappingInfo fieldsToPropertyMapping = new AddressFieldsToDTOMappingInfo();
	
	public AddressEditorField(Class<T> valueType) {
		this(valueType, null, null);
	}
	
	public AddressEditorField(Class<T> valueType, String caption) {
		this(valueType, null, null);
	}
	
	public AddressEditorField(Class<T> valueType, AddressFieldsToDTOMappingInfo fieldsToPropertyMapping) {
		this(valueType, fieldsToPropertyMapping, null);
	}
	
	public AddressEditorField(Class<T> valueType, AddressFieldsToDTOMappingInfo fieldsToPropertyMapping, String caption) {
		this.setCaption(caption);
		this.addStyleName(ADDRESS_EDITOR_STYLENAME);
		this.valueType = valueType;
		if(fieldsToPropertyMapping != null)
			this.fieldsToPropertyMapping = fieldsToPropertyMapping;
		initBeanItem();
		initComponents();
	}
	
	private void initBeanItem(){
		try{
			beanItem = new BeanItem<T>(valueType.newInstance(), valueType);
		}catch(ReflectiveOperationException ex){
			throw new RuntimeException("Has aocurred an unespexted error initializing Address Editor", ex);
		}
	}


	@Override
	protected Component initContent() {
		loadParameters();
		return content;
	}
	
	private void initComponents(){
		content = new  GridLayout(3,2);
		generatorPanel = new HorizontalLayout();
		generatedAddressTxt = new TextField(VWebUtils.getCommonString(ADDRESS_GENERATED_KEY));
		
		buildMainViaPanel();
		buildSecondaryAndComplementaryPanel();
		
		mainViaNumTxt.setMaxLength(3);
		mainViaLetterTxt.setMaxLength(3);
		mainViaBisLetterTxt.setMaxLength(3);
		mainViaComplementTxt.setMaxLength(40);
		secondaryViaNumTxt.setMaxLength(3);
		secondaryViaLetterTxt.setMaxLength(3);
		complementaryViaNumTxt.setMaxLength(3);
		complementaryViaComplementTxt.setMaxLength(40);
		
		mainViaNumTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		mainViaLetterTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		mainViaBisLetterTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		secondaryViaNumTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		secondaryViaLetterTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		complementaryViaNumTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		
		mainViaLetterTxt.addStyleName(UPPER_TRANSFORM_STYLENAME);
		mainViaBisLetterTxt.addStyleName(UPPER_TRANSFORM_STYLENAME);
		secondaryViaLetterTxt.addStyleName(UPPER_TRANSFORM_STYLENAME);
		mainViaComplementTxt.addStyleName(UPPER_TRANSFORM_STYLENAME);
		complementaryViaComplementTxt.addStyleName(UPPER_TRANSFORM_STYLENAME);
		
		mainViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		secondaryViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		complementaryViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		
		generatedAddressTxt.setReadOnly(true);
		generatedAddressTxt.setWidth(100, Unit.PERCENTAGE);
		
		content.setWidth(100, Unit.PERCENTAGE);
		
		content.addComponent(new Label("<span>&nbsp;</span>", ContentMode.HTML), 0, 0, 0, 1);
		content.addComponent(generatorPanel, 1, 0, 1, 0);
		content.addComponent(generatedAddressTxt, 1, 1, 1, 1);
		content.addComponent(new Label("<span>&nbsp;</span>", ContentMode.HTML), 2, 0, 2, 1);
		
		content.setColumnExpandRatio(0, 1.0f);
		content.setColumnExpandRatio(2, 1.0f);
		content.setSpacing(true);
		content.addStyleName(ADDRESS_EDITOR_CONTENT_STYLENAME);
		mainViaNumTxt.setNullRepresentation("");
		mainViaLetterTxt.setNullRepresentation("");
		mainViaBisLetterTxt.setNullRepresentation("");
		mainViaComplementTxt.setNullRepresentation("");
		secondaryViaNumTxt.setNullRepresentation("");
		secondaryViaLetterTxt.setNullRepresentation("");
		complementaryViaNumTxt.setNullRepresentation("");
		complementaryViaComplementTxt.setNullRepresentation("");
		bindEvents();
	}
	
	
	private void buildMainViaPanel(){
		GridLayout mainViaPanel = new GridLayout(2,4);
		HorizontalLayout horizontalRow1 = new HorizontalLayout();
		HorizontalLayout horizontalRow2 = new HorizontalLayout();
		mainViaLbl = new Label(VWebUtils.getCommonString(ADDRESS_MAIN_VIA_KEY));
		mainViaTypeCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_VIA_TYPE_KEY));
		mainViaNumTxt = new TextField(VWebUtils.getCommonString(ADDRESS_NUMBER_KEY));
		mainViaLetterTxt = new TextField(VWebUtils.getCommonString(ADDRESS_LETTERS_KEY));
		mainViaBisCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_BIS_KEY));
		mainViaBisLetterTxt = new TextField(VWebUtils.getCommonString(ADDRESS_LETTERS_KEY));
		mainViaQuadrantCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_QUADRANT_KEY));
		mainViaComplementCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_COMPLEMENT_KEY));
		mainViaComplementTxt = new TextField("");
		Label filler1 = new Label("<span>&nbsp;</span>",ContentMode.HTML);
		Label numberSymbolSeparatorLbl = new Label("<span>#</span>",ContentMode.HTML);
		
		horizontalRow1.addComponents(mainViaTypeCmb, 
				mainViaNumTxt,
				mainViaLetterTxt,
				mainViaBisCmb,
				mainViaBisLetterTxt,
				mainViaQuadrantCmb);
		
		horizontalRow2.addComponents(mainViaComplementCmb,
				mainViaComplementTxt);
		
		mainViaLbl.setWidth(100, Unit.PERCENTAGE);
		mainViaLbl.addStyleName(CENTER_ALIGN_STYLE);
		
		mainViaTypeCmb.setWidth(100, Unit.PIXELS);
		mainViaNumTxt.setWidth(50, Unit.PIXELS);
		mainViaLetterTxt.setWidth(50, Unit.PIXELS);
		mainViaBisCmb.setWidth(50, Unit.PIXELS);
		mainViaBisLetterTxt.setWidth(50, Unit.PIXELS);
		mainViaQuadrantCmb.setWidth(100, Unit.PIXELS);
		mainViaComplementCmb.setWidth(100, Unit.PIXELS);
		mainViaComplementTxt.setWidth(100, Unit.PERCENTAGE);
		filler1.setWidth(20, Unit.PIXELS);
		numberSymbolSeparatorLbl.setWidth(20, Unit.PIXELS);
		numberSymbolSeparatorLbl.addStyleName(CENTER_ALIGN_STYLE);
		
		
		mainViaPanel.addComponents(mainViaLbl, filler1);
		mainViaPanel.addComponents(new Label("<hr/>", ContentMode.HTML), new Label("<span>&nbsp</span>", ContentMode.HTML));
		mainViaPanel.addComponents(horizontalRow1, numberSymbolSeparatorLbl);
		mainViaPanel.addComponent(horizontalRow2, 0, 3, 1, 3);
		mainViaPanel.setComponentAlignment(mainViaLbl, Alignment.TOP_CENTER);
		mainViaPanel.setComponentAlignment(numberSymbolSeparatorLbl, Alignment.BOTTOM_CENTER);
		
		horizontalRow1.setSpacing(true);
		horizontalRow2.setSpacing(true);
		
		generatorPanel.addComponent(mainViaPanel);
	}
	
	private void buildSecondaryAndComplementaryPanel(){
		GridLayout layout = new GridLayout(3,4);
		HorizontalLayout auxHLayout1 = new HorizontalLayout();
		HorizontalLayout auxHLayout2 = new HorizontalLayout();
		HorizontalLayout auxHLayout3 = new HorizontalLayout();
		
		secondaryViaLbl = new Label(VWebUtils.getCommonString(ADDRESS_SECONDARY_VIA_KEY));
		secondaryViaNumTxt = new TextField(VWebUtils.getCommonString(ADDRESS_NUMBER_KEY));
		secondaryViaLetterTxt = new TextField(VWebUtils.getCommonString(ADDRESS_LETTERS_KEY));
		
		complementaryViaLbl = new Label(VWebUtils.getCommonString(ADDRESS_COMPLEMENTARY_VIA_KEY));
		complementaryViaNumTxt = new TextField(VWebUtils.getCommonString(ADDRESS_NUMBER_KEY));
		complementaryViaQuadrantCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_QUADRANT_KEY));
		complementaryViaComplementCmb = new NativeSelect(VWebUtils.getCommonString(ADDRESS_COMPLEMENT_KEY));
		complementaryViaComplementTxt = new TextField("");
		Label filler1 = new Label("<span>&nbsp;</span>", ContentMode.HTML);
		Label dashSymbolSeparatorLbl = new Label("<span>-</span>", ContentMode.HTML);
		
		auxHLayout1.addComponents(secondaryViaNumTxt, secondaryViaLetterTxt);
		auxHLayout2.addComponents(complementaryViaNumTxt, complementaryViaQuadrantCmb);
		auxHLayout3.addComponents(complementaryViaComplementCmb, complementaryViaComplementTxt);
		
		layout.addComponents(secondaryViaLbl, filler1, complementaryViaLbl);
		layout.addComponents(new Label("<hr/>", ContentMode.HTML), new Label("<span>&nbsp;</span>", ContentMode.HTML), new Label("<hr/>", ContentMode.HTML) );
		layout.addComponents(auxHLayout1, dashSymbolSeparatorLbl, auxHLayout2);
		layout.addComponent(auxHLayout3, 0, 3, 2, 3);
		
		secondaryViaLbl.setWidth(100, Unit.PERCENTAGE);
		complementaryViaLbl.setWidth(100, Unit.PERCENTAGE);
		secondaryViaLbl.addStyleName(CENTER_ALIGN_STYLE);
		complementaryViaLbl.addStyleName(CENTER_ALIGN_STYLE);
		filler1.setWidth(20, Unit.PIXELS);
		dashSymbolSeparatorLbl.setWidth(20, Unit.PIXELS);
		dashSymbolSeparatorLbl.addStyleName(CENTER_ALIGN_STYLE);
		
		layout.setComponentAlignment(secondaryViaLbl, Alignment.TOP_CENTER);
		layout.setComponentAlignment(complementaryViaLbl, Alignment.TOP_CENTER);
		
		secondaryViaNumTxt.setWidth(50, Unit.PIXELS);
		secondaryViaLetterTxt.setWidth(50, Unit.PIXELS);
		complementaryViaNumTxt.setWidth(50, Unit.PIXELS);
		complementaryViaQuadrantCmb.setWidth(100, Unit.PIXELS);
		complementaryViaComplementCmb.setWidth(100, Unit.PIXELS);
		complementaryViaComplementTxt.setWidth(100, Unit.PERCENTAGE);
		
		layout.setComponentAlignment(dashSymbolSeparatorLbl, Alignment.BOTTOM_CENTER);
		 
		auxHLayout1.setSpacing(true);
		auxHLayout2.setSpacing(true);
		auxHLayout3.setSpacing(true);
		
		generatorPanel.addComponent(layout);
	}
	
	private void loadParameters(){
		if(parametersProvider == null)
			setParametersProvider(new BuiltInAddressEditorParametersProvider());
		
		setComboBoxItems(mainViaTypeCmb, parametersProvider.getViaTypes(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaBisCmb, parametersProvider.getBis(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaQuadrantCmb, parametersProvider.getQuadrants(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(complementaryViaQuadrantCmb, parametersProvider.getQuadrants(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaComplementCmb, parametersProvider.getNomenclatureComplements(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(complementaryViaComplementCmb, parametersProvider.getNomenclatureComplements(VWebUtils.getCurrentUserLocale()));
	}
	
	private void setComboBoxItems(NativeSelect comboBox, List<? extends Listable> items){
		comboBox.setNullSelectionItemId("");
		comboBox.setItemCaption("", VWebUtils.getComboBoxNoSelectionShortDescription());
		for(Listable item : items){
			comboBox.addItem(item.getPK());
			comboBox.setItemCaption(item.getPK(), item.getDescription());
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void bindEvents(){
		mainViaTypeCmb.setImmediate(true);
		mainViaNumTxt.setImmediate(true);
		mainViaLetterTxt.setImmediate(true);
		mainViaBisCmb.setImmediate(true);
		mainViaBisLetterTxt.setImmediate(true);
		mainViaQuadrantCmb.setImmediate(true);
		mainViaComplementCmb.setImmediate(true);
		mainViaComplementTxt.setImmediate(true);
		secondaryViaNumTxt.setImmediate(true);
		secondaryViaLetterTxt.setImmediate(true);
		complementaryViaNumTxt.setImmediate(true);
		complementaryViaQuadrantCmb.setImmediate(true);
		complementaryViaComplementCmb.setImmediate(true);
		complementaryViaComplementTxt.setImmediate(true);
		
		mainViaTypeCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaTypeProperty()));
		mainViaNumTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaNumProperty()));
		mainViaLetterTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaLetterProperty()));
		mainViaBisCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaBisProperty()));
		mainViaBisLetterTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaBisLetterProperty()));
		mainViaQuadrantCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaQuadrantProperty()));
		mainViaComplementCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaComplementProperty()));
		mainViaComplementTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getMainViaComplementDetailProperty()));
		secondaryViaNumTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getSecondaryViaNumProperty()));
		secondaryViaLetterTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getSecondaryViaLetterProperty()));
		complementaryViaNumTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getComplementaryViaNumProperty()));
		complementaryViaQuadrantCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getComplementaryViaQuadrantProperty()));
		complementaryViaComplementCmb.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getComplementaryViaComplementProperty()));
		complementaryViaComplementTxt.setPropertyDataSource(beanItem.getItemProperty(fieldsToPropertyMapping.getComplementaryViaComplementDetailProperty()));
		generatedAddressTxt.setPropertyDataSource(generatedAddressProperty);
		
		valueChangeListener = new Property.ValueChangeListener() {
			@Override public void valueChange(Property.ValueChangeEvent event) {
				if(event.getProperty().equals(AddressEditorField.this.getPropertyDataSource()) || event.getProperty().equals(AddressEditorField.this))
					handleAdrressEditorPropertyValueChange(event);
				else
					handleControlsPropertyValueChange(event);
			}
		};
		
		
		for(String propertyId : getFieldsToPropertyMapping().getPropertyNames()){
			if(beanItem.getItemProperty(propertyId) instanceof AbstractProperty)
				((AbstractProperty)beanItem.getItemProperty(propertyId)).addValueChangeListener(valueChangeListener);
		}
		addValueChangeListener(valueChangeListener);
	}
	
	@SuppressWarnings("unchecked")
	private void handleAdrressEditorPropertyValueChange(Property.ValueChangeEvent event){
		Object sourceBean = event.getProperty().getValue();
		try {
			ommitDsUpdate = true;
			for(String propertyId : getFieldsToPropertyMapping().getPropertyNames()){
				beanItem.getItemProperty(propertyId).setValue(sourceBean == null ? null : PropertyUtils.getProperty(sourceBean, propertyId));
			}
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Cannot handle value change on AddressEditor: "+e.getMessage());
		}finally{
			ommitDsUpdate = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void handleControlsPropertyValueChange(Property.ValueChangeEvent event){
		generatedAddressProperty.setValue(getAddrressString());
		if(!ommitDsUpdate && this.getPropertyDataSource() != null){
			if(this.getPropertyDataSource().getValue() != null)
				ObjectUtils.transferProperties(beanItem.getBean(), this.getPropertyDataSource().getValue());
			else
				this.getPropertyDataSource().setValue(ObjectUtils.transferProperties(beanItem.getBean(), valueType));
		}
	}

	@Override
	public Class<T> getType() {
		return valueType;
	}
	
	public AddressEditorParametersProvider getParametersProvider() {
		return parametersProvider;
	}

	public void setParametersProvider(AddressEditorParametersProvider parametersProvider) {
		this.parametersProvider = parametersProvider;
	}
	
	public AddressFieldsToDTOMappingInfo getFieldsToPropertyMapping() {
		return fieldsToPropertyMapping;
	}

	public void setFieldsToPropertyMapping(AddressFieldsToDTOMappingInfo fieldsToPropertyMapping) {
		this.fieldsToPropertyMapping = fieldsToPropertyMapping;
	}

	public String getAddrressString() {
		StringBuilder stringBuilder = new StringBuilder();
		for(String propName : getFieldsToPropertyMapping().getPropertyNames()){
			Object propValue = getBeanItemValue(propName);
			String stringValue = propValue != null ? propValue.toString() : "";
			stringBuilder.append(stringBuilder.length() == 0 ? "" : (StringUtils.isEmpty(stringValue) ? "" : FMWConstants.WHITE_SPACE));
			stringBuilder.append(stringValue);
		}
		return stringBuilder.toString().trim();
	}
	
	private Object getBeanItemValue(String propertyId){
		Property<?> prop = beanItem.getItemProperty(propertyId);
		Object resp = prop.getValue();
		if(resp != null && String.class.isAssignableFrom(prop.getType()))
			resp = StringUtils.upperCase((String)resp).trim();
		return resp;
	}
	
	public static class AddressFieldsToDTOMappingInfo{
		private String 		mainViaTypeProperty = "mainViaType", 
							mainViaNumProperty = "mainViaNum", 
							mainViaLetterProperty = "mainViaLetter", 
							mainViaBisProperty = "mainViaBis", 
							mainViaBisLetterProperty = "mainViaBisLetter", 
							mainViaQuadrantProperty = "mainViaQuadrant",
							mainViaComplementProperty = "mainViaComplement", 
							mainViaComplementDetailProperty = "mainViaComplementDetail", 
							secondaryViaNumProperty = "secondaryViaNum", 
							secondaryViaLetterProperty = "secondaryViaLetter",
							complementaryViaNumProperty = "complementaryViaNum", 
							complementaryViaQuadrantProperty = "complementaryViaQuadrant", 
							complementaryViaComplementProperty = "complementaryViaComplement", 
							complementaryViaComplementDetailProperty = "complementaryViaComplementDetail";
		
		public AddressFieldsToDTOMappingInfo(){}

		public AddressFieldsToDTOMappingInfo(String mainViaTypeProperty, 
				String mainViaNumProperty, 
				String mainViaLetterProperty, 
				String mainViaBisProperty, 
				String mainViaBisLetterProperty, 
				String mainViaQuadrantProperty, 
				String mainViaComplementProperty, 
				String mainViaComplementDetailProperty, 
				String secondaryViaNumProperty, 
				String secondaryViaLetterProperty, 
				String complementaryViaNumProperty, 
				String complementaryViaQuadrantProperty, 
				String complementaryViaComplementProperty, 
				String complementaryViaComplementDetailProperty) {
			
			this.mainViaTypeProperty = mainViaTypeProperty;
			this.mainViaNumProperty = mainViaNumProperty;
			this.mainViaLetterProperty = mainViaLetterProperty;
			this.mainViaBisProperty = mainViaBisProperty;
			this.mainViaBisLetterProperty = mainViaBisLetterProperty;
			this.mainViaQuadrantProperty = mainViaQuadrantProperty;
			this.mainViaComplementProperty = mainViaComplementProperty;
			this.mainViaComplementDetailProperty = mainViaComplementDetailProperty;
			this.secondaryViaNumProperty = secondaryViaNumProperty;
			this.secondaryViaLetterProperty = secondaryViaLetterProperty;
			this.complementaryViaNumProperty = complementaryViaNumProperty;
			this.complementaryViaQuadrantProperty = complementaryViaQuadrantProperty;
			this.complementaryViaComplementProperty = complementaryViaComplementProperty;
			this.complementaryViaComplementDetailProperty = complementaryViaComplementDetailProperty;
		}

		public String getMainViaTypeProperty() {
			return StringUtils.defaultString(mainViaTypeProperty, "mainViaType");
		}
		
		public void setMainViaTypeProperty(String mainViaTypeProperty) {
			this.mainViaTypeProperty = mainViaTypeProperty;
		}
		
		public String getMainViaNumProperty() {
			return StringUtils.defaultString(mainViaNumProperty, "mainViaNum");
		}

		public void setMainViaNumProperty(String mainViaNumProperty) {
			this.mainViaNumProperty = mainViaNumProperty;
		}

		public String getMainViaLetterProperty() {
			return StringUtils.defaultString(mainViaLetterProperty, "mainViaLetter");
		}

		public void setMainViaLetterProperty(String mainViaLetterProperty) {
			this.mainViaLetterProperty = mainViaLetterProperty;
		}

		public String getMainViaBisProperty() {
			return StringUtils.defaultString(mainViaBisProperty, "mainViaBis");
		}

		public void setMainViaBisProperty(String mainViaBisProperty) {
			this.mainViaBisProperty = mainViaBisProperty;
		}

		public String getMainViaBisLetterProperty() {
			return StringUtils.defaultString(mainViaBisLetterProperty, "mainViaBisLetter");
		}

		public void setMainViaBisLetterProperty(String mainViaBisLetterProperty) {
			this.mainViaBisLetterProperty = mainViaBisLetterProperty;
		}

		public String getMainViaQuadrantProperty() {
			return StringUtils.defaultString(mainViaQuadrantProperty, "mainViaQuadrant");
		}

		public void setMainViaQuadrantProperty(String mainViaQuadrantProperty) {
			this.mainViaQuadrantProperty = mainViaQuadrantProperty;
		}

		public String getMainViaComplementProperty() {
			return StringUtils.defaultString(mainViaComplementProperty, "mainViaComplement");
		}

		public void setMainViaComplementProperty(String mainViaComplementProperty) {
			this.mainViaComplementProperty = mainViaComplementProperty;
		}

		public String getMainViaComplementDetailProperty() {
			return StringUtils.defaultString(mainViaComplementDetailProperty, "mainViaComplementDetail");
		}

		public void setMainViaComplementDetailProperty(String mainViaComplementDetailProperty) {
			this.mainViaComplementDetailProperty = mainViaComplementDetailProperty;
		}

		public String getSecondaryViaNumProperty() {
			return StringUtils.defaultString(secondaryViaNumProperty, "secondaryViaNum");
		}

		public void setSecondaryViaNumProperty(String secondaryViaNumProperty) {
			this.secondaryViaNumProperty = secondaryViaNumProperty;
		}

		public String getSecondaryViaLetterProperty() {
			return StringUtils.defaultString(secondaryViaLetterProperty, "secondaryViaLetter");
		}

		public void setSecondaryViaLetterProperty(String secondaryViaLetterProperty) {
			this.secondaryViaLetterProperty = secondaryViaLetterProperty;
		}

		public String getComplementaryViaNumProperty() {
			return StringUtils.defaultString(complementaryViaNumProperty, "complementaryViaNum");
		}

		public void setComplementaryViaNumProperty(String complementaryViaNumProperty) {
			this.complementaryViaNumProperty = complementaryViaNumProperty;
		}

		public String getComplementaryViaQuadrantProperty() {
			return StringUtils.defaultString(complementaryViaQuadrantProperty, "complementaryViaQuadrant");
		}

		public void setComplementaryViaQuadrantProperty(String complementaryViaQuadrantProperty) {
			this.complementaryViaQuadrantProperty = complementaryViaQuadrantProperty;
		}

		public String getComplementaryViaComplementProperty() {
			return StringUtils.defaultString(complementaryViaComplementProperty, "complementaryViaComplement");
		}

		public void setComplementaryViaComplementProperty(String complementaryViaComplementProperty) {
			this.complementaryViaComplementProperty = complementaryViaComplementProperty;
		}

		public String getComplementaryViaComplementDetailProperty() {
			return StringUtils.defaultString(complementaryViaComplementDetailProperty, "complementaryViaComplementDetail");
		}

		/**
		 * @param complementaryViaComplementDetailProperty the complementaryViaComplementDetailProperty to set
		 */
		public void setComplementaryViaComplementDetailProperty(String complementaryViaComplementDetailProperty) {
			this.complementaryViaComplementDetailProperty = complementaryViaComplementDetailProperty;
		}
		
		public String[] getPropertyNames(){
			return new String[]{
					getMainViaTypeProperty(), getMainViaNumProperty(), 
					getMainViaLetterProperty(), getMainViaBisProperty(), getMainViaBisLetterProperty(), 
					getMainViaQuadrantProperty(), getMainViaComplementProperty(), getMainViaComplementDetailProperty(), 
					getSecondaryViaNumProperty(), getSecondaryViaLetterProperty(), getComplementaryViaNumProperty(), 
					getComplementaryViaQuadrantProperty(), getComplementaryViaComplementProperty(), getComplementaryViaComplementDetailProperty()
			};
		}
		
	}
}

