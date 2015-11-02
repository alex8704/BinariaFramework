package co.com.binariasystems.fmw.entity.util;

public interface FMWEntityConstants {
	public static final String ENTITY_DYNASQL_MAIN_ALIAS = "as_class";
	public static final String LIKE_SQLCOMPARING_COMIDIN_CHAR = "*";
	
	//Id con el cual se obtendra del contexto de IOC la ruta del archivo de personalizacion de Entities
	public static final String ENTITY_OPERATIONS_SHOWSQL_IOC_KEY = "entity.crudOperatios.showsql";
	public static final String ENTITY_CONVENTION_FORM_TITLE_FMT = "entity.{0}.form.title";
	public static final String ENTITY_CONVENTION_LABELS_FMT = "entity.{0}.{1}.caption";
}
