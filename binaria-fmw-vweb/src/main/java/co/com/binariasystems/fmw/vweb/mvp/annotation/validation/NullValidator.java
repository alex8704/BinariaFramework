package co.com.binariasystems.fmw.vweb.mvp.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value=ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface NullValidator {
	String fieldCaption() default "";
}
