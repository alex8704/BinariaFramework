package co.com.binariasystems.fmw.entity.criteria;

public abstract class SingleValueCompareCriteria implements Criteria {
	protected String entityField;
	protected Object value;
	
	protected SingleValueCompareCriteria(String entityField, Object value){
		this.entityField = entityField;
		this.value = value;
	}
	
	public String getEntityField() {
		return entityField;
	}
	
	public Object getValue() {
		return value;
	}
}
