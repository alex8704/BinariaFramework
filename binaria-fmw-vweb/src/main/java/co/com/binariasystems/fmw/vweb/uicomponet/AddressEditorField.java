package co.com.binariasystems.fmw.vweb.uicomponet;

import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.Address;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class AddressEditorField extends CustomField<Address>{
	private HorizontalLayout content;
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
	
	
	public AddressEditorField() {
		super();
	}
	
	public AddressEditorField(String caption) {
		super();
		this.setCaption(caption);
		this.addStyleName("address-editor");
	}


	@Override
	protected Component initContent() {
		content = new HorizontalLayout();
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
		
		mainViaLetterTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
		mainViaBisLetterTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
		secondaryViaLetterTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
		mainViaComplementTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
		complementaryViaComplementTxt.addStyleName(UIConstants.UPPER_TRANSFORM_STYLENAME);
		
		mainViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		secondaryViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		complementaryViaLbl.addStyleName(ValoTheme.LABEL_BOLD);
		
		content.setSpacing(true);
		
		return content;
	}
	
	
	private void buildMainViaPanel(){
		VerticalLayout mainViaPanel = new VerticalLayout();
		HorizontalLayout horizontalRow1 = new HorizontalLayout();
		HorizontalLayout horizontalRow2 = new HorizontalLayout();
		mainViaLbl = new Label("Via Principal");
		mainViaTypeCmb = new NativeSelect("Tipo Via");
		mainViaNumTxt = new TextField("Num.");
		mainViaLetterTxt = new TextField("Letra(s)");
		mainViaBisCmb = new NativeSelect("Bis");
		mainViaBisLetterTxt = new TextField("Letra(s)");
		mainViaQuadrantCmb = new NativeSelect("Cuadrante");
		mainViaComplementCmb = new NativeSelect("Complemento");
		mainViaComplementTxt = new TextField("");
		Label numberSymbolSeparatorLbl = new Label("#");
		
		horizontalRow1.addComponents(mainViaTypeCmb, 
				mainViaNumTxt,
				mainViaLetterTxt,
				mainViaBisCmb,
				mainViaBisLetterTxt,
				mainViaQuadrantCmb,
				numberSymbolSeparatorLbl);
		
		horizontalRow2.addComponents(mainViaComplementCmb,
				mainViaComplementTxt);
		
		mainViaLbl.setWidth(100, Unit.PERCENTAGE);
		mainViaLbl.addStyleName(UIConstants.CENTER_ALIGN_STYLE);
		
		mainViaTypeCmb.setWidth(100, Unit.PIXELS);
		mainViaNumTxt.setWidth(50, Unit.PIXELS);
		mainViaLetterTxt.setWidth(50, Unit.PIXELS);
		mainViaBisCmb.setWidth(50, Unit.PIXELS);
		mainViaBisLetterTxt.setWidth(50, Unit.PIXELS);
		mainViaQuadrantCmb.setWidth(100, Unit.PIXELS);
		mainViaComplementCmb.setWidth(100, Unit.PIXELS);
		mainViaComplementTxt.setWidth(100, Unit.PERCENTAGE);
		
		mainViaPanel.addComponents(mainViaLbl,
				horizontalRow1,
				horizontalRow2);
		mainViaPanel.setComponentAlignment(mainViaLbl, Alignment.TOP_CENTER);
		
		horizontalRow1.setComponentAlignment(numberSymbolSeparatorLbl, Alignment.BOTTOM_CENTER);
		
		horizontalRow1.setSpacing(true);
		horizontalRow2.setSpacing(true);
		
		content.addComponent(mainViaPanel);
	}
	
	private void buildSecondaryAndComplementaryPanel(){
		VerticalLayout layout = new VerticalLayout();
		HorizontalLayout hLayout = new HorizontalLayout();
		VerticalLayout secoundViaPanel = new VerticalLayout();
		VerticalLayout compleViaPanel = new VerticalLayout();
		HorizontalLayout auxHLayout1 = new HorizontalLayout();
		HorizontalLayout auxHLayout2 = new HorizontalLayout();
		HorizontalLayout auxHLayout3 = new HorizontalLayout();
		
		secondaryViaLbl = new Label("Via Seucundaria");
		secondaryViaNumTxt = new TextField("Num.");
		secondaryViaLetterTxt = new TextField("Letra(s)");
		
		complementaryViaLbl = new Label("Via Complementaria");
		complementaryViaNumTxt = new TextField("Num.");
		complementaryViaQuadrantCmb = new NativeSelect("Cuadrante");
		complementaryViaComplementCmb = new NativeSelect("Complemento");
		complementaryViaComplementTxt = new TextField("");
		Label dashSymbolSeparatorLbl = new Label("-");
		
		auxHLayout1.addComponents(secondaryViaNumTxt, secondaryViaLetterTxt, dashSymbolSeparatorLbl);
		secoundViaPanel.addComponents(secondaryViaLbl, auxHLayout1);
		auxHLayout2.addComponents(complementaryViaNumTxt, complementaryViaQuadrantCmb);
		compleViaPanel.addComponents(complementaryViaLbl, auxHLayout2);
		auxHLayout3.addComponents(complementaryViaComplementCmb, complementaryViaComplementTxt);
		hLayout.addComponents(secoundViaPanel, compleViaPanel);
		layout.addComponents(hLayout, auxHLayout3);
		
		
		secondaryViaLbl.setWidth(100, Unit.PERCENTAGE);
		complementaryViaLbl.setWidth(100, Unit.PERCENTAGE);
		secondaryViaLbl.addStyleName(UIConstants.CENTER_ALIGN_STYLE);
		complementaryViaLbl.addStyleName(UIConstants.CENTER_ALIGN_STYLE);
		
		secoundViaPanel.setComponentAlignment(secondaryViaLbl, Alignment.TOP_CENTER);
		compleViaPanel.setComponentAlignment(complementaryViaLbl, Alignment.TOP_CENTER);
		
		secondaryViaNumTxt.setWidth(50, Unit.PIXELS);
		secondaryViaLetterTxt.setWidth(50, Unit.PIXELS);
		complementaryViaNumTxt.setWidth(50, Unit.PIXELS);
		complementaryViaQuadrantCmb.setWidth(100, Unit.PIXELS);
		complementaryViaComplementCmb.setWidth(100, Unit.PIXELS);
		complementaryViaComplementTxt.setWidth(100, Unit.PERCENTAGE);
		
		auxHLayout1.setComponentAlignment(dashSymbolSeparatorLbl, Alignment.BOTTOM_CENTER);
		 
		hLayout.setSpacing(true);
		auxHLayout1.setSpacing(true);
		auxHLayout2.setSpacing(true);
		auxHLayout3.setSpacing(true);
		
		content.addComponent(layout);
	}

	@Override
	public Class<Address> getType() {
		return Address.class;
	}
	
	public static class Address{
		
	}
}

