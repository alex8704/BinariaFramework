package co.com.binariasystems.fmw.business.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.com.binariasystems.fmw.business.domain.Order.Direction;

public class Sort implements Serializable {
	private final List<Order> orders;
	
	public Sort(Order... orders) {
		this(Arrays.asList(orders));
	}

	public Sort(List<Order> orders) {

		if (null == orders || orders.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
		}

		this.orders = orders;
	}

	public Sort(String... properties) {
		this(Direction.ASC, properties);
	}

	public Sort(Direction direction, String... properties) {
		this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
	}

	public Sort(Direction direction, List<String> properties) {

		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one property to sort by!");
		}

		this.orders = new ArrayList<Order>(properties.size());

		for (String property : properties) {
			this.orders.add(new Order(direction, property));
		}
	}

	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		return orders;
	}
	
}
