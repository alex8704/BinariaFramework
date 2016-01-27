package co.com.binariasystems.fmw.entity.criteria;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.db.DBUtil;

public class CriteriaUtils {
	
	private static final Map<Class<? extends Criteria>, String> operatorsForCriteria = new HashMap<Class<? extends Criteria>, String>();
	private static final String AND_OPER = "and";
	private static final String OR_OPER = "or";
	private static final String EQUALS_OPER = "=";
	private static final String NOTEQUALS_OPER = "<>";
	private static final String GREATER_OPER = ">";
	private static final String LESS_OPER = "<";
	private static final String IN_OPER = "in";
	private static final String NOTIN_OPER = "not in";
	private static final String ISNULL_OPER = "is null";
	private static final String NOTNULL_OPER = "is not null";
	private static final String BETWEEN_OPER = "between";
	private static final String LIKE_OPER = "like";
	private static final String NOTLIKE_OPER = "not like";
	
	static{
		operatorsForCriteria.put(And.class, AND_OPER);
		operatorsForCriteria.put(Or.class, OR_OPER);
		operatorsForCriteria.put(Equals.class, EQUALS_OPER);
		operatorsForCriteria.put(NotEquals.class, NOTEQUALS_OPER);
		operatorsForCriteria.put(Greater.class, GREATER_OPER);
		operatorsForCriteria.put(Less.class, LESS_OPER);
		operatorsForCriteria.put(In.class, IN_OPER);
		operatorsForCriteria.put(NotIn.class, NOTIN_OPER);
		operatorsForCriteria.put(IsNull.class, ISNULL_OPER);
		operatorsForCriteria.put(NotNull.class, NOTNULL_OPER);
		operatorsForCriteria.put(ValueRangeCriteria.class, BETWEEN_OPER);
		operatorsForCriteria.put(Like.class, LIKE_OPER);
		operatorsForCriteria.put(NotLike.class, NOTLIKE_OPER);
		
	}
	
	public static String getCriteriaOperator(Criteria criteria){
		if(criteria == null)
			return null;
		if(instanceOfValueRangeCriteria(criteria))
			return operatorsForCriteria.get(ValueRangeCriteria.class);
		return operatorsForCriteria.get(criteria.getClass());
	}
	

	public static Criteria and(Criteria... criteriaCollection){
		if(criteriaCollection == null || criteriaCollection.length == 0)
			throw new FMWUncheckedException("Cannot create an "+And.class.getName()+" with null nor empty criteriaCollection");
		return new And(criteriaCollection);
	}
	
	public static Criteria or(Criteria... criteriaCollection){
		if(criteriaCollection == null || criteriaCollection.length == 0)
			throw new FMWUncheckedException("Cannot create an "+Or.class.getName()+" with null nor empty criteriaCollection");
		return new Or(criteriaCollection);
	}
	
	public static Criteria equals(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+Equals.class.getName()+" with null entityField");
//		if(value == null)
//			return isNull(entityField);
			//throw new FMWUncheckedException("You shuld use "+IsNull.class.getName()+" instead of "+Equals.class.getName()+" when want to know if a value is NULL");
		return new Equals(entityField, value);
	}
	
	public static Criteria notEquals(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+NotEquals.class.getName()+" with null entityField");
//		if(value == null)
//			return isNotNull(entityField);
			//throw new FMWUncheckedException("You shuld use "+NotNull.class.getName()+" instead of "+NotEquals.class.getName()+" when want to know if a value is NOT NULL");
		return new NotEquals(entityField, value);
	}
	
	public static Criteria greater(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+Greater.class.getName()+" with null entityField");
		return new Greater(entityField, value);
	}
	
	public static Criteria less(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+Less.class.getName()+" with null entityField");
		return new Less(entityField, value);
	}
	
	public static Criteria in(String entityField, Collection<Object> targetCollection){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+In.class.getName()+" with null entityField");
		if(targetCollection == null)
			throw new FMWUncheckedException("Cannot create an "+In.class.getName()+" with null targetCollection");
		return new In(entityField, targetCollection);
	}
	
	public static Criteria notIn(String entityField, Collection<Object> targetCollection){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+NotIn.class.getName()+" with null entityField");
		if(targetCollection == null)
			throw new FMWUncheckedException("Cannot create an "+NotIn.class.getName()+" with null targetCollection");
		return new NotIn(entityField, targetCollection);
	}
	
	public static Criteria isNull(String entityField){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+IsNull.class.getName()+" with null entityField");
		return new IsNull(entityField);
	}
	
	public static Criteria isNotNull(String entityField){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+NotNull.class.getName()+" with null entityField");
		return new NotNull(entityField);
	}
	
	public static ValueRangeCriteria<Date> between(String entityField, Date minValue, Date maxValue){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+BetweenDates.class.getName()+" with null entityField");
		if(minValue == null)
			throw new FMWUncheckedException("Cannot create an "+BetweenDates.class.getName()+" with null minValue");
		if(maxValue == null)
			throw new FMWUncheckedException("Cannot create an "+BetweenDates.class.getName()+" with null maxValue");
		if(minValue.after(maxValue))
			throw new FMWUncheckedException("Cannot create an "+BetweenDates.class.getName()+" with minValue greater than maxValue");
		
		return new BetweenDates(minValue, maxValue, entityField);
	}
	
	public static ValueRangeCriteria<Number> between(String entityField, Number minValue, Number maxValue){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+BetweenNumbers.class.getName()+" with null entityField");
		if(minValue == null)
			throw new FMWUncheckedException("Cannot create an "+BetweenNumbers.class.getName()+" with null minValue");
		if(maxValue == null)
			throw new FMWUncheckedException("Cannot create an "+BetweenNumbers.class.getName()+" with null maxValue");
		if(minValue.doubleValue() > maxValue.doubleValue())
			throw new FMWUncheckedException("Cannot create an "+BetweenNumbers.class.getName()+" with minValue greater than maxValue");
		
		return new BetweenNumbers(minValue, maxValue, entityField);
	}
	
	public static Criteria like(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+Like.class.getName()+" with null entityField");
//		if(value == null)
//			return isNull(entityField);
		return new Like(entityField, value);
	}
	
	public static Criteria notLike(String entityField, Object value){
		if(StringUtils.isBlank(entityField))
			throw new FMWUncheckedException("Cannot create an "+NotLike.class.getName()+" with null entityField");
//		if(value == null)
//			return isNotNull(entityField);
		return new NotLike(entityField, value);
	}
	
	public static boolean instanceOfAnd(Criteria criteria){
		return criteria instanceof And;
	}
	
	public static boolean instanceOfOr(Criteria criteria){
		return criteria instanceof Or;
	}
	
	public static boolean instanceOfEquals(Criteria criteria){
		return criteria instanceof Equals;
	}
	
	public static boolean instanceOfNotEquals(Criteria criteria){
		return criteria instanceof NotEquals;
	}
	
	public static boolean instanceOfGreater(Criteria criteria){
		return criteria instanceof Greater;
	}
	
	public static boolean instanceOfLess(Criteria criteria){
		return criteria instanceof Less;
	}
	
	public static boolean instanceOfIn(Criteria criteria){
		return criteria instanceof In;
	}
	
	public static boolean instanceOfNotIn(Criteria criteria){
		return criteria instanceof NotIn;
	}
	
	public static boolean instanceOfIsNull(Criteria criteria){
		return criteria instanceof IsNull;
	}
	
	public static boolean instanceOfNotNull(Criteria criteria){
		return criteria instanceof NotNull;
	}
	
	public static boolean instanceOfBetweenDates(Criteria criteria){
		return criteria instanceof BetweenDates;
	}
	
	public static boolean instanceOfBetweenNumbers(Criteria criteria){
		return criteria instanceof BetweenNumbers;
	}
	
	public static boolean instanceOfLike(Criteria criteria){
		return criteria instanceof Like;
	}
	
	public static boolean instanceOfNotLike(Criteria criteria){
		return criteria instanceof NotLike;
	}
	
	public static boolean instanceOfMultipleGroupedCriteria(Criteria criteria){
		return criteria instanceof MultipleGroupedCriteria;
	}
	
	public static boolean instanceOfNullabilityCriteria(Criteria criteria){
		return criteria instanceof NullabilityCriteria;
	}
	
	public static boolean instanceOfSingleValueCompareCriteria(Criteria criteria){
		return criteria instanceof SingleValueCompareCriteria;
	}
	
	public static boolean instanceOfSingleValueOnSetCriteria(Criteria criteria){
		return criteria instanceof SingleValueOnSetCriteria;
	}
	
	public static boolean instanceOfValueRangeCriteria(Criteria criteria){
		return criteria instanceof ValueRangeCriteria;
	}
	
	public static String buildSingleValueSQLCondition(Criteria criteria, String columnName){
		if(criteria instanceof NullabilityCriteria)
			return buildSingleValueSQLCondition((NullabilityCriteria)criteria, columnName);
		else if(criteria instanceof SingleValueCompareCriteria)
			return buildSingleValueSQLCondition((SingleValueCompareCriteria)criteria, columnName);
		else if(criteria instanceof SingleValueOnSetCriteria)
			return buildSingleValueSQLCondition((SingleValueOnSetCriteria)criteria, columnName);
		else if(criteria instanceof ValueRangeCriteria)
			return buildSingleValueSQLCondition((ValueRangeCriteria<?>)criteria, columnName);
		else
			throw new FMWUncheckedException("Cannot invoke "+CriteriaUtils.class.getSimpleName()+".buildSingleValueSQLCondition for "+MultipleGroupedCriteria.class.getName()+" type");
	}
	
	public static String buildSingleValueSQLCondition(NullabilityCriteria criteria, String columnName){
		StringBuilder resp = new StringBuilder();
		resp.append(columnName).append(FMWConstants.WHITE_SPACE).append(getCriteriaOperator(criteria));
		return resp.toString();
	}
	
	public static String buildSingleValueSQLCondition(SingleValueCompareCriteria criteria, String columnName){
		StringBuilder resp = new StringBuilder();
		resp.append(columnName).append(FMWConstants.WHITE_SPACE).append(getCriteriaOperator(criteria))
		.append(FMWConstants.WHITE_SPACE).append(valueToSQLFormat(criteria.getValue()));
		return resp.toString();
	}
	
	public static String buildSingleValueSQLCondition(SingleValueOnSetCriteria criteria, String columnName){
		StringBuilder resp = new StringBuilder();
		resp.append(columnName).append(getCriteriaOperator(criteria))
		.append(FMWConstants.LPARENTHESIS);
		boolean first = true;
		for(Object value : criteria.getTargetCollection()){
			if(!first)
				resp.append(FMWConstants.COMMA);
			resp.append(valueToSQLFormat(value));
			first = false;
		}
		resp.append(FMWConstants.RPARENTHESIS);
		
		return resp.toString();
	}
	
	public static String buildSingleValueSQLCondition(ValueRangeCriteria<?> criteria, String columnName){
		StringBuilder resp = new StringBuilder();
		
		resp.append(columnName).append(FMWConstants.WHITE_SPACE).append(getCriteriaOperator(criteria))
		.append(FMWConstants.WHITE_SPACE).append(criteria.getMinValue()).append(FMWConstants.WHITE_SPACE)
		.append(AND_OPER).append(FMWConstants.WHITE_SPACE).append(criteria.getMaxValue());
		
		return resp.toString();
	}
	
	private static String valueToSQLFormat(Object value){
		if(value == null) return "null";
		String resp = null;
		if(value instanceof BigDecimal)
			resp = ((BigDecimal)value).toPlainString();
		else if(Number.class.isAssignableFrom(value.getClass()))
			resp = ((Number)value).toString();
		else if(TypeHelper.isDateOrTimeType(value.getClass()))
			resp = value instanceof Timestamp ? DBUtil.dateToSqlDateFormat((Date)value) : DBUtil.dateToSqlTimestampFormat((Date)value);
		else if(CharSequence.class.isAssignableFrom(value.getClass()) || Character.class.isAssignableFrom(value.getClass()))
			resp = DBUtil.sqlQuotedValue(StringUtils.upperCase(value.toString()));
		else if(Boolean.class.isAssignableFrom(value.getClass()))
			resp = ((Boolean)value).booleanValue() ? "1" : "0";
		else
			resp = value.toString();
		
		return resp;
	}
}
