package co.com.binariasystems.fmw.util.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);
	public enum DBMS{MYSQL,POSTGRES,SQLSERVER,ORACLE,HSQLDB,UNSUPPORTED}
	private static DBMS currentDBMS;
	private static final String STANDARD_SQL_DATETIME_FMT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String STANDARD_SQL_DATE_FMT = "yyyy-MM-dd";
	private static boolean initialized;
	
	public static void init(DataSource dataSource){
		if(initialized)return;
		String dbName = null;
        try {
            dbName = dataSource.getConnection().getMetaData().getDatabaseProductName();
            LOGGER.info("{}.init(''{}'')", DBUtil.class.getSimpleName(), dbName);
        } catch (SQLException ex) {
        	LOGGER.error("Error on {}.init(<DataSource>)", ex);
        }
        
        try{
            if(dbName.toLowerCase().contains("mysql"))
            	currentDBMS = DBMS.MYSQL;
            else if(dbName.toLowerCase().contains("postgre"))
            	currentDBMS = DBMS.POSTGRES;
            else if(dbName.toLowerCase().contains("microsoft"))
            	currentDBMS = DBMS.SQLSERVER;
            else if(dbName.toLowerCase().contains("oracle"))
            	currentDBMS = DBMS.ORACLE;
            else if(dbName.toLowerCase().contains("hsql"))
            	currentDBMS = DBMS.HSQLDB;
            else
            	currentDBMS = DBMS.UNSUPPORTED;
            initialized = true;
        }catch(Exception ex){
        	currentDBMS = null;
        }
	}
	
	public static String dateToSqlDateFormat(Date date){
		if(date == null)return null;
		return new SimpleDateFormat(STANDARD_SQL_DATE_FMT).format(date);
	}
	
	public static String dateToSqlTimestampFormat(Date date){
		if(date == null)return null;
		return new SimpleDateFormat(STANDARD_SQL_DATETIME_FMT).format(date);
	}
	
	public static String sqlQuotedValue(String phrase){
		return "'"+StringUtils.defaultIfEmpty(phrase, "")+"'";
	}
	
	public static DBMS getCurrentDBMS(){
		return currentDBMS != null ? currentDBMS : DBMS.UNSUPPORTED;
	}
}
