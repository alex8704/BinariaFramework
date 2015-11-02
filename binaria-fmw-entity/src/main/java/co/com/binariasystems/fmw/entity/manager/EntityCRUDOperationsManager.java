package co.com.binariasystems.fmw.entity.manager;

import static co.com.binariasystems.fmw.entity.criteria.CriteriaUtils.instanceOfMultipleGroupedCriteria;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.cfg.EnumKeyProperty;
import co.com.binariasystems.fmw.entity.cfg.PKGenerationStrategy;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.criteria.CriteriaUtils;
import co.com.binariasystems.fmw.entity.criteria.MultipleGroupedCriteria;
import co.com.binariasystems.fmw.entity.criteria.NullabilityCriteria;
import co.com.binariasystems.fmw.entity.criteria.SingleValueCompareCriteria;
import co.com.binariasystems.fmw.entity.criteria.SingleValueOnSetCriteria;
import co.com.binariasystems.fmw.entity.criteria.ValueRangeCriteria;
import co.com.binariasystems.fmw.entity.dao.EntityCRUDDAO;
import co.com.binariasystems.fmw.entity.exception.EntityCRUDValidationException;
import co.com.binariasystems.fmw.entity.util.EntityRowMapper;
import co.com.binariasystems.fmw.entity.util.FMWEntityConstants;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.entity.validator.EntityValidator;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.db.DBUtil;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;

/*
 * Clase que Gestiona las operaciones a realizar sobre las Entidades definidas como maestros autoadministrados
 * Expone las operaciones CRUD sobre cada Entidad, accede a la capa de datos por medio de un DAO (Data Access Object) Generico,
 * manejado por Spring.
 */

@SuppressWarnings("rawtypes")
public class EntityCRUDOperationsManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityCRUDOperationsManager.class);
	private static Map<Class<?>, EntityCRUDOperationsManager> entityCRUDMgrContext = new HashMap<Class<?>, EntityCRUDOperationsManager>();
	private static final String SQL_XML_FILE = "entitycruddao.xml";
	private static final PropertiesManager mm = PropertiesManager.forPath(EntityCRUDDAO.class.getPackage().getName() + "." + SQL_XML_FILE, EntityCRUDDAO.class);

	private EntityCRUDOperationsManager() {
	}

	private EntityValidator entityValidator;
	private EntityConfigData entityConfigData;
	private EntityConfigurator configurator;
	private EntityCRUDDAO dao;

	public static enum CRUDOperation {
		INSERT, UPDATE, DELETE
	}

	public static EntityCRUDOperationsManager getInstance(Class<?> entityClazz) {
		EntityCRUDOperationsManager resp = entityCRUDMgrContext.get(entityClazz);
		if (resp == null) {
			synchronized (EntityCRUDOperationsManager.class) {
				try {
					resp = createInstance(entityClazz);
				} catch (Exception e) {
					throw new FMWUncheckedException(e);
				}
				entityCRUDMgrContext.put(entityClazz, resp);
			}
		}
		return resp;
	}

	private static EntityCRUDOperationsManager createInstance(Class entityClazz) throws Exception {
		EntityCRUDOperationsManager resp = new EntityCRUDOperationsManager();
		resp.configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClazz);
		resp.entityConfigData = resp.configurator.configure();
		resp.dao = IOCHelper.getBean(EntityCRUDDAO.class);

		if (resp.entityConfigData.getValidationClass() != null) {
			resp.entityValidator = resp.entityConfigData.getValidationClass().getConstructor().newInstance();
		}

		return resp;
	}

	public Object save(Object entityBean) throws Exception {
		applyValidations(entityBean, CRUDOperation.INSERT);
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder colNamesBuilder = new StringBuilder();
		StringBuilder namedParamsBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		sqlBuilder.append("insert into ").append(entityConfigData.getTable()).append("(");
		String propertyName = null;
		boolean first = true;
		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);

			if (fieldName.equals(entityConfigData.getPkFieldName()) && configurator.getPKGenerationStrategy() == PKGenerationStrategy.IDENTITY)
				continue;

			if (fieldName.equals(entityConfigData.getPkFieldName())) {

				colNamesBuilder.append(first ? "" : ", ").append(fieldCfg.getColumnName());
				if (configurator.getPKGenerationStrategy() == PKGenerationStrategy.SEQUENCE) {
					String seqTemplate = mm.getString(DBUtil.getCurrentDBMS().name().toLowerCase() + ".sequence.next.sentence.template");
					String seqName = "seq_" + entityConfigData.getTable();
					namedParamsBuilder.append(first ? "" : ", ").append("(").append(MessageFormat.format(seqTemplate, seqName)).append(")");
				}
				if (configurator.getPKGenerationStrategy() == PKGenerationStrategy.MAX_QUERY) {
					String maxQueryTemplate = mm.getString("entitycrud.max_query");
					namedParamsBuilder.append(first ? "" : ", ").append("(").append(MessageFormat.format(maxQueryTemplate, fieldCfg.getColumnName(), entityConfigData.getTable())).append(")");
				}
				first = false;
			} else {
				if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
					EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
					EntityConfigData mtd_cfgData = mtd_configurator.configure();
					propertyName = fieldCfg.getFieldName() + "." + mtd_cfgData.getPkFieldName();
				}

				Object fieldValue = PropertyUtils.getNestedProperty(entityBean, fieldName);

				if(fieldCfg.isEnumType() && fieldValue != null)
					fieldValue = configurator.getEnumKeyProperty().equals(EnumKeyProperty.ORDINAL) ? ((Enum)fieldValue).ordinal() : ((Enum)fieldValue).name();
				else if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && fieldValue != null) {
					fieldValue = PropertyUtils.getNestedProperty(entityBean, propertyName);
				}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && fieldValue != null)
					fieldValue = ((Listable)fieldValue).getPK();
				
				if (fieldValue != null) {
					colNamesBuilder.append(first ? "" : ", ").append(fieldCfg.getColumnName());
					namedParamsBuilder.append(first ? "" : ", ").append(":").append(fieldName);
					paramSource.addValue(fieldName, fieldValue);
					first = false;
				}
			}
		}

		sqlBuilder.append(colNamesBuilder.toString()).append(") values(").append(namedParamsBuilder.toString()).append(")");
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("INSERT_SQL: {" + sqlBuilder.toString() + "}");
		dao.save(sqlBuilder.toString(), paramSource);
		ListPage<Object> postSearch = search(entityBean, -1, 1, null);
		return postSearch.getRowCount() > 0 ? postSearch.getData().get(postSearch.getData().size() - 1) : entityBean;
	}

	public void edit(Object entityBean) throws Exception {
		applyValidations(entityBean, CRUDOperation.UPDATE);
		StringBuilder sqlBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		sqlBuilder.append("update ").append(entityConfigData.getTable()).append(" set ");
		boolean first = true;
		String propertyName = null;
		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);

			if (fieldName.equals(entityConfigData.getPkFieldName())) {
				paramSource.addValue(fieldCfg.getFieldName(), PropertyUtils.getNestedProperty(entityBean, fieldName));
				continue;
			}

			sqlBuilder.append(first ? "" : ", ").append(fieldCfg.getColumnName()).append(" = :").append(fieldCfg.getFieldName());

			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				propertyName = fieldCfg.getFieldName() + "." + mtd_cfgData.getPkFieldName();
			}

			Object fieldValue = PropertyUtils.getNestedProperty(entityBean, fieldName);
			
			if(fieldCfg.isEnumType() && fieldValue != null)
				fieldValue = configurator.getEnumKeyProperty().equals(EnumKeyProperty.ORDINAL) ? ((Enum)fieldValue).ordinal() : ((Enum)fieldValue).name();
			else if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && fieldValue != null) {
				fieldValue = PropertyUtils.getNestedProperty(entityBean, propertyName);
			}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && fieldValue != null)
				fieldValue = ((Listable)fieldValue).getPK();

			paramSource.addValue(fieldCfg.getFieldName(), fieldValue);
			first = false;
		}
		FieldConfigData pkField = entityConfigData.getFieldsData().get(entityConfigData.getPkFieldName());
		sqlBuilder.append(" where ").append(pkField.getColumnName()).append(" = :").append(pkField.getFieldName());
		
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("UPDATE_SQL: {" + sqlBuilder.toString() + "}");
		dao.edit(sqlBuilder.toString(), paramSource);
	}

	private List<String> getColumnsForSQLSearchStatementFromEntity(Class entityClazz, String prefix, boolean includeRelations, String aliasPrefix) throws FMWException {
		List<String> resp = new LinkedList<String>();
		EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClazz);
		EntityConfigData mtd_cfgData = mtd_configurator.configure();

		for (String fieldName : mtd_cfgData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = mtd_cfgData.getFieldsData().get(fieldName);
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && includeRelations) {
				List<String> joinCols = getColumnsForSQLSearchStatementFromEntity(((RelationFieldConfigData) fieldCfg).getRelationEntityClass(), ((RelationFieldConfigData) fieldCfg).getQueryAlias(), false, fieldCfg.getFieldName());
				resp.addAll(joinCols);
				continue;
			}

			if (StringUtils.isEmpty(prefix))
				resp.add(fieldCfg.getColumnName()+" "+fieldCfg.getFieldName());
			else
				resp.add(new StringBuilder(prefix).append(".").append(fieldCfg.getColumnName()).append(" ").append(StringUtils.isEmpty(aliasPrefix) ? "" : aliasPrefix + "_").append(fieldCfg.getFieldName()).toString());
		}

		return resp;
	}

	public ListPage<Object> search(Object entityBean, int offset, int rowsByPage, Criteria conditions) throws Exception {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		sqlBuilder.append("select ");

		List<String> queryColumns = getColumnsForSQLSearchStatementFromEntity(entityConfigData.getEntityClass(), FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS, true, null);
		boolean first = true;
		for (String columnName : queryColumns) {
			sqlBuilder.append(first ? "" : ", ").append(columnName);
			first = false;
		}
		sqlBuilder.append(" from ").append(entityConfigData.getTable()).append(" ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS);

		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				sqlBuilder.append(" left join ").append(mtd_cfgData.getTable()).append(" ").append(((RelationFieldConfigData) fieldCfg).getQueryAlias());
				sqlBuilder.append(" on(").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(" = ");
				sqlBuilder.append(((RelationFieldConfigData) fieldCfg).getQueryAlias()).append(".").append(mtd_cfgData.getFieldsData().get(mtd_cfgData.getPkFieldName()).getColumnName()).append(")");

			}
		}

		first = true;
		String propertyName = null;
		Object fieldValue = null;
		String comparingOperator = null;
		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);

			if (fieldName.equals(entityConfigData.getPkFieldName()))
				continue;

			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				propertyName = fieldCfg.getFieldName() + "." + mtd_cfgData.getPkFieldName();
			}

			fieldValue = PropertyUtils.getNestedProperty(entityBean, fieldName);
			
			if (TypeHelper.isNumericType(fieldCfg.getFieldType())) {
				if (fieldValue == null || (Number.class.isAssignableFrom(fieldValue.getClass()) && ((Number) fieldValue).doubleValue() <= 0))
					continue;
				else if (fieldValue != null && fieldValue.getClass().isPrimitive() && (double)fieldValue <= 0)
					continue;
			}

			if(fieldCfg.isEnumType() && fieldValue != null)
				fieldValue = configurator.getEnumKeyProperty().equals(EnumKeyProperty.ORDINAL) ? ((Enum)fieldValue).ordinal() : ((Enum)fieldValue).name();
			else if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && fieldValue != null) {
				fieldValue = PropertyUtils.getNestedProperty(entityBean, propertyName);
			}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && fieldValue != null)
				fieldValue = ((Listable)fieldValue).getPK();

			if (fieldValue instanceof CharSequence && StringUtils.isEmpty((CharSequence) fieldValue))
				continue;

			if (fieldValue != null) {
				comparingOperator = determineComparisonOperator(fieldValue, fieldCfg);
				if (whereBuilder.length() > 0)
					whereBuilder.append(" and ");
				whereBuilder.append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(comparingOperator).append(":").append(fieldCfg.getFieldName());

				if (CharSequence.class.isAssignableFrom(fieldValue.getClass()))
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue.toString().replace(FMWEntityConstants.LIKE_SQLCOMPARING_COMIDIN_CHAR, "%"));
				else
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue);
			}
		}

		if (conditions != null) {
			String criteriaStatements = buildCriteriaStatements(entityConfigData, conditions);
			if (!StringUtils.isBlank(criteriaStatements))
				whereBuilder.append(whereBuilder.length() == 0 ? "" : " and ").append(criteriaStatements);
		}

		if (whereBuilder.length() > 0)
			sqlBuilder.append(" where ").append(whereBuilder.toString());

		sqlBuilder.append(" order by ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(entityConfigData.getFieldsData().get(entityConfigData.getPkFieldName()).getColumnName()).append(" asc");
		
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("SEARCH_SQL: {" + sqlBuilder.toString() + "}");
		return dao.search(sqlBuilder.toString(), paramSource, offset, rowsByPage, new EntityRowMapper(entityConfigData, configurator));
	}

	public void delete(Object entityBean) throws Exception {
		applyValidations(entityBean, CRUDOperation.DELETE);
		StringBuilder sqlBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		FieldConfigData pkField = entityConfigData.getFieldsData().get(entityConfigData.getPkFieldName());

		sqlBuilder.append("delete from ").append(entityConfigData.getTable()).append(" where ").append(pkField.getColumnName()).append(" = :").append(pkField.getFieldName());
		paramSource.addValue(pkField.getFieldName(), PropertyUtils.getNestedProperty(entityBean, pkField.getFieldName()));
		
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("DELETE_SQL: {" + sqlBuilder.toString() + "}");
		dao.delete(sqlBuilder.toString(), paramSource);
	}

	/**
	 * Similar al metodo {@link #search(Object, int, int, Criteria)}, pero
	 * teniendo en cuenta para la Busqueda los campos que representan la Clave
	 * Primario de la Entidad a Buscar. Este metodo se ha creado especialmente
	 * para ser usado por componentes del Framework
	 * 
	 * @param entityBean
	 * @param offset
	 * @param rowsByPage
	 * @return
	 * @throws Exception
	 */
	public ListPage<Object> searchForFmwComponent(Object entityBean, int offset, int rowsByPage, Criteria conditions) throws Exception {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		sqlBuilder.append("select ");

		List<String> queryColumns = getColumnsForSQLSearchStatementFromEntity(entityConfigData.getEntityClass(), FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS, true, null);
		boolean first = true;
		for (String columnName : queryColumns) {
			sqlBuilder.append(first ? "" : ", ").append(columnName);
			first = false;
		}
		sqlBuilder.append(" from ").append(entityConfigData.getTable()).append(" ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS);

		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				sqlBuilder.append(" left join ").append(mtd_cfgData.getTable()).append(" ").append(((RelationFieldConfigData) fieldCfg).getQueryAlias());
				sqlBuilder.append(" on(").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(" = ");
				sqlBuilder.append(((RelationFieldConfigData) fieldCfg).getQueryAlias()).append(".").append(mtd_cfgData.getFieldsData().get(mtd_cfgData.getPkFieldName()).getColumnName()).append(")");

			}
		}

		String propertyName = null;
		Object fieldValue = null;
		String comparingOperator = null;
		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				propertyName = fieldCfg.getFieldName() + "." + mtd_cfgData.getPkFieldName();
			}

			fieldValue = PropertyUtils.getNestedProperty(entityBean, fieldName);

			if (TypeHelper.isNumericType(fieldCfg.getFieldType())) {
				if (fieldValue == null || (Number.class.isAssignableFrom(fieldValue.getClass()) && ((Number) fieldValue).doubleValue() <= 0))
					continue;
				else if (fieldValue != null && fieldValue.getClass().isPrimitive() && (double)fieldValue <= 0)
					continue;
			}
			
			if(fieldCfg.isEnumType() && fieldValue != null)
				fieldValue = configurator.getEnumKeyProperty().equals(EnumKeyProperty.ORDINAL) ? ((Enum)fieldValue).ordinal() : ((Enum)fieldValue).name();
			else if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && fieldValue != null) {
				fieldValue = PropertyUtils.getNestedProperty(entityBean, propertyName);
			}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && fieldValue != null)
				fieldValue = ((Listable)fieldValue).getPK();

			if (fieldValue instanceof CharSequence && StringUtils.isEmpty((CharSequence) fieldValue))
				continue;

			if (fieldValue != null) {
				comparingOperator = determineComparisonOperator(fieldValue, fieldCfg);
				if (whereBuilder.length() > 0)
					whereBuilder.append(" and ");
				whereBuilder.append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(comparingOperator).append(":").append(fieldCfg.getFieldName());

				if (CharSequence.class.isAssignableFrom(fieldValue.getClass()))
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue.toString().replace(FMWEntityConstants.LIKE_SQLCOMPARING_COMIDIN_CHAR, "%"));
				else
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue);
			}
		}

		if (conditions != null) {
			String criteriaStatements = buildCriteriaStatements(entityConfigData, conditions);
			if (!StringUtils.isBlank(criteriaStatements))
				whereBuilder.append(whereBuilder.length() == 0 ? "" : " and ").append(criteriaStatements);
		}

		if (whereBuilder.length() > 0)
			sqlBuilder.append(" where ").append(whereBuilder.toString());

		sqlBuilder.append(" order by ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(entityConfigData.getFieldsData().get(entityConfigData.getPkFieldName()).getColumnName()).append(" asc");
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("SEARCH_SQL: {" + sqlBuilder.toString() + "}");
		
		return dao.search(sqlBuilder.toString(), paramSource, offset, rowsByPage, new EntityRowMapper(entityConfigData, configurator));
	}

	/**
	 * Realiza busquedas para una Entidad determinada, sin realizar paginacion,
	 * perfecto para llenar componentes de lista de seleccion, con entidades
	 * cuyos registros no superen las decenas
	 * 
	 * @param searchDTO
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 * @throws FMWException 
	 * @throws Exception
	 */
	public List<Object> searchWithoutPaging(Object searchDTO) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FMWException{
		Object entityBean = searchDTO != null ? searchDTO : ConstructorUtils.invokeConstructor(entityConfigData.getEntityClass());
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		sqlBuilder.append("select ");

		List<String> queryColumns = getColumnsForSQLSearchStatementFromEntity(entityConfigData.getEntityClass(), FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS, true, null);
		boolean first = true;
		for (String columnName : queryColumns) {
			sqlBuilder.append(first ? "" : ", ").append(columnName);
			first = false;
		}
		sqlBuilder.append(" from ").append(entityConfigData.getTable()).append(" ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS);

		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				sqlBuilder.append(" left join ").append(mtd_cfgData.getTable()).append(" ").append(((RelationFieldConfigData) fieldCfg).getQueryAlias());
				sqlBuilder.append(" on(").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(" = ");
				sqlBuilder.append(((RelationFieldConfigData) fieldCfg).getQueryAlias()).append(".").append(mtd_cfgData.getFieldsData().get(mtd_cfgData.getPkFieldName()).getColumnName()).append(")");

			}
		}

		first = true;
		String propertyName = null;
		Object fieldValue = null;
		String comparingOperator = null;
		for (String fieldName : entityConfigData.getFieldsData().keySet()) {
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);

			if (fieldName.equals(entityConfigData.getPkFieldName()))
				continue;

			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				EntityConfigurator mtd_configurator = EntityConfigurationManager.getInstance().getConfigurator(((RelationFieldConfigData) fieldCfg).getRelationEntityClass());
				EntityConfigData mtd_cfgData = mtd_configurator.configure();
				propertyName = fieldCfg.getFieldName() + "." + mtd_cfgData.getPkFieldName();
			}

			fieldValue = PropertyUtils.getNestedProperty(entityBean, fieldName);
			
			if (TypeHelper.isNumericType(fieldCfg.getFieldType())) {
				if (fieldValue == null || (Number.class.isAssignableFrom(fieldValue.getClass()) && ((Number) fieldValue).doubleValue() <= 0))
					continue;
				else if (fieldValue != null && fieldValue.getClass().isPrimitive() && (double)fieldValue <= 0)
					continue;
			}
			
			if(fieldCfg.isEnumType() && fieldValue != null)
				fieldValue = configurator.getEnumKeyProperty().equals(EnumKeyProperty.ORDINAL) ? ((Enum)fieldValue).ordinal() : ((Enum)fieldValue).name();
			else if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()) && fieldValue != null) {
				fieldValue = PropertyUtils.getNestedProperty(entityBean, propertyName);
			}else if(Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && fieldValue != null)
				fieldValue = ((Listable)fieldValue).getPK();

			if (fieldValue instanceof CharSequence && StringUtils.isEmpty((CharSequence) fieldValue))
				continue;

			if (fieldValue != null) {
				comparingOperator = determineComparisonOperator(fieldValue, fieldCfg);
				if (whereBuilder.length() > 0)
					whereBuilder.append(" and ");
				whereBuilder.append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(fieldCfg.getColumnName()).append(comparingOperator).append(":").append(fieldCfg.getFieldName());

				if (CharSequence.class.isAssignableFrom(fieldValue.getClass()))
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue.toString().replace(FMWEntityConstants.LIKE_SQLCOMPARING_COMIDIN_CHAR, "%"));
				else
					paramSource.addValue(fieldCfg.getFieldName(), fieldValue);
			}
		}

		if (whereBuilder.length() > 0)
			sqlBuilder.append(" where ").append(whereBuilder.toString());

		sqlBuilder.append(" order by ").append(FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS).append(".").append(entityConfigData.getFieldsData().get(entityConfigData.getPkFieldName()).getColumnName()).append(" asc");
		
		if(FMWEntityUtils.showOpeationsSql())
			LOGGER.info("SEARCH_WITHOUT_PAGING_SQL: {" + sqlBuilder.toString() + "}");
		
		return dao.searchWithoutPaging(sqlBuilder.toString(), paramSource, new EntityRowMapper(entityConfigData, configurator));
	}

	private String buildCriteriaStatements(EntityConfigData configData, Criteria criteria) {
		if (criteria == null)
			return "";

		StringBuilder resp = new StringBuilder();

		if (instanceOfMultipleGroupedCriteria(criteria)) {
			String operator = CriteriaUtils.getCriteriaOperator(criteria);
			boolean first = true;
			String subStatement = null;
			for (Criteria subCriteria : ((MultipleGroupedCriteria) criteria).getCriteriaCollection()) {
				subStatement = buildCriteriaStatements(configData, subCriteria);
				if (StringUtils.isBlank(subStatement))
					continue;
				if (!first)
					resp.append(FMWConstants.WHITE_SPACE).append(operator).append(FMWConstants.WHITE_SPACE);
				resp.append(subStatement);
				first = false;
			}
		} else {
			FieldConfigData fieldCfg = null;
			String entityField = null;
			String columnName = null;
			if (criteria instanceof NullabilityCriteria)
				entityField = ((NullabilityCriteria) criteria).getEntityField();
			else if (criteria instanceof SingleValueCompareCriteria)
				entityField = ((SingleValueCompareCriteria) criteria).getEntityField();
			else if (criteria instanceof SingleValueOnSetCriteria)
				entityField = ((SingleValueOnSetCriteria) criteria).getEntityField();
			else if (criteria instanceof ValueRangeCriteria)
				entityField = ((ValueRangeCriteria) criteria).getEntityField();

			fieldCfg = configData.getFieldsData().get(entityField);

			if (fieldCfg == null)
				throw new FMWUncheckedException("Cannon find '" + entityField + "' on Entyty class " + configData.getEntityClass() + ", for build criteria conditions");

			columnName = FMWEntityConstants.ENTITY_DYNASQL_MAIN_ALIAS + "." + fieldCfg.getColumnName();
			if (fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType())) {
				((RelationFieldConfigData) fieldCfg).getClass();
			}
			resp.append(CriteriaUtils.buildSingleValueSQLCondition(criteria, columnName));
		}

		return resp.toString();
	}

	@SuppressWarnings("unchecked")
	private void applyValidations(Object entityBean, CRUDOperation operation) throws EntityCRUDValidationException {
		if (entityValidator == null)
			return;
		if (operation == CRUDOperation.INSERT)
			entityValidator.beforeInsert(entityBean);
		else if (operation == CRUDOperation.UPDATE)
			entityValidator.beforeUpdate(entityBean);
		else if (operation == CRUDOperation.DELETE)
			entityValidator.beforeDelete(entityBean);
	}

	
	private String determineComparisonOperator(Object fieldValue, FieldConfigData fieldCfg){
		return !Listable.class.isAssignableFrom(fieldCfg.getFieldType()) && CharSequence.class.isAssignableFrom(fieldValue.getClass()) ? " like " : " = ";
	}

}
