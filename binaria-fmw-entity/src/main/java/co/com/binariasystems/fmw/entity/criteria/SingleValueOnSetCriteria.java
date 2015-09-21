package co.com.binariasystems.fmw.entity.criteria;

import java.util.Collection;

public abstract class SingleValueOnSetCriteria implements Criteria {
	protected String entityField;
	protected Collection<Object> targetCollection;
	
	protected SingleValueOnSetCriteria(String entityField, Collection<Object> targetCollection){
		this.entityField = entityField;
		this.targetCollection = targetCollection;
	}
	
	public String getEntityField() {
		return entityField;
	}
	
	public Collection<Object> getTargetCollection() {
		return targetCollection;
	}
}
