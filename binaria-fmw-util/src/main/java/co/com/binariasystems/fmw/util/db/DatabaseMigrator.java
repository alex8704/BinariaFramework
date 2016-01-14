package co.com.binariasystems.fmw.util.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMigrator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrator.class);
	private Properties properties;
	private DataSource dataSource;
	private Class loaderClass;
	
	public boolean executeDatabaseMigration(){
		DBUtil.init(getDataSource());
		String dbmsMigrationDirectory = DBUtil.getCurrentDBMS().name().toLowerCase();
		boolean subpackageByDbms = StringUtils.defaultIfBlank(properties.getProperty("dbmigrator.subpackageByDbms"), "false").equals("true");
		
		String migrationsLocation = properties.getProperty("dbmigrator.baselocation") + (subpackageByDbms ? "." + dbmsMigrationDirectory : "");
		Map<String, String> placeHolders = new HashMap<String, String>();
		Flyway migrator = new Flyway();
		ClassLoader classLoader = loaderClass != null ? loaderClass.getClassLoader() : DatabaseMigrator.class.getClassLoader();
		migrator.setClassLoader(classLoader);
		migrator.setLocations(migrationsLocation);
		migrator.setSchemas(properties.getProperty("dbmigrator.schemas"));
		migrator.setPlaceholderReplacement(StringUtils.defaultIfBlank(properties.getProperty("dbmigrator.enablePlaceHolders"), "false").equals("true"));
		migrator.setPlaceholderPrefix(properties.getProperty("dbmigrator.placeholderPrefix"));
		migrator.setPlaceholderSuffix(properties.getProperty("dbmigrator.placeholderSuffix"));
		migrator.setEncoding(properties.getProperty("dbmigrator.encoding"));
		migrator.setSqlMigrationPrefix(properties.getProperty("dbmigrator.sqlMigrationPrefix"));
		migrator.setSqlMigrationSeparator(properties.getProperty("dbmigrator.sqlMigrationSeparator"));
		migrator.setSqlMigrationSuffix(properties.getProperty("dbmigrator.sqlMigrationSuffix"));
		migrator.setDataSource(getDataSource());
		for(Object objectKey : properties.keySet()){
			String key = (String)objectKey;
			if(key.startsWith("dbmigrator.placeholders.")){
				placeHolders.put(key.replace("dbmigrator.placeholders.", ""), properties.getProperty(key));
			}
		}
		migrator.setPlaceholders(placeHolders);
		try{
			migrator.migrate();
			return true;
		}catch(FlywayException ex){
			LOGGER.error("Has ocurred an unexpected error while trying execute Database migration.", ex);
			return false;
		}
		
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
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
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
	
	
}
