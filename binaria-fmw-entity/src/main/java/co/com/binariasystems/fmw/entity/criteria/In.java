package co.com.binariasystems.fmw.entity.criteria;

import java.util.Collection;

public class In extends SingleValueOnSetCriteria {
	private In(){
		super(null, null);
	}
	protected In(String entityField, Collection<Object> targetCollection){
		super(entityField, targetCollection);
	}
}
