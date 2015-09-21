package co.com.binariasystems.fmw.vweb.uicomponet.pager;

import co.com.binariasystems.fmw.util.pagination.ListPage;


public interface PageChangeHandler<T>{
	public ListPage<T> loadPage(PageChangeEvent event) throws Exception;
}
