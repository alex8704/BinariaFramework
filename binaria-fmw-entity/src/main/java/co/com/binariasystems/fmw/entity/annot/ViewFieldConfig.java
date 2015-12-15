package co.com.binariasystems.fmw.entity.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ViewFieldConfig {
	public abstract EntityConfigUIControl uiControl() default EntityConfigUIControl.DEFAULT;
	public abstract String uiLabel() default "";
	public boolean ommitUpperTransform() default false;
	public FieldValue[] fixedValues() default {};
}
