package co.com.binariasystems.fmw.vweb.servlet;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class DBMigrationContextListener implements ServletContextListener {
	@Resource()
	protected DataSource dataSource;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Data Source -----------------------------------------------------------------");
		System.out.println("dataSource == null? "+ (dataSource == null));
		System.out.println("Data Source -----------------------------------------------------------------");
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
