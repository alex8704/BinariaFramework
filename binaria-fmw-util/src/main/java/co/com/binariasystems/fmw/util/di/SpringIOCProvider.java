package co.com.binariasystems.fmw.util.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import co.com.binariasystems.fmw.ioc.IOCProvider;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;

public class SpringIOCProvider implements IOCProvider{
	private static final Logger LOGGER  =  LoggerFactory.getLogger(SpringIOCProvider.class);
	private static IOCProvider instance;
	private ApplicationContext appCtx;
	
	public static IOCProvider configure(Object iocContext) {
		synchronized (SpringIOCProvider.class) {
			if(instance == null)
				instance = new SpringIOCProvider();
			((SpringIOCProvider)instance).appCtx = (ApplicationContext)iocContext;
		}
		return instance;
	}

	public <T> T getBean(String beanId, Class<T> clazz) {
		try{
			return appCtx.getBean(beanId, clazz);
		}catch(Exception ex){
			LOGGER.error(FMWExceptionUtils.prettyMessageException(ex).getMessage(), ex);
			return null;
		}
		
	}

	public <T> T getBean(Class<T> clazz) {
		try{
			return appCtx.getBean(clazz);
		}catch(Exception ex){
			LOGGER.error(FMWExceptionUtils.prettyMessageException(ex).getMessage(), ex);
			return null;
		}
	}

	public Object getBean(String beanId) {
		try{
			return appCtx.getBean(beanId);
		}catch(Exception ex){
			LOGGER.error(FMWExceptionUtils.prettyMessageException(ex).getMessage(), ex);
			return null;
		}
	}
}
