package co.com.binariasystems.fmw.vweb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.util.db.HSQLSeverRunner;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;

public class HSQLSeverRunListener implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(HSQLSeverRunListener.class);
	public static final String BASE_DIRECTORY = "hsqlRunner.dataBasesDirectory";
	public static final String DATABASE_NAMES = "hsqlRunner.databaseNames";
	public static final String SERVER_PORT = "hsqlRunner.serverPort";
	public static final String SERVER_CONFIG_FILE = "hsqlRunner.serverConfigFile";
	private HSQLSeverRunner serverRunner;
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(serverRunner != null)
			serverRunner.stopServer();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String configFileParam = sce.getServletContext().getInitParameter(SERVER_CONFIG_FILE);
		serverRunner = new HSQLSeverRunner();
		if(StringUtils.isBlank(configFileParam)){
			serverRunner.setDataBasesDirectory(sce.getServletContext().getInitParameter(BASE_DIRECTORY));
			serverRunner.setServerPort(Integer.parseInt(StringUtils.defaultIfBlank(sce.getServletContext().getInitParameter(SERVER_PORT), "0")));
			String databaseNamesParam = StringUtils.defaultString(sce.getServletContext().getInitParameter(DATABASE_NAMES));
			StringTokenizer dbNamesTokenizer = new StringTokenizer(databaseNamesParam, ",");
			List<String> dbNames = (dbNamesTokenizer.countTokens() > 0) ? new ArrayList<String>() : null;
			while(dbNamesTokenizer.hasMoreTokens()){
				dbNames.add(dbNamesTokenizer.nextToken());
			}
			serverRunner.setDatabaseNames(dbNames);
		}else{
			Properties serverConfig = new Properties();
			try{
				if(configFileParam.endsWith(".xml"))
					serverConfig.loadFromXML(HSQLSeverRunListener.class.getResourceAsStream(configFileParam));
				else
					serverConfig.load(HSQLSeverRunListener.class.getResourceAsStream(configFileParam));
				serverRunner.setServerConfig(serverConfig);
			}catch(IOException ex){
				LOGGER.error("Has ocurred an configuring HSQL Server", ex);
				return;
			}
		}
		try{
			serverRunner.startServer();
			waitServerStart();
		}catch(IOException ex){
			LOGGER.error("Canno't start HSQL server from application: "+FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
		
	}
	
	private void waitServerStart(){
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {}
	}
}
