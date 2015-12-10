package co.com.binariasystems.fmw.vweb.uicomponet;

import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.Address;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

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

public class AddressEditorField extends CustomField<Address> implements UIConstants, VWebCommonConstants{
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
	
	
	public AddressEditorField() {
		super();
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

	@Override
	public Class<Address> getType() {
		return Address.class;
	}
	
	public static class Address{
		
	}
}

