package co.com.binariasystems.fmw.entity.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Alexander Castro O.
 */

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SearcherConfig {
	public abstract String[] descriptionFields() default {};
	public abstract String[] gridColumnFields() default {};
	public abstract String searchField() default "";
}
