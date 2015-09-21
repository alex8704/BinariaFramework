package co.com.binariasystems.fmw.entity.criteria;

public class NotEquals extends SingleValueCompareCriteria {
	private NotEquals(){
		super(null, null);
	}
	protected NotEquals(String entityField, Object value){
		super(entityField, value);
	}
}
