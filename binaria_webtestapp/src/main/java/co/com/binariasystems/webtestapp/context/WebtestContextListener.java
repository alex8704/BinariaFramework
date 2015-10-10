package co.com.binariasystems.webtestapp.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.util.FMWEntityConstants;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.db.DBUtil;
import co.com.binariasystems.fmw.util.di.SpringIOCProvider;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.DefaultViewProvider;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewConfigurationException;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.ViewProvider;

/**
 * Application Lifecycle Listener implementation class WebtestContextListener
 *
 */
public class WebtestContextListener implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebtestContextListener.class);
    /**
     * Default constructor. 
     */
    public WebtestContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	IOCHelper.setDefault(SpringIOCProvider.configure(WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())));
    	DBUtil.init(IOCHelper.getBean("mainDS", DataSource.class));
    	Class resourceLoaderClass = IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class);
    	String entitiesStringsFilePath = IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class);
    	String entityOperatiosShowSql = IOCHelper.getBean(FMWEntityConstants.ENTITY_OPERATIONS_SHOWSQL_IOC_KEY, String.class);
    	
    	LOGGER.info(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY + ": " + resourceLoaderClass);
    	LOGGER.info(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY + ": " + entitiesStringsFilePath);
    	LOGGER.info(FMWEntityConstants.ENTITY_OPERATIONS_SHOWSQL_IOC_KEY + ": " + entityOperatiosShowSql);
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    }
	
}
