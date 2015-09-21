package co.com.binariasystems.fmw.vweb.uicomponet.searcher;

import java.util.Collection;
import java.util.Date;

import co.com.binariasystems.fmw.entity.criteria.And;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.criteria.CriteriaUtils;
import co.com.binariasystems.fmw.entity.criteria.Equals;
import co.com.binariasystems.fmw.entity.criteria.Greater;
import co.com.binariasystems.fmw.entity.criteria.Less;
import co.com.binariasystems.fmw.entity.criteria.Like;
import co.com.binariasystems.fmw.entity.criteria.MultipleGroupedCriteria;
import co.com.binariasystems.fmw.entity.criteria.NotEquals;
import co.com.binariasystems.fmw.entity.criteria.NotLike;
import co.com.binariasystems.fmw.entity.criteria.Or;
import co.com.binariasystems.fmw.entity.criteria.SingleValueCompareCriteria;
import co.com.binariasystems.fmw.entity.criteria.ValueRangeCriteria;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.reflec.TypeHelper;

import com.vaadin.data.Property;

public class VCriteriaUtils {

	public static Criteria and(Criteria... criteriaCollection) {
		return CriteriaUtils.and(criteriaCollection);
	}

	public static Criteria or(Criteria... criteriaCollection) {
		return CriteriaUtils.or(criteriaCollection);
	}

	public static Criteria equals(String entityField, Object value) {
		return CriteriaUtils.equals(entityField, value);
	}

	public static Criteria equals(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) equals(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria notEquals(String entityField, Object value) {
		return CriteriaUtils.notEquals(entityField, value);
	}

	public static Criteria notEquals(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) notEquals(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria greater(String entityField, Object value) {
		return CriteriaUtils.greater(entityField, value);
	}

	public static Criteria greater(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) greater(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria less(String entityField, Object value) {
		return CriteriaUtils.less(entityField, value);
	}

	public static Criteria less(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) less(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria in(String entityField, Collection<Object> targetCollection) {
		return CriteriaUtils.in(entityField, targetCollection);
	}

	public static Criteria notIn(String entityField, Collection<Object> targetCollection) {
		return CriteriaUtils.notIn(entityField, targetCollection);
	}

	public static Criteria isNull(String entityField) {
		return CriteriaUtils.isNull(entityField);
	}

	public static Criteria isNotNull(String entityField) {
		return CriteriaUtils.isNotNull(entityField);
	}

	public static Criteria between(String entityField, Date minValue, Date maxValue) {
		return CriteriaUtils.between(entityField, minValue, maxValue);
	}

	public static Criteria between(String entityField, Number minValue, Number maxValue) {
		return CriteriaUtils.between(entityField, minValue, maxValue);
	}

	public static Criteria between(String entityField, Property minValueProperty, Property maxValueProperty, Class targetClazz) {
		if (targetClazz == null || (!TypeHelper.isDateOrTimeType(targetClazz) && !TypeHelper.isNumericType(targetClazz)))
			throw new FMWUncheckedException("Argument 'targetClazz' of method " + VCriteriaUtils.class.getSimpleName() + ".between(String, Property, Property, Clazz) must be a Date or Number type");
		if (TypeHelper.isDateOrTimeType(targetClazz))
			return new VRangeCriteria((ValueRangeCriteria) between(entityField, (Date) minValueProperty.getValue(), (Date) maxValueProperty.getValue()), minValueProperty, maxValueProperty, Date.class);
		return new VRangeCriteria((ValueRangeCriteria) between(entityField, (Number) minValueProperty.getValue(), (Number) maxValueProperty.getValue()), minValueProperty, maxValueProperty, Number.class);
	}

	public static Criteria like(String entityField, Object value) {
		return CriteriaUtils.like(entityField, value);
	}

	public static Criteria like(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) like(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria notLike(String entityField, Object value) {
		return CriteriaUtils.notLike(entityField, value);
	}

	public static Criteria notLike(String entityField, Property valueProperty) {
		return new VSimpleCriteria((SingleValueCompareCriteria) notLike(entityField, valueProperty.getValue()), valueProperty);
	}

	public static SingleValueCompareCriteria copyWithNewValue(SingleValueCompareCriteria criteria, Object newValue) {
		if (criteria instanceof Equals)
			return (SingleValueCompareCriteria) equals(criteria.getEntityField(), newValue);
		if (criteria instanceof NotEquals)
			return (SingleValueCompareCriteria) notEquals(criteria.getEntityField(), newValue);
		if (criteria instanceof Greater)
			return (SingleValueCompareCriteria) greater(criteria.getEntityField(), newValue);
		if (criteria instanceof Less)
			return (SingleValueCompareCriteria) less(criteria.getEntityField(), newValue);
		if (criteria instanceof Like)
			return (SingleValueCompareCriteria) like(criteria.getEntityField(), newValue);
		if (criteria instanceof NotLike)
			return (SingleValueCompareCriteria) notLike(criteria.getEntityField(), newValue);
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ValueRangeCriteria copyWithNewValues(ValueRangeCriteria criteria, Object minValue, Object maxValue, Class dataClass) {
		if (Date.class.isAssignableFrom(dataClass))
			return (ValueRangeCriteria<Date>) between(criteria.getEntityField(), (Date) minValue, (Date) maxValue);
		return (ValueRangeCriteria<Number>) between(criteria.getEntityField(), (Number) minValue, (Number) maxValue);
	}

	public static Criteria castVCriteria(Criteria currentCriteria) {
		if(currentCriteria == null) return null;
		if (currentCriteria instanceof MultipleGroupedCriteria) {
			Criteria[] multipleCriterias = new Criteria[((MultipleGroupedCriteria) currentCriteria).getCriteriaCollection().length];
			for (int i = 0; i < ((MultipleGroupedCriteria) currentCriteria).getCriteriaCollection().length; i++) {
				multipleCriterias[i] = castVCriteria(((MultipleGroupedCriteria) currentCriteria).getCriteriaCollection()[i]);
			}
			if (currentCriteria instanceof Or)
				return or(multipleCriterias);
			if (currentCriteria instanceof And)
				return and(multipleCriterias);
		} else if (currentCriteria instanceof VCriteria) {
			if (currentCriteria instanceof VSimpleCriteria)
				return ((VSimpleCriteria) currentCriteria).getProperty().getValue().equals(((VSimpleCriteria)currentCriteria).getCriteria().getValue()) ? 
						((VSimpleCriteria) currentCriteria).getCriteria() : copyWithNewValue(((VSimpleCriteria)currentCriteria).getCriteria(), ((VSimpleCriteria) currentCriteria).getProperty().getValue());
			if(currentCriteria instanceof VRangeCriteria)
				return ((VRangeCriteria) currentCriteria).getMinProperty().getValue().equals(((VRangeCriteria) currentCriteria).getCriteria().getMinValue()) &&
						((VRangeCriteria) currentCriteria).getMaxProperty().getValue().equals(((VRangeCriteria) currentCriteria).getCriteria().getMaxValue())? 
						((VRangeCriteria) currentCriteria).getCriteria() : copyWithNewValues(((VRangeCriteria) currentCriteria).getCriteria(), 
								((VRangeCriteria) currentCriteria).getMinProperty().getValue(), ((VRangeCriteria) currentCriteria).getMaxProperty().getValue(), ((VRangeCriteria) currentCriteria).getTargetClazz());
		}
		return currentCriteria;
	}

}
