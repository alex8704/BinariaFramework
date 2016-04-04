package co.com.binariasystems.fmw.vweb.uicomponet.pager;

import co.com.binariasystems.fmw.business.domain.Order;
import co.com.binariasystems.fmw.business.domain.Order.Direction;
import co.com.binariasystems.fmw.business.domain.PageRequest;
import co.com.binariasystems.fmw.business.domain.Sort;
import co.com.binariasystems.fmw.event.FMWEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager;

public class PageChangeEvent<FILTER_TYPE> implements FMWEvent{
	private int oldPage;
	private int page;
	private int rowsByPage;
	private int pagesPerGroup;
	private FILTER_TYPE filterDTO;
	private Pager<FILTER_TYPE, ?> pager;
	private String[] sortProperties;
	private boolean[] ascending;
	
	
	public int getOldPage() {
		return oldPage;
	}
	public void setOldPage(int oldPage) {
		this.oldPage = oldPage;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRowsByPage() {
		return rowsByPage;
	}
	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	public FILTER_TYPE getFilterDTO() {
		return filterDTO;
	}
	public void setFilterDTO(FILTER_TYPE filterDTO) {
		this.filterDTO = filterDTO;
	}
	public Pager<FILTER_TYPE, ?> getPager() {
		return pager;
	}
	public void setPager(Pager<FILTER_TYPE, ?> pager) {
		this.pager = pager;
	}
	
	public int getInitialRow(){
		//@PageNumber - 1) * @RowspPage
		return (page -1) * rowsByPage;
		//return (rowsByPage * page - rowsByPage ) + 1;
	}
	
	public int getFinalRow(){
		return getInitialRow() + (rowsByPage * pagesPerGroup);
	}
	public int getPagesPerGroup() {
		return pagesPerGroup;
	}
	public void setPagesPerGroup(int pagesPerGroup) {
		this.pagesPerGroup = pagesPerGroup;
	}
	/**
	 * @return the sortProperties
	 */
	public String[] getSortProperties() {
		return sortProperties;
	}
	/**
	 * @param sortProperties the sortProperties to set
	 */
	public void setSortProperties(String[] sortProperties) {
		this.sortProperties = sortProperties;
	}
	/**
	 * @return the ascending
	 */
	public boolean[] getAscending() {
		return ascending;
	}
	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean[] ascending) {
		this.ascending = ascending;
	}
	
	public PageRequest getPageRequest(){
		Order[] orders = null;
		if(getSortProperties() != null && getSortProperties().length > 0){
			orders = new Order[getSortProperties().length];
			for(int i = 0; i < getSortProperties().length; i++){
				orders[i] = new Order((getAscending()[i] ? Direction.ASC : Direction.DESC),getSortProperties()[i]);
			}
		}
		return new PageRequest(page, rowsByPage * pagesPerGroup , (orders != null ? new Sort(orders) : null));
	}
	
	
}
