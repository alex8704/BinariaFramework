package co.com.binariasystems.fmw.util.pagination;

import java.util.LinkedList;
import java.util.List;

public class ListPage<T> {
	private List<T> data;
	private int rowCount;
	
	public ListPage(){
	}

	public ListPage(List<T> data){
		this.data = data;
	}
	
	public ListPage(List<T> data, int rowCount){
		this.data = data;
		this.rowCount = rowCount;
	}
	
	public List<T> getData() {
		if(data == null)
			data = new LinkedList<T>();
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public int getRowCount() {
		if(rowCount < getData().size()){
			setRowCount(getData().size());
		}
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
}
