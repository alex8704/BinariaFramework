package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

public interface PageDataTarget<RESULT_TYPE> {
	public void refreshPageData(List<RESULT_TYPE>  pageData);
}
