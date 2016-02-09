package co.com.binariasystems.fmw.entity.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.com.binariasystems.fmw.entity.validator.EntityValidator;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CRUDViewConfig {
	public abstract SearcherConfig searcherConfig() default @SearcherConfig;
	public abstract Class<? extends EntityValidator> validationClass() default EntityValidator.class;
	public abstract String titleKey() default "";
	public abstract boolean deleteEnabled() default true;
	public abstract boolean isAuditable() default false;
	public abstract String creationUserField() default "creationUser";
	public abstract String modificationUserField() default "modificationUser";
	public abstract String creationDateField() default "creationDate";
	public abstract String modificationDateField() default "modificationDate";
	public abstract String messagesFilePath() default "";
}
