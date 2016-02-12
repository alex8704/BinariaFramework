package co.com.binariasystems.fmw.util.db;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSQLSeverRunner{
	private static final Logger LOGGER = LoggerFactory.getLogger(HSQLSeverRunner.class);
	private static final String DEFAULT_DIRECTORY = System.getProperty("user.home");
	private Properties serverConfig;
	private static final int DEFAULT_PORT = 9001;
	private String dataBasesDirectory;
	private List<String> databaseNames;
	private int serverPort;
	private Server server;
	private boolean serverStarted;
	
	public void startServer() throws IOException{
		if(serverStarted) return;
		if(serverConfig == null)buildServerConfig();;
		HsqlProperties serverProps = new HsqlProperties(serverConfig);
		server = new Server();
		try {
			server.setProperties(serverProps);
		} catch (IOException | AclFormatException e) {
			LOGGER.error("Error configuring HSQL Server properties", e);
			return;
		}
		server.start();
		serverStarted = !serverStarted;
	}
	
	
	private void buildServerConfig(){
		dataBasesDirectory = StringUtils.defaultIfBlank(dataBasesDirectory, DEFAULT_DIRECTORY);
		serverConfig = new Properties();
		serverConfig.setProperty("server.no_system_exit", "true");
		serverConfig.setProperty("server.port", String.valueOf((serverPort <= 0) ? DEFAULT_PORT : serverPort));
		int dbIdx = 0;
		for(String  dbName : databaseNames){
			serverConfig.setProperty("server.database."+(dbIdx++), "file:"+dataBasesDirectory+"/"+dbName+"/db");
			serverConfig.setProperty("server.dbname."+(dbIdx++), dbName);
		}
		
	}
	
	public void stopServer(){
		if(!serverStarted)return;
		server.shutdown();
		serverStarted = !serverStarted;
	}


	/**
	 * @return the serverConfig
	 */
	public Properties getServerConfig() {
		return serverConfig;
	}


	/**
	 * @param serverConfig the serverConfig to set
	 */
	public void setServerConfig(Properties serverConfig) {
		this.serverConfig = serverConfig;
	}


	/**
	 * @return the dataBasesDirectory
	 */
	public String getDataBasesDirectory() {
		return dataBasesDirectory;
	}


	/**
	 * @param dataBasesDirectory the dataBasesDirectory to set
	 */
	public void setDataBasesDirectory(String dataBasesDirectory) {
		this.dataBasesDirectory = dataBasesDirectory;
	}


	/**
	 * @return the databaseNames
	 */
	public List<String> getDatabaseNames() {
		return databaseNames;
	}


	/**
	 * @param databaseNames the databaseNames to set
	 */
	public void setDatabaseNames(List<String> databaseNames) {
		this.databaseNames = databaseNames;
	}


	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}


	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	
}
