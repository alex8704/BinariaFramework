package co.com.binariasystems.fmw.vweb.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface View {
	boolean isPublic() default false;
	String url();
	String messages() default "";
	Class controller() default NullController.class;
	boolean viewStringsByConventions() default false;
	
	@Target(value = ElementType.TYPE)
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface Root{
		String contentSetterMethod() default "setContent";
	}
	
	public static class NullController{
	}
}
