package co.com.binariasystems.fmw.entity.criteria;

public class Greater extends SingleValueCompareCriteria {
	private Greater(){
		super(null, null);
	}
	protected Greater(String entityField, Object value){
		super(entityField, value);
	}
}
