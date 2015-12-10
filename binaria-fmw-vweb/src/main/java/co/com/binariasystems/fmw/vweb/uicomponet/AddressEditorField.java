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
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.Address;
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

public class AddressEditorField<T extends Address> extends CustomField<T> implements UIConstants, VWebCommonConstants{
	private static final Logger LOGGER = LoggerFactory.getLogger(AddressEditorField.class);
	private static final String[] ADDRESS_PROPERTYIDS = {
		"mainViaType", "mainViaNum", "mainViaLetter", "mainViaBis", "mainViaBisLetter", "mainViaQuadrant",
		"mainViaComplement", "mainViaComplementDetail", "secondaryViaNum", "secondaryViaLetter",
		"complementaryViaNum", "complementaryViaQuadrant", "complementaryViaComplement", "complementaryViaComplementDetail"
	};
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
	
	private BeanItem<InternalAddress> beanItem = new BeanItem<InternalAddress>(new InternalAddress(), InternalAddress.class);
	private ObjectProperty<String> generatedAddressProperty = new ObjectProperty<String>("", String.class);
	private ValueChangeListener valueChangeListener;
	
	public AddressEditorField() {
		super();
		this.addStyleName(ADDRESS_EDITOR_STYLENAME);
	}
	
	public AddressEditorField(String caption) {
		super();
		this.setCaption(caption);
		this.addStyleName(ADDRESS_EDITOR_STYLENAME);
	}


	@Override
	protected Component initContent() {
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
		
		loadParameters();
		bindEvents();
		
		return content;
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
		mainViaTypeCmb.setNullSelectionItemId("");
		mainViaBisCmb.setNullSelectionItemId("");
		mainViaQuadrantCmb.setNullSelectionItemId("");
		complementaryViaQuadrantCmb.setNullSelectionItemId("");
		mainViaComplementCmb.setNullSelectionItemId("");
		complementaryViaComplementCmb.setNullSelectionItemId("");
		
		setComboBoxItems(mainViaTypeCmb, parametersProvider.getViaTypes(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaBisCmb, parametersProvider.getBis(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaQuadrantCmb, parametersProvider.getQuadrants(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(complementaryViaQuadrantCmb, parametersProvider.getQuadrants(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(mainViaComplementCmb, parametersProvider.getNomenclatureComplements(VWebUtils.getCurrentUserLocale()));
		setComboBoxItems(complementaryViaComplementCmb, parametersProvider.getNomenclatureComplements(VWebUtils.getCurrentUserLocale()));
	}
	
	private void setComboBoxItems(NativeSelect comboBox, List<? extends Listable> items){
		comboBox.setItemCaption("", VWebUtils.getComboBoxNoSelectionShortDescription());
		for(Listable item : items){
			comboBox.addItem(item.getPK());
			comboBox.setItemCaption(item.getPK(), item.getDescription());
		}
	}
	
	private void bindEvents(){
		valueChangeListener = new Property.ValueChangeListener() {
			@Override public void valueChange(Property.ValueChangeEvent event) {
				if(event.getProperty().equals(AddressEditorField.this.getPropertyDataSource()))
					handleAdrressEditorPropertyValueChange(event);
				else
					handleControlsPropertyValueChange(event);
			}
		};
		
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
		
		mainViaTypeCmb.setPropertyDataSource(beanItem.getItemProperty("mainViaType"));
		mainViaNumTxt.setPropertyDataSource(beanItem.getItemProperty("mainViaNum"));
		mainViaLetterTxt.setPropertyDataSource(beanItem.getItemProperty("mainViaLetter"));
		mainViaBisCmb.setPropertyDataSource(beanItem.getItemProperty("mainViaBis"));
		mainViaBisLetterTxt.setPropertyDataSource(beanItem.getItemProperty("mainViaBisLetter"));
		mainViaQuadrantCmb.setPropertyDataSource(beanItem.getItemProperty("mainViaQuadrant"));
		mainViaComplementCmb.setPropertyDataSource(beanItem.getItemProperty("mainViaComplement"));
		mainViaComplementTxt.setPropertyDataSource(beanItem.getItemProperty("mainViaComplementDetail"));
		secondaryViaNumTxt.setPropertyDataSource(beanItem.getItemProperty("secondaryViaNum"));
		secondaryViaLetterTxt.setPropertyDataSource(beanItem.getItemProperty("secondaryViaLetter"));
		complementaryViaNumTxt.setPropertyDataSource(beanItem.getItemProperty("complementaryViaNum"));
		complementaryViaQuadrantCmb.setPropertyDataSource(beanItem.getItemProperty("complementaryViaQuadrant"));
		complementaryViaComplementCmb.setPropertyDataSource(beanItem.getItemProperty("complementaryViaComplement"));
		complementaryViaComplementTxt.setPropertyDataSource(beanItem.getItemProperty("complementaryViaComplementDetail"));
		generatedAddressTxt.setPropertyDataSource(generatedAddressProperty);
		
		mainViaNumTxt.setNullRepresentation("");
		mainViaLetterTxt.setNullRepresentation("");
		mainViaBisLetterTxt.setNullRepresentation("");
		mainViaComplementTxt.setNullRepresentation("");
		secondaryViaNumTxt.setNullRepresentation("");
		secondaryViaLetterTxt.setNullRepresentation("");
		complementaryViaNumTxt.setNullRepresentation("");
		complementaryViaComplementTxt.setNullRepresentation("");
		
		for(String propertyId : ADDRESS_PROPERTYIDS){
			if(beanItem.getItemProperty(propertyId) instanceof AbstractProperty)
				((AbstractProperty)beanItem.getItemProperty(propertyId)).addValueChangeListener(valueChangeListener);
		}
		addValueChangeListener(valueChangeListener);
	}
	
	
	private void handleAdrressEditorPropertyValueChange(Property.ValueChangeEvent event){
		if(event.getProperty().getValue() == null)
			beanItem.getBean().clean();
		for(String propertyId : ADDRESS_PROPERTYIDS){
			try {
				beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(event.getProperty().getValue(), propertyId));
			} catch (ReflectiveOperationException e) {
				LOGGER.error("Cannot handle value change on AddressEditor: "+e.getMessage());
			}
		}
	}
	
	private void handleControlsPropertyValueChange(Property.ValueChangeEvent event){
		generatedAddressProperty.setValue(beanItem.getBean().toString());
		if(this.getPropertyDataSource() != null){
			if(this.getPropertyDataSource().getValue() != null)
				ObjectUtils.transferProperties(beanItem.getBean(), this.getPropertyDataSource().getValue());
			else
				this.getPropertyDataSource().setValue(beanItem.getBean());
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
	
	public abstract class Address{
		public abstract String getMainViaType();
		public abstract void setMainViaType(String mainViaType);
		public abstract Integer getMainViaNum();
		public abstract void setMainViaNum(Integer mainViaNum);
		public abstract String getMainViaLetter();
		public abstract void setMainViaLetter(String mainViaLetter);
		public abstract String getMainViaBis();
		public abstract void setMainViaBis(String mainViaBis);
		public abstract String getMainViaBisLetter();
		public abstract void setMainViaBisLetter(String mainViaBisLetter);
		public abstract String getMainViaQuadrant();
		public abstract void setMainViaQuadrant(String mainViaQuadrant);
		public abstract String getMainViaComplement();
		public abstract void setMainViaComplement(String mainViaComplement);
		public abstract String getMainViaComplementDetail();
		public abstract void setMainViaComplementDetail(String mainViaComplementDetail);
		public abstract Integer getSecondaryViaNum();
		public abstract void setSecondaryViaNum(Integer secondaryViaNum);
		public abstract String getSecondaryViaLetter();
		public abstract void setSecondaryViaLetter(String secondaryViaLetter);
		public abstract Integer getComplementaryViaNum();
		public abstract void setComplementaryViaNum(Integer complementaryViaNum);
		public abstract String getComplementaryViaQuadrant();
		public abstract void setComplementaryViaQuadrant(String complementaryViaQuadrant);
		public abstract String getComplementaryViaComplement();
		public abstract void setComplementaryViaComplement(String complementaryViaComplement);
		public abstract String getComplementaryViaComplementDetail();
		public abstract void setComplementaryViaComplementDetail(String complementaryViaComplementDetail);
		
		public void clean(){
			setMainViaType(null);
			setMainViaNum(null);
			setMainViaLetter(null);
			setMainViaBis(null);
			setMainViaBisLetter(null);
			setMainViaQuadrant(null);
			setMainViaComplement(null);
			setMainViaComplementDetail(null);
			setSecondaryViaNum(null);
			setSecondaryViaLetter(null);
			setComplementaryViaNum(null);
			setComplementaryViaQuadrant(null);
			setComplementaryViaComplement(null);
			setComplementaryViaComplementDetail(null);
		}
		
		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(StringUtils.isNotBlank( getMainViaType()) ?  getMainViaType() : "")
			.append(getMainViaNum() != null ? FMWConstants.WHITE_SPACE : "").append(getMainViaNum() != null ? getMainViaNum() : "")
			.append(StringUtils.isNotBlank(getMainViaLetter()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaLetter()) ? getMainViaLetter() : "")
			.append(StringUtils.isNotBlank(getMainViaBis()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaBis()) ? getMainViaBis() : "")
			.append(StringUtils.isNotBlank(getMainViaBisLetter()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaBisLetter()) ? getMainViaBisLetter() : "")
			.append(StringUtils.isNotBlank(getMainViaQuadrant()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaQuadrant()) ? getMainViaQuadrant() : "")
			.append(StringUtils.isNotBlank(getMainViaComplement()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaComplement()) ? getMainViaComplement() : "")
			.append(StringUtils.isNotBlank(getMainViaComplementDetail()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getMainViaComplementDetail()) ? getMainViaComplementDetail() : "")
			.append(getSecondaryViaNum() != null ? FMWConstants.WHITE_SPACE : "").append(getSecondaryViaNum() != null ? getSecondaryViaNum() : "")
			.append(StringUtils.isNotBlank(getSecondaryViaLetter()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getSecondaryViaLetter()) ? getSecondaryViaLetter() : "")
			.append(getComplementaryViaNum() != null ? FMWConstants.WHITE_SPACE : "").append(getComplementaryViaNum() != null ? getComplementaryViaNum() : "")
			.append(StringUtils.isNotBlank(getComplementaryViaQuadrant()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getComplementaryViaQuadrant()) ? getComplementaryViaQuadrant() : "")
			.append(StringUtils.isNotBlank(getComplementaryViaComplement()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getComplementaryViaComplement()) ? getComplementaryViaComplement() : "")
			.append(StringUtils.isNotBlank(getComplementaryViaComplementDetail()) ? FMWConstants.WHITE_SPACE : "").append(StringUtils.isNotBlank(getComplementaryViaComplementDetail()) ? getComplementaryViaComplementDetail() : "");
			return stringBuilder.toString().trim();
		}
	}

	private class InternalAddress extends Address{
		private String mainViaType;
		private Integer mainViaNum;
		private String mainViaLetter;
		private String mainViaBis;
		private String mainViaBisLetter;
		private String mainViaQuadrant;
		private String mainViaComplement;
		private String mainViaComplementDetail;
		
		private Integer secondaryViaNum;
		private String secondaryViaLetter;
		
		private Integer complementaryViaNum;
		private String complementaryViaQuadrant;
		private String complementaryViaComplement;
		private String complementaryViaComplementDetail;

		public String getMainViaType() {
			return mainViaType;
		}
		public void setMainViaType(String mainViaType) {
			this.mainViaType = mainViaType;
		}
		public Integer getMainViaNum() {
			return mainViaNum;
		}
		public void setMainViaNum(Integer mainViaNum) {
			this.mainViaNum = mainViaNum;
		}
		public String getMainViaLetter() {
			return mainViaLetter;
		}
		public void setMainViaLetter(String mainViaLetter) {
			this.mainViaLetter = StringUtils.upperCase(mainViaLetter);
		}
		public String getMainViaBis() {
			return mainViaBis;
		}
		public void setMainViaBis(String mainViaBis) {
			this.mainViaBis = mainViaBis;
		}
		public String getMainViaBisLetter() {
			return mainViaBisLetter;
		}
		public void setMainViaBisLetter(String mainViaBisLetter) {
			this.mainViaBisLetter = StringUtils.upperCase(mainViaBisLetter);
		}
		public String getMainViaQuadrant() {
			return mainViaQuadrant;
		}
		public void setMainViaQuadrant(String mainViaQuadrant) {
			this.mainViaQuadrant = mainViaQuadrant;
		}
		public String getMainViaComplement() {
			return mainViaComplement;
		}
		public void setMainViaComplement(String mainViaComplement) {
			this.mainViaComplement = mainViaComplement;
		}
		public String getMainViaComplementDetail() {
			return mainViaComplementDetail;
		}
		public void setMainViaComplementDetail(String mainViaComplementDetail) {
			this.mainViaComplementDetail = StringUtils.upperCase(mainViaComplementDetail);
		}
		public Integer getSecondaryViaNum() {
			return secondaryViaNum;
		}
		public void setSecondaryViaNum(Integer secondaryViaNum) {
			this.secondaryViaNum = secondaryViaNum;
		}
		public String getSecondaryViaLetter() {
			return secondaryViaLetter;
		}
		public void setSecondaryViaLetter(String secondaryViaLetter) {
			this.secondaryViaLetter = StringUtils.upperCase(secondaryViaLetter);
		}
		public Integer getComplementaryViaNum() {
			return complementaryViaNum;
		}
		public void setComplementaryViaNum(Integer complementaryViaNum) {
			this.complementaryViaNum = complementaryViaNum;
		}
		public String getComplementaryViaQuadrant() {
			return complementaryViaQuadrant;
		}
		public void setComplementaryViaQuadrant(String complementaryViaQuadrant) {
			this.complementaryViaQuadrant = complementaryViaQuadrant;
		}
		public String getComplementaryViaComplement() {
			return complementaryViaComplement;
		}
		public void setComplementaryViaComplement(String complementaryViaComplement) {
			this.complementaryViaComplement = complementaryViaComplement;
		}
		public String getComplementaryViaComplementDetail() {
			return complementaryViaComplementDetail;
		}
		public void setComplementaryViaComplementDetail(
				String complementaryViaComplementDetail) {
			this.complementaryViaComplementDetail = StringUtils.upperCase(complementaryViaComplementDetail);
		}
	}
}

