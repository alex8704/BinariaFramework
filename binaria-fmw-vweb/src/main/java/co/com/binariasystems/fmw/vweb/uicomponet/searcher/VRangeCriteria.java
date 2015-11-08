package co.com.binariasystems.fmw.vweb.uicomponet.searcher;

import co.com.binariasystems.fmw.entity.criteria.ValueRangeCriteria;

import com.vaadin.data.Property;

public class VRangeCriteria<T> implements VCriteria{
	private ValueRangeCriteria<T> criteria;
	private Property<T> minProperty;
	private Property<T> maxProperty;
	private Class<T> targetClazz;
	
	
	protected VRangeCriteria(ValueRangeCriteria<T> criteria, Property<T> minProperty, Property<T> maxProperty, Class<T> targetClazz){
		this.criteria = criteria;
		this.minProperty = minProperty;
		this.maxProperty = maxProperty;
		this.targetClazz = targetClazz;
	}

	public ValueRangeCriteria<T> getCriteria() {
		return criteria;
	}

	public void setCriteria(ValueRangeCriteria<T> criteria) {
		this.criteria = criteria;
	}

	public Property<T> getMinProperty() {
		return minProperty;
	}

	public void setMinProperty(Property<T> minProperty) {
		this.minProperty = minProperty;
	}

	public Property<T> getMaxProperty() {
		return maxProperty;
	}

	public void setMaxProperty(Property<T> maxProperty) {
		this.maxProperty = maxProperty;
	}

	public Class<T> getTargetClazz() {
		return targetClazz;
	}

	public void setTargetClazz(Class<T> targetClazz) {
		this.targetClazz = targetClazz;
	}
	
	
}
