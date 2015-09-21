package co.com.binariasystems.fmw.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.com.binariasystems.fmw.entity.validator.EntityValidator;

/**
 * @author Alexander Castro O.
 */

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Entity {
	public abstract String table() default "";
	public abstract Class<? extends EntityValidator> validationClass() default EntityValidator.class;
}
