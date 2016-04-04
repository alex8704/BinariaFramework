package co.com.binariasystems.fmw.business.domain;

import java.io.Serializable;

import co.com.binariasystems.fmw.business.domain.Order.Direction;


public class PageRequest implements Serializable{
	private final Sort sort;
	private final int page;
	private final int size;

	/**
	 * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
	 * page.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 */
	public PageRequest(int page, int size) {
		this(page, size, null);
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	public PageRequest(int page, int size, Direction direction, String... properties) {
		this(page, size, new Sort(direction, properties));
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param sort can be {@literal null}.
	 */
	public PageRequest(int page, int size, Sort sort) {
		this.sort = sort;
		this.page = page;
		this.size = size;
	}

	public Sort getSort() {
		return sort;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	

}
