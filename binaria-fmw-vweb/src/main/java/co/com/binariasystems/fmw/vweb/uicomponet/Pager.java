package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

public class Pager<T> extends Observable {
	public enum PagerMode {PAGE_PAGINATION, ROW_PAGINATION}
	public static final Integer[] ROWS_BY_PAGE_VALS = {10, 15, 20};
	public static final int DEFAULT_MAX_PAGES_MEMORY = 200;
	private List<T> pageData;
	private int rowsByPage = ROWS_BY_PAGE_VALS[0];
	private int oldPage = 0;
	private int currentPage = 0;
	private int rowCount = 0;
	private int pageCount = 0;
	
	private HorizontalLayout content;
	private Label rowsFoundLabel;
	private Label rowsByPageConfLabel;
	private NativeSelect rowsByPageConfSelect;
	
	private Link nextLink;
	private Link backLink;
	private Link firstLink;
	private Link lastLink;
	
	private HorizontalLayout nextLinkWrap;
	private HorizontalLayout backLinkWrap;
	private HorizontalLayout firstLinkWrap;
	private HorizontalLayout lastLinkWrap;
	
	private TextField pageTxt;
	private ObjectProperty<Integer> pageCountProperty = new ObjectProperty<Integer>(pageCount);
	private ObjectProperty<Integer> currentPageProperty = new ObjectProperty<Integer>(currentPage);
	private ObjectProperty<Integer> rowsByPageProperty = new ObjectProperty<Integer>(rowsByPage);
	private Object searchDTO;
	private PageChangeHandler pageChangeHandler;
	private int maxPagesInMemory = 1000;
	private ListPage<T> pageList = new ListPage<T>();
	private IntegerRangeValidator rangeValidator;
	private PagerMode pagerMode = PagerMode.PAGE_PAGINATION;
	
	
	public Pager(){
		super();
		initComponents();
	}
	
	public Pager(PagerMode pMode){
		super();
		setPagerMode(pMode);
		initComponents();
	}
	
	public Pager(PagerMode pMode, int rowsByPage){
		super();
		setRowsByPage(rowsByPage);
		setPagerMode(pMode);
		initComponents();
	}

	private void initComponents() {
		content = new HorizontalLayout();
		rowsFoundLabel = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));						//Se encontraron {0} registros
		HorizontalLayout rowsConfLayout = new HorizontalLayout();
		rowsByPageConfLabel = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_ROWS_CAPTION)+":");
		rowsByPageConfSelect = new NativeSelect();
		rowsByPageConfSelect.addItems(ROWS_BY_PAGE_VALS);
		rowsByPageConfSelect.setPropertyDataSource(rowsByPageProperty);
		HorizontalLayout controlsLayout = new HorizontalLayout();
		firstLink = new Link(null, null);								//<<
		backLink = new Link(null, null);								//<
		Label pageLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_PAGE_CAPTION)+":");						//Pagina:
		pageTxt = new TextField(currentPageProperty);		//{0}
		Label xOfn = new Label("/");						//
		Label pageCountLbl = new Label(pageCountProperty);	//
		nextLink = new Link(null, null);								//>
		lastLink = new Link(null, null);								//>>
		firstLinkWrap = new HorizontalLayout();
		backLinkWrap = new HorizontalLayout();
		nextLinkWrap = new HorizontalLayout();
		lastLinkWrap = new HorizontalLayout();
		rangeValidator = new IntegerRangeValidator("", 0, pageCount);
		
		rowsByPageConfSelect.setHeight(22, Unit.PIXELS);
		rowsByPageConfSelect.setWidth(50, Unit.PIXELS);
		rowsByPageConfSelect.setNullSelectionAllowed(false);
		
		firstLink.setWidth(16, Unit.PIXELS);
		backLink.setWidth(16, Unit.PIXELS);
		nextLink.setWidth(16, Unit.PIXELS);
		lastLink.setWidth(16, Unit.PIXELS);
		firstLink.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		backLink.setIcon(FontAwesome.ANGLE_LEFT);
		nextLink.setIcon(FontAwesome.ANGLE_RIGHT);
		lastLink.setIcon(FontAwesome.ANGLE_DOUBLE_RIGHT);
		
		pageTxt.setWidth(50, Unit.PIXELS);
		pageTxt.setHeight(22, Unit.PIXELS);
		pageTxt.addStyleName("align-center");
		
		
		if(pagerMode == PagerMode.PAGE_PAGINATION){
			rowsConfLayout.addComponent(rowsByPageConfLabel);
			rowsConfLayout.addComponent(rowsByPageConfSelect);
			
			rowsConfLayout.setSpacing(true);
			rowsConfLayout.setComponentAlignment(rowsByPageConfLabel, Alignment.MIDDLE_LEFT);
			rowsConfLayout.setComponentAlignment(rowsByPageConfSelect, Alignment.MIDDLE_LEFT);
			rowsConfLayout.setExpandRatio(rowsByPageConfSelect, 1.0f);
		}
		
		firstLinkWrap.addComponent(firstLink);
		backLinkWrap.addComponent(backLink);
		nextLinkWrap.addComponent(nextLink);
		lastLinkWrap.addComponent(lastLink);
		
		controlsLayout.addStyleName(UIConstants.PAGER_STYLENAME);
		controlsLayout.setSpacing(true);
		controlsLayout.addComponent(firstLinkWrap);
		controlsLayout.addComponent(backLinkWrap);
		controlsLayout.addComponent(pageLbl);
		controlsLayout.addComponent(pageTxt);
		controlsLayout.addComponent(xOfn);
		controlsLayout.addComponent(pageCountLbl);
		controlsLayout.addComponent(nextLinkWrap);
		controlsLayout.addComponent(lastLinkWrap);
		
		firstLinkWrap.setComponentAlignment(firstLink, Alignment.MIDDLE_LEFT);
		backLinkWrap.setComponentAlignment(backLink, Alignment.MIDDLE_LEFT);
		nextLinkWrap.setComponentAlignment(nextLink, Alignment.MIDDLE_LEFT);
		lastLinkWrap.setComponentAlignment(lastLink, Alignment.MIDDLE_LEFT);
		
		controlsLayout.setComponentAlignment(pageLbl, Alignment.MIDDLE_LEFT);
		controlsLayout.setComponentAlignment(pageTxt, Alignment.MIDDLE_LEFT);
		controlsLayout.setComponentAlignment(xOfn, Alignment.MIDDLE_LEFT);
		controlsLayout.setComponentAlignment(pageCountLbl, Alignment.MIDDLE_LEFT);
		
		
		content.setSpacing(true);
		content.addComponent(rowsFoundLabel);
		content.addComponent(rowsConfLayout);
		content.addComponent(controlsLayout);
		
		content.setExpandRatio(rowsFoundLabel, 1.0f);
		//content.setExpandRatio(rowsConfLayout, 0.1f);
		content.setComponentAlignment(controlsLayout, Alignment.TOP_RIGHT);
		
		firstLink.setEnabled(false);
		backLink.setEnabled(false);
		nextLink.setEnabled(false);
		lastLink.setEnabled(false);
		rowsByPageConfSelect.setImmediate(true);
		
		bindEvents();
	}
	
	private void bindEvents(){
		pageTxt.addValidator(rangeValidator);
		pageTxt.addValidator(new NullValidator("", false));
		pageTxt.setValidationVisible(false);
		
		currentPageProperty.addValueChangeListener(new ValueChangeListener() {			
			public void valueChange(ValueChangeEvent event) {
				oldPage = currentPage;
				currentPage = currentPageProperty.getValue();
				try{
					if(currentPage != 0)
						firePageChangeEvent();
					firstLink.setEnabled(currentPage > rangeValidator.getMinValue());
					backLink.setEnabled(currentPage > rangeValidator.getMinValue());
					nextLink.setEnabled(currentPage < rangeValidator.getMaxValue());
					lastLink.setEnabled(currentPage < rangeValidator.getMaxValue());
				}catch(Exception ex){
					MessageDialog.showExceptions(ex);
					ex.printStackTrace();
				}
			}
		});
		
		firstLinkWrap.addLayoutClickListener(new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				if(firstLink.isEnabled()){
					currentPageProperty.setValue(1);
				}
			}
		});
		
		backLinkWrap.addLayoutClickListener(new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				if(backLink.isEnabled()){
					currentPageProperty.setValue(currentPage - 1);
				}
			}
		});
		
		nextLinkWrap.addLayoutClickListener(new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				if(nextLink.isEnabled()){
					currentPageProperty.setValue(currentPage + 1);
				}
			}
		});
		
		lastLinkWrap.addLayoutClickListener(new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				if(lastLink.isEnabled()){
					currentPageProperty.setValue(pageCount);
				}
			}
		});
		
		if(pagerMode == PagerMode.PAGE_PAGINATION){
			rowsByPageProperty.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					pageCount = (int)Math.ceil((float)rowCount / (float)rowsByPageProperty.getValue());
					pageCountProperty.setValue(pageCount);
					rowsByPage = rowsByPageProperty.getValue();
					if(rangeValidator.getMinValue() == 0) return;
					if(currentPage != rangeValidator.getMinValue())
						currentPageProperty.setValue(rangeValidator.getMinValue());
					else{
						try{
							firePageChangeEvent();
						}catch(Exception ex){
							MessageDialog.showExceptions(ex);
							System.err.println(ex.getMessage());
						}
					}
				}
			});
		}
	}
	
	public HorizontalLayout getContent(){
		return content;
	}
	
	public Object getSearchDTO() {
		return searchDTO;
	}

	public void setSearchDTO(Object searchDTO) {
		this.searchDTO = searchDTO;
		
		try {
			fireDTOChangeEvent();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void reset(){
		rowCount = 0;
		setRangeValidatorProperties();
		currentPageProperty.setValue(0);
		pageCountProperty.setValue(0);
		rowsFoundLabel.setValue(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
		pageList.setRowCount(0);
		pageList.getData().clear();
		if(pagerMode == PagerMode.PAGE_PAGINATION)
			setPageData(pageList.getData());
	}
	
	private void firePageChangeEvent() throws Exception{
		if(searchDTO == null)return;
//		if(availablePageInMemory())
		//if(oldPage == currentPage)
			//setPageData(extractCurrentPageData());
		if(pageChangeHandler != null){
			PageChangeEvent pce = new PageChangeEvent();
			pce.setFilterDTO(this.searchDTO);
			pce.setOldPage(oldPage);
			pce.setPage(currentPage);
			pce.setPager(this);
			pce.setRowsByPage(rowsByPage);
			pageList = pageChangeHandler.loadPage(pce);
			//-----------------------------------------------------
			rowCount = pageList.getRowCount();
			pageCount = (int)Math.ceil((float)rowCount / (float)rowsByPageProperty.getValue());
			pageCountProperty.setValue(pageCount);
			if(rowCount > 0)
				rowsFoundLabel.setValue(MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NROWS_FOUND), rowCount));
			else
				rowsFoundLabel.setValue(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
			//currentPage = (pageList.getRowCount() <= 0) ? 0 : 1;
			setRangeValidatorProperties();
			if(pageList.getRowCount() == 0)
				currentPageProperty.setValue(0);
			//-----------------------------------------------------
			setPageData(extractCurrentPageData());
		}	
	}
	
	private void fireDTOChangeEvent() throws Exception{
		rowCount =1;
		oldPage = 0;
		setRangeValidatorProperties();
		currentPageProperty.setValue(1);
		return;
	}
	
	private List<T> extractCurrentPageData(){
		if(rowsByPageProperty.getValue().intValue() >= pageList.getData().size())
			return pageList.getData();
		int iniPos = rowsByPageProperty.getValue().intValue() * currentPageProperty.getValue().intValue() - rowsByPageProperty.getValue().intValue();
		int finPos = iniPos + rowsByPageProperty.getValue().intValue();
		return pageList.getData().subList(iniPos, finPos > pageList.getData().size() ? pageList.getData().size() : finPos);
	}
	
	private void setRangeValidatorProperties(){
		int min = 0;
		int max = 0;
		
		if(rowCount > 0){
			min = 1;
			max = pageCount;
		}
		rangeValidator.setMinValue(min);
		rangeValidator.setMaxValue(max);
	}
	
	public void setPageChangeHandler(PageChangeHandler handler){
		pageChangeHandler = handler;
	}

	

	public PagerMode getPagerMode() {
		return pagerMode;
	}

	public void setPagerMode(PagerMode pm) {
		this.pagerMode = pm != null ? pm : PagerMode.PAGE_PAGINATION;
		if(pagerMode == PagerMode.ROW_PAGINATION)
			setRowsByPage(1);
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	private void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
		rowsByPageProperty.setValue(this.rowsByPage);
	}




	public static class TableDataLoadObserver implements Observer{
		private Table table;
		
		private TableDataLoadObserver(){}
		
		public TableDataLoadObserver(Table table){
			this.table = table;
		}
		
		public void update(Observable o, Object arg) {
			table.getContainerDataSource().removeAllItems();
			if(arg instanceof List){
				List list = (List)arg;
				for(Object obj : list)
					table.getContainerDataSource().addItem(obj);
			}
		}
		
	}
	
	private void setPageData(List<T> data){
		this.pageData = data;
		setChanged();
		notifyObservers(pageData);
	}
}
