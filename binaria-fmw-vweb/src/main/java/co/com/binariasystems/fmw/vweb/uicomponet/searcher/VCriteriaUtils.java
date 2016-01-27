
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

	public static <T> Criteria equals(String entityField, Property<T> valueProperty) {
		Criteria auxCriteria = equals(entityField, valueProperty.getValue());
		return (auxCriteria instanceof SingleValueCompareCriteria) ? new VSimpleCriteria<T>((SingleValueCompareCriteria) auxCriteria, valueProperty) : auxCriteria;
	}

	public static Criteria notEquals(String entityField, Object value) {
		return CriteriaUtils.notEquals(entityField, value);
	}

	public static <T> Criteria notEquals(String entityField, Property<T> valueProperty) {
		Criteria auxCriteria = notEquals(entityField, valueProperty.getValue());
		return (auxCriteria instanceof SingleValueCompareCriteria) ? new VSimpleCriteria<T>((SingleValueCompareCriteria) auxCriteria, valueProperty) : auxCriteria;
	}

	public static Criteria greater(String entityField, Object value) {
		return CriteriaUtils.greater(entityField, value);
	}

	public static <T> Criteria greater(String entityField, Property<T> valueProperty) {
		return new VSimpleCriteria<T>((SingleValueCompareCriteria) greater(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria less(String entityField, Object value) {
		return CriteriaUtils.less(entityField, value);
	}

	public static <T> Criteria less(String entityField, Property<T> valueProperty) {
		return new VSimpleCriteria<T>((SingleValueCompareCriteria) less(entityField, valueProperty.getValue()), valueProperty);
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

	public static ValueRangeCriteria<Date> between(String entityField, Date minValue, Date maxValue) {
		return CriteriaUtils.between(entityField, minValue, maxValue);
	}

	public static ValueRangeCriteria<Number> between(String entityField, Number minValue, Number maxValue) {
		return CriteriaUtils.between(entityField, minValue, maxValue);
	}

	@SuppressWarnings("unchecked")
	public static <T> VRangeCriteria<T> between(String entityField, Property<T> minValueProperty, Property<T> maxValueProperty, Class<T> targetClazz) {
		if (targetClazz == null || (!TypeHelper.isDateOrTimeType(targetClazz) && !TypeHelper.isNumericType(targetClazz)))
			throw new FMWUncheckedException("Argument 'targetClazz' of method " + VCriteriaUtils.class.getSimpleName() + ".between(String, Property, Property, Clazz) must be a Date or Number type");
		if (TypeHelper.isDateOrTimeType(targetClazz))
			return new VRangeCriteria<>((ValueRangeCriteria<T>) between(entityField, (Date) minValueProperty.getValue(), (Date) maxValueProperty.getValue()), minValueProperty, maxValueProperty, targetClazz);
		return new VRangeCriteria<>((ValueRangeCriteria<T>) between(entityField, (Number) minValueProperty.getValue(), (Number) maxValueProperty.getValue()), minValueProperty, maxValueProperty, targetClazz);
	}

	public static Criteria like(String entityField, Object value) {
		return CriteriaUtils.like(entityField, value);
	}

	public static <T> Criteria like(String entityField, Property<T> valueProperty) {
		return new VSimpleCriteria<>((SingleValueCompareCriteria) like(entityField, valueProperty.getValue()), valueProperty);
	}

	public static Criteria notLike(String entityField, Object value) {
		return CriteriaUtils.notLike(entityField, value);
	}

	public static <T> Criteria notLike(String entityField, Property<T> valueProperty) {
		return new VSimpleCriteria<T>((SingleValueCompareCriteria) notLike(entityField, valueProperty.getValue()), valueProperty);
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
	public static <T> ValueRangeCriteria<T> copyWithNewValues(ValueRangeCriteria<T> criteria, Object minValue, Object maxValue, Class<T> dataClass) {
		if (Date.class.isAssignableFrom(dataClass))
			return (ValueRangeCriteria<T>) between(criteria.getEntityField(), (Date) minValue, (Date) maxValue);
		return (ValueRangeCriteria<T>) between(criteria.getEntityField(), (Number) minValue, (Number) maxValue);
	}

	@SuppressWarnings("unchecked")
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
			if (currentCriteria instanceof VSimpleCriteria){
				Object propVal = ((VSimpleCriteria<?>) currentCriteria).getProperty().getValue();
				Object currVal = ((VSimpleCriteria<?>)currentCriteria).getCriteria().getValue();
				return (propVal == null && currVal == null) || (propVal != null && currVal != null && propVal.equals(currVal)) ? 
						((VSimpleCriteria<?>) currentCriteria).getCriteria() : copyWithNewValue(((VSimpleCriteria<?>)currentCriteria).getCriteria(), ((VSimpleCriteria<?>) currentCriteria).getProperty().getValue());
			}if(currentCriteria instanceof VRangeCriteria){
				return ((VRangeCriteria<?>) currentCriteria).getMinProperty().getValue().equals(((VRangeCriteria<?>) currentCriteria).getCriteria().getMinValue()) &&
						((VRangeCriteria<?>) currentCriteria).getMaxProperty().getValue().equals(((VRangeCriteria<?>) currentCriteria).getCriteria().getMaxValue())? 
						((VRangeCriteria<?>) currentCriteria).getCriteria() : copyWithNewValues(((VRangeCriteria) currentCriteria).getCriteria(), 
								((VRangeCriteria) currentCriteria).getMinProperty().getValue(), ((VRangeCriteria) currentCriteria).getMaxProperty().getValue(), ((VRangeCriteria) currentCriteria).getTargetClazz());
			}
		}
		return currentCriteria;
	}

}
