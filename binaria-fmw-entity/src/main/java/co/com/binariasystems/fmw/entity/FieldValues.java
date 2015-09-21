package co.com.binariasystems.fmw.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)

/**
 * @author Alexander Castro O.
 */

public @interface FieldValues {
	public abstract FieldValue[] value() default {};
}
