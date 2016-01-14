package co.com.binariasystems.fmw.constants;

public interface FMWConstants {
	final String PIPE = "|";
	final String WHITE_SPACE = " ";
	final String LPARENTHESIS = "(";
	final String RPARENTHESIS = ")";
	final String COMMA = ",";
	final String DEFAULT_NULL_REPRESENTATION = "-";
	final String LINE_SEP = System.getProperty("line.separator");
	
	//Id con el cual se obtendra del contexto de IOC la clase usada para invocar Class.getResource() y evitar
	//Errores de recursos no encontrados por problemas de Classloader y Classpath
	final String DEFAULT_LOADER_CLASS = "application.default.resorceLoaderClass"; 
	
	String TIMESTAMP_DEFAULT_FORMAT = "dd/MM/yyyy HH:mm";
	String DATE_DEFAULT_FORMAT = "dd/MM/yyyy";
	String TIMESTAMP_NAMEDDAY_FORMAT = "EEEE, dd MMMM yyyy HH:mm";
	String DATE_NAMEDDAY_FORMAT = "EEEE, dd MMMM yyyy";
	String SHORT_TIME_FORMAT = "HH:mm";
	String MEDIANE_TIME_FORMAT = "HH:mm:ss";
	String LARGE_TIME_FORMAT = "hh:mm:ss a";
	
	String TIMESTAMP_SECONDS_FORMAT = "dd/MM/yyyy HH:mm:ss";
	String TIMESTAMP_FULL_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";
	
	String FLOAT_POINT_NUMBER_DEFAULT_FORMAT = "#,###.00";
	String INT_NUMBER_DEFAULT_FORMAT = "#,###";
}
