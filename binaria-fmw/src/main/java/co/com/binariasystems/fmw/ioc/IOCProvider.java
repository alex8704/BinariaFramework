package co.com.binariasystems.fmw.ioc;


public interface IOCProvider {
    public <T extends Object> T getBean(String beanId, Class<T> clazz);
    public <T extends Object> T getBean(Class<T> clazz);
    public Object getBean(String beanId);
}
