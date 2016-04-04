package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import com.vaadin.ui.Table;

public class TablePageDataTarget<RESULT_TYPE> implements PageDataTarget<RESULT_TYPE>{
	private Table table;
	
	public TablePageDataTarget(Table table){
		this.table = table;
	}
	
	@Override
	public void refreshPageData(List<RESULT_TYPE> pageData) {
		if(table != null){
			table.setValue(null);
			table.getContainerDataSource().removeAllItems();
			for(RESULT_TYPE row : pageData)
				table.getContainerDataSource().addItem(row);
		}
	}

}
