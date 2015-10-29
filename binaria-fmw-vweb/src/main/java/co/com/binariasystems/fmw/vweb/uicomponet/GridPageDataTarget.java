package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import com.vaadin.ui.Grid;

public class GridPageDataTarget<RESULT_TYPE> implements PageDataTarget<RESULT_TYPE> {
	private Grid grid;
	public GridPageDataTarget(Grid grid){
		this.grid = grid;
	}
	
	@Override
	public void refreshPageData(List<RESULT_TYPE> pageData) {
		if(grid != null){
			grid.getContainerDataSource().removeAllItems();
			for(RESULT_TYPE row : pageData)
				grid.getContainerDataSource().addItem(row);
		}
	}

}
