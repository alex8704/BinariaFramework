package co.com.binariasystems.fmw.entity.criteria;

import java.util.Date;

public class BetweenDates extends ValueRangeCriteria<Date>{

	protected BetweenDates(Date minValue, Date maxValue, String entityField) {
		super(minValue, maxValue, entityField);
	}

}
