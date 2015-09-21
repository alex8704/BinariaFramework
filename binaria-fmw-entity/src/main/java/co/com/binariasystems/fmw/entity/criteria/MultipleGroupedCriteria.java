package co.com.binariasystems.fmw.entity.criteria;

public abstract class MultipleGroupedCriteria implements Criteria {
	protected Criteria[] criteriaCollection;
	
	protected MultipleGroupedCriteria(Criteria... criteriaCollection){
		this.criteriaCollection = criteriaCollection;
	}

	public Criteria[] getCriteriaCollection() {
		return criteriaCollection;
	}
}
