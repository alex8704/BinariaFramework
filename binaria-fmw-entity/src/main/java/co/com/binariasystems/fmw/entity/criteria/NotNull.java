package co.com.binariasystems.fmw.entity.criteria;

public class NotNull extends NullabilityCriteria {
	private NotNull(){
		super(null);
	}
	protected NotNull(String entityField){
		super(entityField);
	}
}
