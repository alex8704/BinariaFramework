package co.com.binariasystems.fmw.ioc;


public interface IOCProvider {
    public <T> T getBean(String beanId, Class<T> clazz);
    public <T> T getBean(Class<T> clazz);
    public Object getBean(String beanId);
}
