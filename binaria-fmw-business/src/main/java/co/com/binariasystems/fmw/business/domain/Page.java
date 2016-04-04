package co.com.binariasystems.fmw.business.domain;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable{
	private int totalPages;
	private long totalElements;
	private List<T> content;
	public Page(int totalPages, long totalElements, List<T> content) {
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.content = content;
	}
	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * @return the totalElements
	 */
	public long getTotalElements() {
		return totalElements;
	}
	/**
	 * @param totalElements the totalElements to set
	 */
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	/**
	 * @return the content
	 */
	public List<T> getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(List<T> content) {
		this.content = content;
	}
	
	
}
