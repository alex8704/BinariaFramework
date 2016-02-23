package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;

public class PasswordFieldBuilder extends PasswordField {
	
	private boolean useCaptionAsInputPrompt;
	private boolean centerAlign;
	private boolean rightAlign;

	public PasswordFieldBuilder() {
		super();
		setDefaults();
	}

	public PasswordFieldBuilder(Property dataSource) {
		super(dataSource);
		setDefaults();
	}

	public PasswordFieldBuilder(String caption, Property dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public PasswordFieldBuilder(String caption, String value) {
		super(caption, value);
		setDefaults();
	}

	public PasswordFieldBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setNullRepresentation("");
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		setInvalidCommitted(true);
		setImmediate(true);
		setNullSettingAllowed(true);
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if(StringUtils.isEmpty(getDescription()))
			setDescription(caption);
		if(useCaptionAsInputPrompt && StringUtils.isNoneEmpty(caption))
			setInputPrompt(caption);
		setRequiredError(ValidationUtils.requiredErrorFor(caption));
	}
	
	public PasswordFieldBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public PasswordFieldBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public PasswordFieldBuilder readOnly(){
		setReadOnly(true);
		return this;
	}
	
	public PasswordFieldBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public PasswordFieldBuilder useCaptioAsPrompt(){
		useCaptionAsInputPrompt = true;
		if(StringUtils.isNoneEmpty(getCaption()))
			setInputPrompt(getCaption());
		return this;
	}
	
	public PasswordFieldBuilder maxLength(int maxLength){
		setMaxLength(maxLength);
		return this;
	}
	
	public PasswordFieldBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public PasswordFieldBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public PasswordFieldBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public PasswordFieldBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public PasswordFieldBuilder withValue(String value){
		setValue(value);
		return this;
	}
	
	public PasswordFieldBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public PasswordFieldBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public PasswordFieldBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public PasswordFieldBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public PasswordFieldBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public PasswordFieldBuilder centerAlignContent(){
		if(!centerAlign){
			if(rightAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			centerAlign = !centerAlign;
		}
		return this;
	}
	
	public PasswordFieldBuilder rightAlignContent(){
		if(!rightAlign){
			if(centerAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			rightAlign = !rightAlign;
		}
		return this;
	}
	
	public PasswordFieldBuilder withRegexpValidator(String regExp, String example){
		addValidator(ValidationUtils.regexpValidator(getCaption(), regExp, null));
		return this;
	}
	
	public PasswordFieldBuilder withEmailValidator(){
		addValidator(ValidationUtils.emailValidator(getCaption()));
		return this;
	}
	
	public PasswordFieldBuilder withMinDoubleValidator(Double minValue){
		addValidator(ValidationUtils.minDoubleValidator(getCaption(), minValue));
		return this;
	}
	
	public PasswordFieldBuilder withMaxDoubleValidator(Double maxValue){
		addValidator(ValidationUtils.maxDoubleValidator(getCaption(), maxValue));
		return this;
	}
	
	public PasswordFieldBuilder withdoubleRangeValidator(Double minValue, Double maxValue){
		addValidator(ValidationUtils.doubleRangeValidator(getCaption(), minValue, maxValue));
		return this;
	}
	
	public PasswordFieldBuilder withMinIntegerValidator(Integer minValue){
		addValidator(ValidationUtils.minIntegerValidator(getCaption(), minValue));
		return this;
	}
	
	public PasswordFieldBuilder withMaxIntegerValidator(Integer maxValue){
		addValidator(ValidationUtils.maxIntegerValidator(getCaption(), maxValue));
		return this;
	}
	
	public PasswordFieldBuilder withIntegerRangeValidator(Integer minValue, Integer maxValue){
		addValidator(ValidationUtils.integerRangeValidator(getCaption(), minValue, maxValue));
		return this;
	}
	
	public PasswordFieldBuilder withMinStringLengthValidator(Integer minLength){
		addValidator(ValidationUtils.minStringLengthValidator(getCaption(), minLength));
		return this;
	}
	
	public PasswordFieldBuilder withMaxStringLengthValidator(Integer maxLength){
		addValidator(ValidationUtils.maxStringLengthValidator(getCaption(), maxLength));
		return this;
	}
	
	public PasswordFieldBuilder withStringLengRangeValidator(Integer minLength, Integer maxLength){
		addValidator(ValidationUtils.stringLengthRangeValidator(getCaption(), minLength, maxLength));
		return this;
	}
}
