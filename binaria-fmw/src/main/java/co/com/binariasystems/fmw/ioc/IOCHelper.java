package co.com.binariasystems.fmw.ioc;

public class IOCHelper {
	private static IOCProvider defaultIOC;
	
	public static void setProvider(IOCProvider iocProvider){
		defaultIOC = iocProvider;
	}
	
	public static boolean isConfigured(){
		return defaultIOC != null;
	}
	
    public static <T> T getBean(String beanId, Class<T> clazz){
    	return defaultIOC.getBean(beanId, clazz);
    }
    public static <T> T getBean(Class<T> clazz){
    	return defaultIOC.getBean(clazz);
    }
    public static Object getBean(String beanId){
    	return defaultIOC.getBean(beanId);
    }

}
