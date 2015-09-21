package co.com.binariasystems.fmw.entity.criteria;

public class Or extends MultipleGroupedCriteria {
	private Or(){
		super();
	}
	protected Or(Criteria... criteriaCollection){
		super(criteriaCollection);
	}
}
