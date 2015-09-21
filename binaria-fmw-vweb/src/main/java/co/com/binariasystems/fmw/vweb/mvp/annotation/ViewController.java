package co.com.binariasystems.fmw.vweb.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ViewController {
	
	@Target(value=ElementType.METHOD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface OnLoad {
	}
	
	@Target(value=ElementType.METHOD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface OnUnLoad {
	}
}
