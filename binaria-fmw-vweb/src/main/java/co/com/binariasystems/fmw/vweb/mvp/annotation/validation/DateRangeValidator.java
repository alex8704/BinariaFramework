package co.com.binariasystems.fmw.vweb.mvp.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.com.binariasystems.fmw.constants.FMWConstants;

@Target(value=ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DateRangeValidator {
	public static final String CURRENT_DATE = "today";
	String min();
	String max();
	String fieldCaption() default "";
	String parseFormat() default FMWConstants.TIMESTAMP_SECONDS_FORMAT;
}
