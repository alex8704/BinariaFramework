package co.com.binariasystems.fmw.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import co.com.binariasystems.fmw.util.resources.resources;

public class DataModelInstaller {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataModelInstaller.class);
	private boolean createIfNotExist;
	private DataSource dataSource;
	private Class loaderClass;
	private Properties configProperties;
	private boolean subpackageByDbms;
	private RowCallbackHandler dbValidationCallback = new RowCallbackHandler() {
		@Override public void processRow(ResultSet rs) throws SQLException {
			LOGGER.info("DataBase Validation Sucessfull finished. Dummy Value: '{}'", rs.getString(1));
		}
	};
	
	
	public boolean validateDataModelCreation(){
		DBUtil.init(dataSource);
		subpackageByDbms = StringUtils.defaultIfBlank(configProperties.getProperty("dbinstaller.subpackageByDbms"), "false").equals("true");
		
		if(!isDataModelAlreadyCreated()){
			if(!createIfNotExist) return false;
			String resourcePath = null;
			
			StringTokenizer scriptFilesTokenizer = new StringTokenizer(configProperties.getProperty("dbinstaller.installationScriptFiles"), ",");
			
			while(scriptFilesTokenizer.hasMoreTokens()){
				resourcePath = new StringBuilder(configProperties.getProperty("dbinstaller.baselocation")).append("/")
				.append(subpackageByDbms ? DBUtil.getCurrentDBMS().name().toLowerCase() : "").append(subpackageByDbms ? "/": "")
				.append(scriptFilesTokenizer.nextToken().trim()).toString();
				if(!runSingleScript(new ClassPathResource(resourcePath, loaderClass != null ? loaderClass : resources.class)))
					return false;
			}
		}
		return true;
	}
	
	private boolean isDataModelAlreadyCreated(){
		if(dataSource == null) return true;//Responde True para evitar interpretar error de conexion con NO creacionde BD
		String sqlStmt = configProperties.getProperty("dbinstaller.installationValidateQuery");
		try {
			new JdbcTemplate(dataSource).query(sqlStmt, dbValidationCallback);
			return true;
		} catch (DataAccessException e) {
			return false;
		}
	}
	
	private boolean runSingleScript(Resource scriptResource){
		try {
			ScriptUtils.executeSqlScript(dataSource.getConnection(), scriptResource);
			LOGGER.info("Sucessful script '"+scriptResource.getFilename()+"' execution");
		} catch (ScriptException | SQLException e) {
			LOGGER.error("Has ocurred an unexpected error while trying run script file '"+scriptResource.getFilename()+"'.", e);
			return false;
		}
		return true;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the loaderClass
	 */
	public Class getLoaderClass() {
		return loaderClass;
	}

	/**
	 * @param loaderClass the loaderClass to set
	 */
	public void setLoaderClass(Class loaderClass) {
		this.loaderClass = loaderClass;
	}

	/**
	 * @return the configProperties
	 */
	public Properties getConfigProperties() {
		return configProperties;
	}

	/**
	 * @param configProperties the configProperties to set
	 */
	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

	/**
	 * @return the createIfNotExist
	 */
	public boolean isCreateIfNotExist() {
		return createIfNotExist;
	}

	/**
	 * @param createIfNotExist the createIfNotExist to set
	 */
	public void setCreateIfNotExist(boolean createIfNotExist) {
		this.createIfNotExist = createIfNotExist;
	}

	
}
