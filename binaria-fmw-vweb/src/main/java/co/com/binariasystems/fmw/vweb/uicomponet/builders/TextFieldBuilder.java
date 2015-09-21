package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.server.Resource;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class TextFieldBuilder extends TextField{
	
	private boolean useCaptionAsInputPrompt;
	private boolean centerAlign;
	private boolean rightAlign;

	public TextFieldBuilder() {
		super();
		setDefaults();
	}

	public TextFieldBuilder(Property dataSource) {
		super(dataSource);
		setDefaults();
	}

	public TextFieldBuilder(String caption, Property dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public TextFieldBuilder(String caption, String value) {
		super(caption, value);
		setDefaults();
	}

	public TextFieldBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setNullRepresentation("");
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
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
	
	public TextFieldBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public TextFieldBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public TextFieldBuilder readOnly(){
		setReadOnly(true);
		return this;
	}
	
	public TextFieldBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public TextFieldBuilder useCaptioAsPrompt(){
		useCaptionAsInputPrompt = true;
		if(StringUtils.isNoneEmpty(getCaption()))
			setInputPrompt(getCaption());
		return this;
	}
	
	public TextFieldBuilder maxLength(int maxLength){
		setMaxLength(maxLength);
		return this;
	}
	
	public TextFieldBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public TextFieldBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public TextFieldBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public TextFieldBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public TextFieldBuilder withValue(String value){
		setValue(value);
		return this;
	}
	
	public TextFieldBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public TextFieldBuilder withPixelsWidth(float width){
		setWidth(width, Unit.PIXELS);
		return this;
	}
	
	public TextFieldBuilder withPointsWidth(float width){
		setWidth(width, Unit.POINTS);
		return this;
	}
	
	public TextFieldBuilder withEmWidth(float width){
		setWidth(width, Unit.EM);
		return this;
	}
	
	public TextFieldBuilder withRemWidth(float width){
		setWidth(width, Unit.REM);
		return this;
	}
	
	public TextFieldBuilder withMilimetersWidth(float width){
		setWidth(width, Unit.MM);
		return this;
	}
	
	public TextFieldBuilder withCentimetersWidth(float width){
		setWidth(width, Unit.CM);
		return this;
	}
	
	public TextFieldBuilder withInchsWidth(float width){
		setWidth(width, Unit.INCH);
		return this;
	}
	
	public TextFieldBuilder withPixelsHeight(float height){
		setHeight(height, Unit.PIXELS);
		return this;
	}
	
	public TextFieldBuilder withPointsHeight(float height){
		setHeight(height, Unit.POINTS);
		return this;
	}
	
	public TextFieldBuilder withEmHeight(float height){
		setHeight(height, Unit.EM);
		return this;
	}
	
	public TextFieldBuilder withRemHeight(float height){
		setHeight(height, Unit.REM);
		return this;
	}
	
	public TextFieldBuilder withMilimetersHeight(float height){
		setHeight(height, Unit.MM);
		return this;
	}
	
	public TextFieldBuilder withCentimetersHeight(float height){
		setHeight(height, Unit.CM);
		return this;
	}
	
	public TextFieldBuilder withInchsHeight(float height){
		setHeight(height, Unit.INCH);
		return this;
	}
	
	public TextFieldBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public TextFieldBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public TextFieldBuilder centerAlignContent(){
		if(!centerAlign){
			if(rightAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			centerAlign = !centerAlign;
		}
		return this;
	}
	
	public TextFieldBuilder rightAlignContent(){
		if(!rightAlign){
			if(centerAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			rightAlign = !rightAlign;
		}
		return this;
	}
	
	public TextFieldBuilder withRegexpValidator(String regExp, String example){
		addValidator(ValidationUtils.regexpValidator(getCaption(), regExp, null));
		return this;
	}
	
	public TextFieldBuilder withEmailValidator(){
		addValidator(ValidationUtils.emailValidator(getCaption()));
		return this;
	}
	
	public TextFieldBuilder withMinDoubleValidator(Double minValue){
		addValidator(ValidationUtils.minDoubleValidator(getCaption(), minValue));
		return this;
	}
	
	public TextFieldBuilder withMaxDoubleValidator(Double maxValue){
		addValidator(ValidationUtils.maxDoubleValidator(getCaption(), maxValue));
		return this;
	}
	
	public TextFieldBuilder withdoubleRangeValidator(Double minValue, Double maxValue){
		addValidator(ValidationUtils.doubleRangeValidator(getCaption(), minValue, maxValue));
		return this;
	}
	
	public TextFieldBuilder withMinIntegerValidator(Integer minValue){
		addValidator(ValidationUtils.minIntegerValidator(getCaption(), minValue));
		return this;
	}
	
	public TextFieldBuilder withMaxIntegerValidator(Integer maxValue){
		addValidator(ValidationUtils.maxIntegerValidator(getCaption(), maxValue));
		return this;
	}
	
	public TextFieldBuilder withIntegerRangeValidator(Integer minValue, Integer maxValue){
		addValidator(ValidationUtils.integerRangeValidator(getCaption(), minValue, maxValue));
		return this;
	}
	
	public TextFieldBuilder withMinStringLengthValidator(Integer minLength){
		addValidator(ValidationUtils.minStringLengthValidator(getCaption(), minLength));
		return this;
	}
	
	public TextFieldBuilder withMaxStringLengthValidator(Integer maxLength){
		addValidator(ValidationUtils.maxStringLengthValidator(getCaption(), maxLength));
		return this;
	}
	
	public TextFieldBuilder withStringLengRangeValidator(Integer minLength, Integer maxLength){
		addValidator(ValidationUtils.stringLengthRangeValidator(getCaption(), minLength, maxLength));
		return this;
	}
}
