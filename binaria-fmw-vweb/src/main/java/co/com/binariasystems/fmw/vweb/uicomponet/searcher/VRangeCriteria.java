package co.com.binariasystems.fmw.vweb.uicomponet.searcher;

import co.com.binariasystems.fmw.entity.criteria.ValueRangeCriteria;

import com.vaadin.data.Property;

public class VRangeCriteria implements VCriteria{
	private ValueRangeCriteria criteria;
	private Property minProperty;
	private Property maxProperty;
	private Class targetClazz;
	
	
	protected VRangeCriteria(ValueRangeCriteria criteria, Property minProperty, Property maxProperty, Class targetClazz){
		this.criteria = criteria;
		this.minProperty = minProperty;
		this.maxProperty = maxProperty;
		this.targetClazz = targetClazz;
	}

	public ValueRangeCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ValueRangeCriteria criteria) {
		this.criteria = criteria;
	}

	public Property getMinProperty() {
		return minProperty;
	}

	public void setMinProperty(Property minProperty) {
		this.minProperty = minProperty;
	}

	public Property getMaxProperty() {
		return maxProperty;
	}

	public void setMaxProperty(Property maxProperty) {
		this.maxProperty = maxProperty;
	}

	public Class getTargetClazz() {
		return targetClazz;
	}

	public void setTargetClazz(Class targetClazz) {
		this.targetClazz = targetClazz;
	}
	
	
}
