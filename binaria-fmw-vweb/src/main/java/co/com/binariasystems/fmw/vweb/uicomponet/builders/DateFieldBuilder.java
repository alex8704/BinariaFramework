package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.themes.ValoTheme;

public class DateFieldBuilder extends DateField {
	private boolean centerAlign;
	private boolean rightAlign;
	
	public DateFieldBuilder() {
		super();
		setDefaults();
	}

	public DateFieldBuilder(Property dataSource) throws IllegalArgumentException {
		super(dataSource);
		setDefaults();
	}

	public DateFieldBuilder(String caption, Date value) {
		super(caption, value);
		setDefaults();
	}

	public DateFieldBuilder(String caption, Property dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public DateFieldBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		setInvalidCommitted(true);
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	
	public DateFieldBuilder withClosedRange(Date minValue, Date maxValue){
		setRangeStart(minValue);
		setRangeEnd(maxValue);
		setDateOutOfRangeMessage(ValidationUtils.dateRangeErrorFor(getCaption(), minValue, maxValue));
		return this;
	}
	
	public DateFieldBuilder secondResolution(){
		setResolution(Resolution.SECOND);
		return this;
	}
	
	public DateFieldBuilder minuteResolution(){
		setResolution(Resolution.MINUTE);
		return this;
	}
	
	public DateFieldBuilder hourResolution(){
		setResolution(Resolution.HOUR);
		return this;
	}
	
	public DateFieldBuilder dayResolution(){
		setResolution(Resolution.HOUR);
		return this;
	}
	
	public DateFieldBuilder monthResolution(){
		setResolution(Resolution.MONTH);
		return this;
	}
	
	public DateFieldBuilder yearResolution(){
		setResolution(Resolution.YEAR);
		return this;
	}
	
	public DateFieldBuilder birthDateRestrict(){
		setRangeEnd(new Date());
		return this;
	}
	
	public DateFieldBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public DateFieldBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public DateFieldBuilder readOnly(){
		setReadOnly(true);
		return this;
	}
	
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if(StringUtils.isEmpty(getDescription()))
			setDescription(caption);
		setRequiredError(ValidationUtils.requiredErrorFor(caption));
		if(getRangeStart() != null || getRangeEnd() != null)
			setDateOutOfRangeMessage(ValidationUtils.dateRangeErrorFor(getCaption(), getRangeStart(), getRangeEnd()));
	}
	
	public DateFieldBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public DateFieldBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public DateFieldBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public DateFieldBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public DateFieldBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public DateFieldBuilder withValue(Date value){
		setValue(value);
		return this;
	}
	
	public DateFieldBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public DateFieldBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public DateFieldBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public DateFieldBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public DateFieldBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public DateFieldBuilder centerAlignContent(){
		if(!centerAlign){
			if(rightAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			centerAlign = !centerAlign;
		}
		return this;
	}
	
	public DateFieldBuilder rightAlignContent(){
		if(!rightAlign){
			if(centerAlign) removeStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
			rightAlign = !rightAlign;
		}
		return this;
	}
	
	public DateFieldBuilder withMinDateValidator(Date minDate){
		addValidator(ValidationUtils.minDateValidator(getCaption(), minDate));
		return this;
	}
	
	public DateFieldBuilder withMaxDateValidator(Date maxDate){
		addValidator(ValidationUtils.maxDateValidator(getCaption(), maxDate));
		return this;
	}
	
	public DateFieldBuilder withDateRangeValidator(Date minDate, Date maxDate){
		addValidator(ValidationUtils.dateRangeValidator(getCaption(), minDate, maxDate));
		return this;
	}
	
}
