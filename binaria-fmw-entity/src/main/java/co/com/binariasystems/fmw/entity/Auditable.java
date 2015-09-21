package co.com.binariasystems.fmw.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Auditable {
	public abstract String creationUserField() default "creationUser";
	public abstract String modificationUserField() default "modificationUser";
	public abstract String creationDateField() default "creationDate";
	public abstract String modificationDateField() default "modificationDate";
}
