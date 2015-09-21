package co.com.binariasystems.fmw.entity.criteria;

public class IsNull extends NullabilityCriteria {
	private IsNull(){
		super(null);
	}
	protected IsNull(String entityField){
		super(entityField);
	}
}
