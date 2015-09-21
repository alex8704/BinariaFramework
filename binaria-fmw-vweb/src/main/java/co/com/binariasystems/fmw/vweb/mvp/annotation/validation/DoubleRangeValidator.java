package co.com.binariasystems.fmw.vweb.mvp.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DoubleRangeValidator {
	double min() default 0;
	double max() default Double.MAX_VALUE;
	String fieldCaption() default "";
}
