package co.com.binariasystems.fmw.entity.criteria;

public class BetweenNumbers extends ValueRangeCriteria<Number>{

	protected BetweenNumbers(Number minValue, Number maxValue,String entityField) {
		super(minValue, maxValue, entityField);
	}

}
