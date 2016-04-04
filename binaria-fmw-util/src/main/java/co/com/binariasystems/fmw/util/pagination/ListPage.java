package co.com.binariasystems.fmw.util.pagination;

import java.util.Collections;
import java.util.List;

public class ListPage<T> {
	private List<T> data;
	private long rowCount;
	
	public ListPage(){
	}

	public ListPage(List<T> data){
		this.data = data;
	}
	
	public ListPage(List<T> data, long rowCount){
		this.data = data;
		this.rowCount = rowCount;
	}
	
	public List<T> getData() {
		if(data == null)
			data = Collections.EMPTY_LIST;
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public long getRowCount() {
		return rowCount;
	}
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
}
