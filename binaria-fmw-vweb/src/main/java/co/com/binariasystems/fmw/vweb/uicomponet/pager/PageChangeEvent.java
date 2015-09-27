package co.com.binariasystems.fmw.vweb.uicomponet.pager;

import co.com.binariasystems.fmw.event.FMWEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager;

public class PageChangeEvent implements FMWEvent{
	private int oldPage;
	private int page;
	private int rowsByPage;
	private Object filterDTO;
	private Pager pager;
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
	public Object getFilterDTO() {
		return filterDTO;
	}
	public void setFilterDTO(Object filterDTO) {
		this.filterDTO = filterDTO;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
	public int getInitialRow(){
		//@PageNumber - 1) * @RowspPage
		return (page -1) * rowsByPage;
		//return (rowsByPage * page - rowsByPage ) + 1;
	}
	
	public int getFinalRow(){
		return getInitialRow() + rowsByPage;
	}
	
}