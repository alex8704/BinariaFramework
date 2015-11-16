
package co.com.binariasystems.fmw.entity.cfg;

import java.util.HashMap;
import java.util.Map;

import co.com.binariasystems.fmw.security.auditory.AuditoryDataProvider;

/*
 * Clase que Actua como proveedor y Administrador de instancias de configuracion
 * de maestros, permite generar la configuracion y la logica de administracion de
 * cada entidad a partir de un nombre de Clase Java, actualmente se inicializa
 * con la entidades configaradas por Spring, pero en caliente administra nuevas
 * entidades Solicitadas. Guarda internamente el estado de todas las configuraciones
 * de maestros
 * 
 * @author Alexander Castro O.
 */

public class EntityConfigurationManager {
	private	static EntityConfigurationManager instance;
	private Map<String, EntityConfigurator<?>> configuratorsContext = new HashMap<String, EntityConfigurator<?>>();
	private AuditoryDataProvider<?> auditoryDataProvider;
	
	private EntityConfigurationManager(){}
	
	public static EntityConfigurationManager getInstance(){
		if(instance == null){
			synchronized (EntityConfigurationManager.class) {
				instance = new EntityConfigurationManager();
			}
		}
		return instance;
	}
	
	public void setConfiguratorsContext(Map<String, EntityConfigurator<?>> configuratorsContext){
		this.configuratorsContext = configuratorsContext;
	}
	
	@SuppressWarnings("unchecked")
	public <T> EntityConfigurator<T> getConfigurator(String entityClass){
		Class<T> clazz;
		try {
			clazz = (Class<T>) Class.forName(entityClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot load class "+entityClass, e);
		}
		
		return getConfigurator(clazz);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> EntityConfigurator<T> getConfigurator(Class<T> entityClass){
		EntityConfigurator<T> resp = (EntityConfigurator<T>) configuratorsContext.get(entityClass.getName());
		if(resp == null){
			synchronized (EntityConfigurationManager.class) {
				resp = new DefaultEntityConfigurator(entityClass);
				configuratorsContext.put(entityClass.getName(), resp);
			}
		}else if(resp.getEntityClass() == null){
			((DefaultEntityConfigurator)resp).setEntityClass(entityClass);
		}
		return resp;
	}

	public AuditoryDataProvider<?> getAuditoryDataProvider() {
		return auditoryDataProvider;
	}

	public void setAuditoryDataProvider(AuditoryDataProvider<?> auditoryDataProvider) {
		this.auditoryDataProvider = auditoryDataProvider;
	}
	
	
}
