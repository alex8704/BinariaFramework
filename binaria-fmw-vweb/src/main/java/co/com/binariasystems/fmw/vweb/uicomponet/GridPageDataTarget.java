package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionModel;

public class GridPageDataTarget<RESULT_TYPE> implements PageDataTarget<RESULT_TYPE> {
	private Grid grid;
	public GridPageDataTarget(Grid grid){
		this.grid = grid;
	}
	
	@Override
	public void refreshPageData(List<RESULT_TYPE> pageData) {
		if(grid != null){
			if(grid.getSelectionModel() instanceof SelectionModel.Single)
				grid.select(null);
			else if(grid.getSelectionModel() instanceof SelectionModel.Multi)
				grid.deselect(grid.getSelectedRows());
			grid.getContainerDataSource().removeAllItems();
			for(RESULT_TYPE row : pageData)
				grid.getContainerDataSource().addItem(row);
		}
	}

}
