package co.com.binariasystems.fmw.ioc;

public class IOCHelper {
	private static IOCProvider defaultIOC;
	
	public static void setDefault(IOCProvider iocProvider){
		defaultIOC = iocProvider;
	}
	
    public static <T extends Object> T getBean(String beanId, Class<T> clazz){
    	return defaultIOC.getBean(beanId, clazz);
    }
    public static <T extends Object> T getBean(Class<T> clazz){
    	return defaultIOC.getBean(clazz);
    }
    public static Object getBean(String beanId){
    	return defaultIOC.getBean(beanId);
    }

}
