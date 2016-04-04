package co.com.binariasystems.fmw.business.domain;

import java.io.Serializable;

public class Order implements Serializable {
	public static enum Direction{DESC, ASC};
	private final Direction direction;
	private final String property;
	public Order(Direction direction, String property) {
		this.direction = direction;
		this.property = property;
	}
	
	public Order(String property) {
		this.direction = Direction.ASC;
		this.property = property;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
	
	
}
