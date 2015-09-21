package co.com.binariasystems.fmw.constants;

public interface FMWConstants {
	public static final String PIPE = "|";
	public static final String WHITE_SPACE = " ";
	public static final String LPARENTHESIS = "(";
	public static final String RPARENTHESIS = ")";
	public static final String COMMA = ",";
	
	//Id con el cual se obtendra del contexto de IOC la clase usada para invocar Class.getResource() y evitar
	//Errores de recursos no encontrados por problemas de Classloader y Classpath
	public static final String APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY = "application.default.resorceLoaderClass"; 
	
	public String TIMESTAMP_DEFAULT_FORMAT = "dd/MM/yyyy HH:mm";
	public String DATE_DEFAULT_FORMAT = "dd/MM/yyyy";
	public String TIMESTAMP_NAMEDDAY_FORMAT = "EEEE, dd MMMM yyyy HH:mm";
	public String DATE_NAMEDDAY_FORMAT = "EEEE, dd MMMM yyyy";
	public String SHORT_TIME_FORMAT = "HH:mm";
	public String MEDIANE_TIME_FORMAT = "HH:mm:ss";
	public String LARGE_TIME_FORMAT = "hh:mm:ss a";
	
	public String TIMESTAMP_SECONDS_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public String TIMESTAMP_FULL_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";
}
