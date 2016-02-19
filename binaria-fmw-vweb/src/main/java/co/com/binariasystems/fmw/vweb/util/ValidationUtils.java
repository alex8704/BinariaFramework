package co.com.binariasystems.fmw.vweb.util;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.AddressFieldsToDTOMappingInfo;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;

public class ValidationUtils {
	//Value of {0} field is mandatory
	public static final String REQUIRED_MESSAGE_TEMPLATE_KEY = "validation.required_message.template";
	//Value of {0} field must be at least {minVal}
	public static final String VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY = "validation.value.atleast_message.template";
	//Value of {0} field must be less or equal than {maxValue}
	public static final String VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY = "validation.value.maximumof_message.template";
	//Value of {0} field must be between {min} and {max}
	public static final String VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY = "validation.value.between_message.template";
	//Value of {0} field must have at least {minVal} characters
	public static final String LENGTH_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY = "validation.length.atleast_message.template";
	//Value of {0} field must have maximum {maxValue} characters
	public static final String LENGTH_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY = "validation.length.maximumof_message.template";
	//Value of {0} field must have between {min} and {max} characters
	public static final String LENGTH_BETWEEN_MESSAGE_TEMPLATE_KEY = "validation.length.between_message.template";
	//Value of {0} field must have an email valid format
	public static final String EMAIL_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.emailformat_message.template";
	//Value of {0} field has an invalid format
	public static final String REGEXP_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.regexpformat_message.template";
	//Value of {0} must be a format like {1}
	public static final String REGEXP_WITH_EXAMPLE_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.regexpexampleformat_message.template";
	//Value of {0} must be a completed correctly
	public static final String ADDRESS_MESSAGE_TEMPLATE_KEY = "validation.addess.template";
	
	
	public static final String VALIDATION_ERROR_WINDOW_TITLE = "validation.error_window.title";
	
	
	public static Validator nullValidator(String fieldCaption){
		return new NullValidator(new MessageFormat(VWebUtils.getCommonString(REQUIRED_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption}), false);
	}
	
	public static Validator minDateValidator(String fieldCaption, Date minDate){
		String expectedValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		return new DateRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), minDate, null, null);
	}
	
	public static Validator maxDateValidator(String fieldCaption, Date maxDate){
		String expectedValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		return new DateRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), null, maxDate, null);
	}
	
	public static Validator dateRangeValidator(String fieldCaption, Date minDate, Date maxDate){
		if(minDate != null && maxDate == null)
			return minDateValidator(fieldCaption, minDate);
		if(minDate == null && maxDate != null)
			return maxDateValidator(fieldCaption, maxDate);
		String expectedMinValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		String expectedMaxValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		return new DateRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minDate, maxDate, null);
	}
	
	public static Validator minIntegerValidator(String fieldCaption, Integer minValue){
		String expectedValue = minValue == null ? "#" : String.valueOf(minValue);
		return new IntegerRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), minValue, null);
	}
	
	public static Validator maxIntegerValidator(String fieldCaption, Integer maxValue){
		String expectedValue = maxValue == null ? "#" : String.valueOf(maxValue);
		return new IntegerRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), null, maxValue);
	}
	
	public static Validator integerRangeValidator(String fieldCaption, Integer minValue, Integer maxValue){
		if(minValue != null && (maxValue == null || maxValue == Integer.MAX_VALUE))
			return minIntegerValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null && maxValue != Integer.MAX_VALUE)
			return maxIntegerValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null || maxValue == Integer.MAX_VALUE ? "#" : String.valueOf(maxValue);
		return new IntegerRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minValue, maxValue);
	}
	
	public static Validator minDoubleValidator(String fieldCaption, Double minValue){
		String expectedValue = minValue == null ? "#" : String.valueOf(minValue);
		return new DoubleRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), minValue, null);
	}
	
	public static Validator maxDoubleValidator(String fieldCaption, Double maxValue){
		String expectedValue = maxValue == null ? "#" : String.valueOf(maxValue);
		return new DoubleRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedValue}), null, maxValue);
	}
	
	public static Validator doubleRangeValidator(String fieldCaption, Double minValue, Double maxValue){
		if(minValue != null && (maxValue == null || maxValue == Double.MAX_VALUE))
			return minDoubleValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null && maxValue != Double.MAX_VALUE)
			return maxDoubleValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null || maxValue == Double.MAX_VALUE ? "#" : String.valueOf(maxValue);
		return new DoubleRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minValue, maxValue);
	}
	
	public static Validator minStringLengthValidator(String fieldCaption, Integer minLength){
		String expectedLength = minLength == null ? "#" : String.valueOf(minLength);
		return new CustomStringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedLength}), minLength, null);
	}
	
	public static Validator maxStringLengthValidator(String fieldCaption, Integer maxLength){
		String expectedLength = maxLength == null ? "#" : String.valueOf(maxLength);
		return new CustomStringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedLength}), null, maxLength);
	}
	
	public static Validator stringLengthRangeValidator(String fieldCaption, Integer minLength, Integer maxLength){
		if(minLength != null && (maxLength == null || maxLength == Integer.MAX_VALUE))
			return minStringLengthValidator(fieldCaption, minLength);
		if(minLength == null && maxLength != null && maxLength != Integer.MAX_VALUE)
			return maxStringLengthValidator(fieldCaption, maxLength);
		
		String expectedMinLength = minLength == null ? "#" : String.valueOf(minLength);
		String expectedMaxLength = maxLength == null || maxLength == Integer.MAX_VALUE? "#" : String.valueOf(maxLength);
		return new CustomStringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinLength, expectedMaxLength}), minLength, maxLength);
	}
	
	public static Validator emailValidator(String fieldCaption){
		return new EmailValidator(new MessageFormat(VWebUtils.getCommonString(EMAIL_FORMAT_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption}));
	}
	
	public static Validator regexpValidator(String fieldCaption, String regexp, String sample){
		if(StringUtils.isEmpty(sample))
			return new RegexpValidator(regexp, new MessageFormat(VWebUtils.getCommonString(REGEXP_FORMAT_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption}));
		return new RegexpValidator(regexp, new MessageFormat(VWebUtils.getCommonString(REGEXP_WITH_EXAMPLE_FORMAT_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, sample}));
	}
	
	public static String requiredErrorFor(String fieldCaption){
		return new MessageFormat(VWebUtils.getCommonString(REQUIRED_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption});
	}
	
	public static String dateRangeErrorFor(String fieldCaption, Date minDate, Date maxDate){
		String expectedMinValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		String expectedMaxValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		if(minDate != null && maxDate == null)
			return new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue});
		if(minDate == null && maxDate != null)
			return new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMaxValue});
		return new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue});
	}
	
	public static Validator addressValidator(String fieldCaption, AddressFieldsToDTOMappingInfo fieldsToDTOMapping){
		return new AddressFieldValidator(new MessageFormat(VWebUtils.getCommonString(ADDRESS_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption}), fieldsToDTOMapping);
	}
	
	public static boolean isValidatorAnnotation(Annotation annot){
		return annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.AddressValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DateRangeValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.DoubleRangeValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.EmailValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.IntRangeValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.NullValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.RegExpValidator ||
				annot instanceof co.com.binariasystems.fmw.vweb.mvp.annotation.validation.StringLengthValidator;
	}
}
