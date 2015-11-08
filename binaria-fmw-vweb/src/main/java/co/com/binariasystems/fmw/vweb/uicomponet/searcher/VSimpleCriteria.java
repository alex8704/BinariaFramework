package co.com.binariasystems.fmw.vweb.uicomponet.searcher;

import co.com.binariasystems.fmw.entity.criteria.SingleValueCompareCriteria;

import com.vaadin.data.Property;

public class VSimpleCriteria<T> implements VCriteria{
	private SingleValueCompareCriteria criteria;
	private Property<T> property;
	
	protected VSimpleCriteria(SingleValueCompareCriteria criteria, Property<T> property){
		this.criteria = criteria;
		this.property = property;
	}
	
	public SingleValueCompareCriteria getCriteria() {
		return criteria;
	}
	public void setCriteria(SingleValueCompareCriteria criteria) {
		this.criteria = criteria;
	}
	public Property<T> getProperty() {
		return property;
	}
	public void setProperty(Property<T> property) {
		this.property = property;
	}
	
}
