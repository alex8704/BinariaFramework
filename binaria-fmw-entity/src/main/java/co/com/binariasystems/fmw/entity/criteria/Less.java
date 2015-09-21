package co.com.binariasystems.fmw.entity.criteria;

public class Less extends SingleValueCompareCriteria {
	private Less(){
		super(null, null);
	}
	protected Less(String entityField, Object value){
		super(entityField, value);
	}
}
