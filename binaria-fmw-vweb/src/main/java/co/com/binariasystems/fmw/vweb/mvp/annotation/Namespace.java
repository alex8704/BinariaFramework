package co.com.binariasystems.fmw.vweb.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.PACKAGE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Namespace {
	String path();
	String messages() default "";
}
