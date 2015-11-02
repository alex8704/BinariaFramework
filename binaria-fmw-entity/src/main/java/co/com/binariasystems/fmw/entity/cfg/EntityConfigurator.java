package co.com.binariasystems.fmw.entity.cfg;

import java.util.Map;

import co.com.binariasystems.fmw.exception.FMWException;

/*
 * Interface a implementar para definir el comportamiento de la generacion de configuracion
 * en tiempo de ejecucion, para la Administracion Automatica de Maestros Basada en POJOS/Entidades
 * Anotadas
 * 
 * @see DefaultMasterEntityConfigurator
 * @author Alexander Castro O.
 */

public interface EntityConfigurator {
	public String getTitleKey();
	public Class<?> getEntityClass();
	public EntityConfigData configure() throws FMWException;
	public EnumKeyProperty getEnumKeyProperty();
	public Map<String, String> getFieldLabelMappings();
	public Map<String, EntityConfigUIControl> getFieldUIControlMappings();
	public boolean isDeleteEnabled();
	public PKGenerationStrategy getPKGenerationStrategy();
}
