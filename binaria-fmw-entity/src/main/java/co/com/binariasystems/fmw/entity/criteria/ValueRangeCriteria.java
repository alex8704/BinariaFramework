package co.com.binariasystems.fmw.entity.criteria;

public abstract class ValueRangeCriteria<T> implements Criteria{
	private T minValue;
	private T maxValue;
	private String entityField;
	
	protected ValueRangeCriteria(T minValue, T maxValue, String entityField) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.entityField = entityField;
	}
	public T getMinValue() {
		return minValue;
	}
	public void setMinValue(T minValue) {
		this.minValue = minValue;
	}
	public T getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;
	}
	public String getEntityField() {
		return entityField;
	}
	public void setEntityField(String entityField) {
		this.entityField = entityField;
	}
}
