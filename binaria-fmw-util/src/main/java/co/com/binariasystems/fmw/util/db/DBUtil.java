package co.com.binariasystems.fmw.util.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.dto.DTOPrueba;

public class DBUtil {

	public enum DBMS{MYSQL,POSTGRES,SQLSERVER,ORACLE,HSQLDB,UNSUPPORTED}
	private static DBMS currentDBMS;
	private static final String STANDARD_SQL_DATETIME_FMT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String STANDARD_SQL_DATE_FMT = "yyyy-MM-dd";
	
	public static void init(DataSource dataSource){
		String dbName = null;
        
        try {
            dbName = dataSource.getConnection().getMetaData().getDatabaseProductName();
            System.out.println(DBUtil.class.getSimpleName()+".init('"+dbName+"')");
        } catch (SQLException ex) {
            dbName = null;
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
		return currentDBMS;
	}
	
	public static void main(String[] args) throws Exception {
		DTOPrueba dto = new DTOPrueba();
		//dto.setAnidado(new DTOAnidado());
		dto.setNombre("FRANKLIN");
		dto.setIdentificador(1200);
//		dto.getAnidado().setCantidad(25);
//		dto.getAnidado().setDescripcion(null);
//		dto.getAnidado().setValor(1300.0d);
		PropertyUtils.setNestedProperty(dto, "anidado.descripcion", "JORGE MELENDEZ");
		Object obj = PropertyUtils.getNestedProperty(dto, "anidado.descripcion");
		System.out.println("{"+obj+"}");
	}
}
