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
import com.vaadin.ui.InlineDateField;

public class DatePickerBuilder extends InlineDateField {
	
	public DatePickerBuilder() {
		super();
		setDefaults();
	}

	public DatePickerBuilder(Property dataSource) throws IllegalArgumentException {
		super(dataSource);
		setDefaults();
	}


	public DatePickerBuilder(String caption, Date value) {
		super(caption, value);
		setDefaults();
	}


	public DatePickerBuilder(String caption, Property dataSource) {
		super(caption, dataSource);
		setDefaults();
	}


	public DatePickerBuilder(String caption) {
		super(caption);
		setDefaults();
	}


	private void setDefaults(){
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		setInvalidCommitted(true);
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	
	public DatePickerBuilder withClosedRange(Date minValue, Date maxValue){
		setRangeStart(minValue);
		setRangeEnd(maxValue);
		setDateOutOfRangeMessage(ValidationUtils.dateRangeErrorFor(getCaption(), minValue, maxValue));
		return this;
	}
	
	public DatePickerBuilder secondResolution(){
		setResolution(Resolution.SECOND);
		return this;
	}
	
	public DatePickerBuilder minuteResolution(){
		setResolution(Resolution.MINUTE);
		return this;
	}
	
	public DatePickerBuilder hourResolution(){
		setResolution(Resolution.HOUR);
		return this;
	}
	
	public DatePickerBuilder dayResolution(){
		setResolution(Resolution.HOUR);
		return this;
	}
	
	public DatePickerBuilder monthResolution(){
		setResolution(Resolution.MONTH);
		return this;
	}
	
	public DatePickerBuilder yearResolution(){
		setResolution(Resolution.YEAR);
		return this;
	}
	
	public DatePickerBuilder birthDateRestrict(){
		setRangeEnd(new Date());
		return this;
	}
	
	public DatePickerBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public DatePickerBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public DatePickerBuilder readOnly(){
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
	
	public DatePickerBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public DatePickerBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public DatePickerBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public DatePickerBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public DatePickerBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public DatePickerBuilder withValue(Date value){
		setValue(value);
		return this;
	}
	
	public DatePickerBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public DatePickerBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public DatePickerBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public DatePickerBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public DatePickerBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public DatePickerBuilder withMinDateValidator(Date minDate){
		addValidator(ValidationUtils.minDateValidator(getCaption(), minDate));
		return this;
	}
	
	public DatePickerBuilder withMaxDateValidator(Date maxDate){
		addValidator(ValidationUtils.maxDateValidator(getCaption(), maxDate));
		return this;
	}
	
	public DatePickerBuilder withDateRangeValidator(Date minDate, Date maxDate){
		addValidator(ValidationUtils.dateRangeValidator(getCaption(), minDate, maxDate));
		return this;
	}
}
