package co.com.binariasystems.fmw.entity.criteria;

import java.util.Collection;

public class NotIn extends SingleValueOnSetCriteria {
	private NotIn(){
		super(null, null);
	}
	protected NotIn(String entityField, Collection<Object> targetCollection){
		super(entityField, targetCollection);
	}
}
