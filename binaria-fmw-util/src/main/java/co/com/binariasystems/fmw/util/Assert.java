package co.com.binariasystems.fmw.util;

public class Assert {
	public static void notNull(Object object, String message){
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
