package co.com.binariasystems.fmw.entity.criteria;

public abstract class NullabilityCriteria implements Criteria {
	protected String entityField;
	
	protected NullabilityCriteria(String entityField){
		this.entityField = entityField;
	}

	public String getEntityField() {
		return entityField;
	}

}
