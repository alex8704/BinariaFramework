package co.com.binariasystems.fmw.vweb.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;

public class ValidationUtils {
	
	public static final String REQUIRED_MESSAGE_TEMPLATE_KEY = "validation.required_message.template";
	//Value of {0} field is mandatory
	public static final String VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY = "validation.value.atleast_message.template";
	//Value of {0} field must be at least {minVal}
	public static final String VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY = "validation.value.maximumof_message.template";
	//Value of {0} field must be less or equal than {maxValue}
	public static final String VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY = "validation.value.between_message.template";
	//Value of {0} field must be between {min} and {max}
	public static final String LENGTH_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY = "validation.length.atleast_message.template";
	//Value of {0} field must have at least {minVal} characters
	public static final String LENGTH_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY = "validation.length.maximumof_message.template";
	//Value of {0} field must have maximum {maxValue} characters
	public static final String LENGTH_BETWEEN_MESSAGE_TEMPLATE_KEY = "validation.length.between_message.template";
	//Value of {0} field must have between {min} and {max} characters
	public static final String EMAIL_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.emailformat_message.template";
	//Value of {0} field must have an email valid format
	public static final String REGEXP_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.regexpformat_message.template";
	//Value of {0} field has an invalid format
	public static final String REGEXP_WITH_EXAMPLE_FORMAT_MESSAGE_TEMPLATE_KEY = "validation.regexpexampleformat_message.template";
	//Value of {0} must be a format like {1}
	
	public static final String VALIDATION_ERROR_WINDOW_TITLE = "validation.error_window.title";
	
	private static boolean initialized;
	
	private static MessageFormat NULL_VALID_MF;
	private static MessageFormat MINDATE_VALID_MF;
	private static MessageFormat MAXDATE_VALID_MF;
	private static MessageFormat DATERANGE_VALID_MF;
	private static MessageFormat MININTEGER_VALID_MF;
	private static MessageFormat MAXINTEGER_VALID_MF;
	private static MessageFormat INTEGERRANGE_VALID_MF;
	private static MessageFormat MINDOUBLE_VALID_MF;
	private static MessageFormat MAXDOUBLE_VALID_MF;
	private static MessageFormat DOUBLERANGE_VALID_MF;
	private static MessageFormat MINSTRING_LENGTH_VALID_MF;
	private static MessageFormat MAXSTRING_LENGTH_VALID_MF;
	private static MessageFormat STRING_LENGTH_RANGE_VALID_MF;
	private static MessageFormat EMAIL_VALID_MF;
	private static MessageFormat REGEXP_VALID_MF;
	private static MessageFormat REGEXP_VALID_EXAMPLE_MF;
	
	private static void ensureInit(){
		if(!initialized){
			NULL_VALID_MF = new MessageFormat(VWebUtils.getCommonString(REQUIRED_MESSAGE_TEMPLATE_KEY));
			MINDATE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY));
			MAXDATE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY));
			DATERANGE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY));
			MININTEGER_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY));
			MAXINTEGER_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY));
			INTEGERRANGE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY));
			MINDOUBLE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY));
			MAXDOUBLE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY));
			DOUBLERANGE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY));
			MINSTRING_LENGTH_VALID_MF = new MessageFormat(VWebUtils.getCommonString(LENGTH_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY));
			MAXSTRING_LENGTH_VALID_MF = new MessageFormat(VWebUtils.getCommonString(LENGTH_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY));
			STRING_LENGTH_RANGE_VALID_MF = new MessageFormat(VWebUtils.getCommonString(LENGTH_BETWEEN_MESSAGE_TEMPLATE_KEY));
			EMAIL_VALID_MF = new MessageFormat(VWebUtils.getCommonString(EMAIL_FORMAT_MESSAGE_TEMPLATE_KEY));
			REGEXP_VALID_MF = new MessageFormat(VWebUtils.getCommonString(REGEXP_FORMAT_MESSAGE_TEMPLATE_KEY));
			REGEXP_VALID_EXAMPLE_MF = new MessageFormat(VWebUtils.getCommonString(REGEXP_WITH_EXAMPLE_FORMAT_MESSAGE_TEMPLATE_KEY));
			initialized = true;
		}
	}
	
	
	public static Validator nullValidator(String fieldCaption){
		ensureInit();
		return new NullValidator(NULL_VALID_MF.format(new Object[]{fieldCaption}), false);
	}
	
	public static Validator minDateValidator(String fieldCaption, Date minDate){
		ensureInit();
		String expectedValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		return new DateRangeValidator(MINDATE_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), minDate, null, null);
	}
	
	public static Validator maxDateValidator(String fieldCaption, Date maxDate){
		ensureInit();
		String expectedValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		return new DateRangeValidator(MAXDATE_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), null, maxDate, null);
	}
	
	public static Validator dateRangeValidator(String fieldCaption, Date minDate, Date maxDate){
		ensureInit();
		if(minDate != null && maxDate == null)
			return minDateValidator(fieldCaption, minDate);
		if(minDate == null && maxDate != null)
			return maxDateValidator(fieldCaption, maxDate);
		String expectedMinValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		String expectedMaxValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		return new DateRangeValidator(DATERANGE_VALID_MF.format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minDate, maxDate, null);
	}
	
	public static Validator minIntegerValidator(String fieldCaption, Integer minValue){
		ensureInit();
		String expectedValue = minValue == null ? "#" : String.valueOf(minValue);
		return new IntegerRangeValidator(MININTEGER_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), minValue, null);
	}
	
	public static Validator maxIntegerValidator(String fieldCaption, Integer maxValue){
		ensureInit();
		String expectedValue = maxValue == null ? "#" : String.valueOf(maxValue);
		return new IntegerRangeValidator(MAXINTEGER_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), null, maxValue);
	}
	
	public static Validator integerRangeValidator(String fieldCaption, Integer minValue, Integer maxValue){
		ensureInit();
		if(minValue != null && (maxValue == null || maxValue == Integer.MAX_VALUE))
			return minIntegerValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null && maxValue != Integer.MAX_VALUE)
			return maxIntegerValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null || maxValue == Integer.MAX_VALUE ? "#" : String.valueOf(maxValue);
		return new IntegerRangeValidator(INTEGERRANGE_VALID_MF.format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minValue, maxValue);
	}
	
	public static Validator minDoubleValidator(String fieldCaption, Double minValue){
		ensureInit();
		String expectedValue = minValue == null ? "#" : String.valueOf(minValue);
		return new DoubleRangeValidator(MINDOUBLE_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), minValue, null);
	}
	
	public static Validator maxDoubleValidator(String fieldCaption, Double maxValue){
		ensureInit();
		String expectedValue = maxValue == null ? "#" : String.valueOf(maxValue);
		return new DoubleRangeValidator(MAXDOUBLE_VALID_MF.format(new Object[]{fieldCaption, expectedValue}), null, maxValue);
	}
	
	public static Validator doubleRangeValidator(String fieldCaption, Double minValue, Double maxValue){
		ensureInit();
		if(minValue != null && (maxValue == null || maxValue == Double.MAX_VALUE))
			return minDoubleValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null && maxValue != Double.MAX_VALUE)
			return maxDoubleValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null || maxValue == Double.MAX_VALUE ? "#" : String.valueOf(maxValue);
		return new DoubleRangeValidator(DOUBLERANGE_VALID_MF.format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minValue, maxValue);
	}
	
	public static Validator minStringLengthValidator(String fieldCaption, Integer minLength){
		ensureInit();
		String expectedLength = minLength == null ? "#" : String.valueOf(minLength);
		return new StringLengthValidator(MINSTRING_LENGTH_VALID_MF.format(new Object[]{fieldCaption, expectedLength}), minLength, null, true);
	}
	
	public static Validator maxStringLengthValidator(String fieldCaption, Integer maxLength){
		ensureInit();
		String expectedLength = maxLength == null ? "#" : String.valueOf(maxLength);
		return new StringLengthValidator(MAXSTRING_LENGTH_VALID_MF.format(new Object[]{fieldCaption, expectedLength}), null, maxLength, true);
	}
	
	public static Validator stringLengthRangeValidator(String fieldCaption, Integer minLength, Integer maxLength){
		ensureInit();
		if(minLength != null && (maxLength == null || maxLength == Integer.MAX_VALUE))
			return minStringLengthValidator(fieldCaption, minLength);
		if(minLength == null && maxLength != null && maxLength != Integer.MAX_VALUE)
			return maxStringLengthValidator(fieldCaption, maxLength);
		
		String expectedMinLength = minLength == null ? "#" : String.valueOf(minLength);
		String expectedMaxLength = maxLength == null || maxLength == Integer.MAX_VALUE? "#" : String.valueOf(maxLength);
		return new StringLengthValidator(STRING_LENGTH_RANGE_VALID_MF.format(new Object[]{fieldCaption, expectedMinLength, expectedMaxLength}), minLength, maxLength, true);
	}
	
	public static Validator emailValidator(String fieldCaption){
		ensureInit();
		return new EmailValidator(EMAIL_VALID_MF.format(new Object[]{fieldCaption}));
	}
	
	public static Validator regexpValidator(String fieldCaption, String regexp, String sample){
		ensureInit();
		if(StringUtils.isEmpty(sample))
			return new RegexpValidator(regexp, REGEXP_VALID_MF.format(new Object[]{fieldCaption}));
		return new RegexpValidator(regexp, REGEXP_VALID_EXAMPLE_MF.format(new Object[]{fieldCaption, sample}));
	}
	
	public static String requiredErrorFor(String fieldCaption){
		ensureInit();
		return NULL_VALID_MF.format(new Object[]{fieldCaption});
	}
	
	public static String dateRangeErrorFor(String fieldCaption, Date minDate, Date maxDate){
		ensureInit();
		String expectedMinValue = minDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(minDate);
		String expectedMaxValue = maxDate == null ? FMWConstants.TIMESTAMP_DEFAULT_FORMAT : new SimpleDateFormat(FMWConstants.TIMESTAMP_DEFAULT_FORMAT).format(maxDate);
		if(minDate != null && maxDate == null)
			return MINDATE_VALID_MF.format(new Object[]{fieldCaption, expectedMinValue});
		if(minDate == null && maxDate != null)
			return MAXDATE_VALID_MF.format(new Object[]{fieldCaption, expectedMaxValue});
		return DATERANGE_VALID_MF.format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue});
	}
	
	
	/*
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
		if(minValue != null && maxValue == null)
			return minIntegerValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null)
			return maxIntegerValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null ? "#" : String.valueOf(maxValue);
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
		if(minValue != null && maxValue == null)
			return minDoubleValidator(fieldCaption, minValue);
		if(minValue == null && maxValue != null)
			return maxDoubleValidator(fieldCaption, maxValue);
		String expectedMinValue = minValue == null ? "#" : String.valueOf(minValue);
		String expectedMaxValue = maxValue == null ? "#" : String.valueOf(maxValue);
		return new DoubleRangeValidator(new MessageFormat(VWebUtils.getCommonString(VALUE_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinValue, expectedMaxValue}), minValue, maxValue);
	}
	
	public static Validator minStringLengthValidator(String fieldCaption, Integer minLength){
		String expectedLength = minLength == null ? "#" : String.valueOf(minLength);
		return new StringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_AT_LEAST_THAN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedLength}), minLength, null, true);
	}
	
	public static Validator maxStringLengthValidator(String fieldCaption, Integer maxLength){
		String expectedLength = maxLength == null ? "#" : String.valueOf(maxLength);
		return new StringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_MAXIMUM_OF_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedLength}), null, maxLength, true);
	}
	
	public static Validator stringLengthRangeValidator(String fieldCaption, Integer minLength, Integer maxLength){
		if(minLength != null && maxLength == null)
			return minStringLengthValidator(fieldCaption, minLength);
		if(minLength == null && maxLength != null)
			return maxStringLengthValidator(fieldCaption, maxLength);
		
		String expectedMinLength = minLength == null ? "#" : String.valueOf(minLength);
		String expectedMaxLength = maxLength == null ? "#" : String.valueOf(maxLength);
		return new StringLengthValidator(new MessageFormat(VWebUtils.getCommonString(LENGTH_BETWEEN_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption, expectedMinLength, expectedMaxLength}), minLength, maxLength, true);
	}
	
	public static Validator emailValidator(String fieldCaption){
		return new EmailValidator(new MessageFormat(VWebUtils.getCommonString(EMAIL_FORMAT_MESSAGE_TEMPLATE_KEY)).format(new Object[]{fieldCaption}));
	}
	
	public static Validator regexpVallidator(String fieldCaption, String regexp, String sample){
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
	}*/
}
