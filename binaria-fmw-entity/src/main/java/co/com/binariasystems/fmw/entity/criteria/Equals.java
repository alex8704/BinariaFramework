package co.com.binariasystems.fmw.entity.criteria;

public class Equals extends SingleValueCompareCriteria {
	private Equals(){
		super(null, null);
	}
	protected Equals(String entityField, Object value){
		super(entityField, value);
	}
}
