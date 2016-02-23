package co.com.binariasystems.fmw.vweb.uicomponet.pager;

import co.com.binariasystems.fmw.util.pagination.ListPage;


public interface PageChangeHandler<FILTER_TYPE, RESULT_TYPE>{
	public ListPage<RESULT_TYPE> loadPage(PageChangeEvent<FILTER_TYPE> event);
}
