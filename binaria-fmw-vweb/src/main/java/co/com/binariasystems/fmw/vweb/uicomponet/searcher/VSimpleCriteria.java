package co.com.binariasystems.fmw.vweb.uicomponet.searcher;

import co.com.binariasystems.fmw.entity.criteria.SingleValueCompareCriteria;

import com.vaadin.data.Property;

public class VSimpleCriteria implements VCriteria{
	private SingleValueCompareCriteria criteria;
	private Property property;
	
	protected VSimpleCriteria(SingleValueCompareCriteria criteria, Property property){
		this.criteria = criteria;
		this.property = property;
	}
	
	public SingleValueCompareCriteria getCriteria() {
		return criteria;
	}
	public void setCriteria(SingleValueCompareCriteria criteria) {
		this.criteria = criteria;
	}
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	
}
