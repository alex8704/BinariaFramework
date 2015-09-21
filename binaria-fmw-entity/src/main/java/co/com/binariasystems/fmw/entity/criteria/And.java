package co.com.binariasystems.fmw.entity.criteria;

public class And extends MultipleGroupedCriteria {
	private And(){
		super();
	}
	protected And(Criteria... criteriaCollection){
		super(criteriaCollection);
	}
}
