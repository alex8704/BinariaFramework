package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.ClickHandler;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.LinkClickEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class Pager<FILTER_TYPE, RESULT_TYPE> extends HorizontalLayout implements ValueChangeListener, ClickHandler {
	public static final Integer[] ROWS_BY_PAGE_ITEMS = {10, 15, 20};
	public static enum PagerMode {PAGE, ITEM}
	
	private ObjectProperty<Integer> pageCountProperty = new ObjectProperty<Integer>(0, Integer.class);
	private ObjectProperty<Integer> currentPageProperty = new ObjectProperty<Integer>(0, Integer.class);
	private ObjectProperty<Integer> rowsByPageProperty = new ObjectProperty<Integer>(0, Integer.class);
	private IntegerRangeValidator rangeValidator;
	private Label foundItemsLbl;
	private Label rowsByPageConfLbl;
	private ComboBox rowsByPageConfCmb;
	private LinkLabel firstPLink;
	private LinkLabel lastPLink;
	private LinkLabel nextPLink;
	private LinkLabel backPLink;
	private Label currentPageLeftLbl;
	private TextField currentPageTxt;
	private Label currentPageRightLbl;
	private Label totalPagesLbl;
	private Label filler;
	
	private PagerMode pagerMode = PagerMode.PAGE;
	private ListPage<RESULT_TYPE> pageList = new ListPage<RESULT_TYPE>();
	private PageChangeHandler<FILTER_TYPE, RESULT_TYPE> pageChangeHandler;
	private PageDataTarget<RESULT_TYPE> pageDataTarget;
	private FILTER_TYPE filterDto;
	private int maxCachedPages=1;
	private int currentCacheGroup;
	private boolean initialized;
	private int rowsByPage;
	private BeanItemSorter itemSorter;
	
	
	public Pager(){
		this(null);
	}
	
	public Pager(PagerMode pagerMode){
		this.pagerMode = (pagerMode != null) ? pagerMode : PagerMode.PAGE;
		rowsByPageProperty.setValue(this.pagerMode.equals(PagerMode.ITEM) ? 1 : ROWS_BY_PAGE_ITEMS[0]);
	}
	
	@Override
	public void attach() {
		super.attach();
		initContent();
	}
	
	
	private void initContent() {
		if(initialized)return;
		foundItemsLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
		rowsByPageConfLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_ROWS_CAPTION)+":");
		rowsByPageConfCmb = new ComboBox();
		firstPLink = new LinkLabel("&lt;&lt;");
		backPLink = new LinkLabel("&lt;");
		currentPageLeftLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_PAGE_CAPTION)+":");
		currentPageTxt = new TextField(currentPageProperty);
		currentPageRightLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_PAGE_OF_CAPTION));
		totalPagesLbl = new Label(pageCountProperty);
		nextPLink = new LinkLabel("&gt;");
		lastPLink = new LinkLabel("&gt;&gt;");
		
		filler = new Label("");
		rangeValidator = new IntegerRangeValidator("", 0, 0);
		
		//Layouts auxiliares
		HorizontalLayout pageSizePanel = new HorizontalLayout();
		HorizontalLayout currentPagePanel = new HorizontalLayout();
		
		foundItemsLbl.setWidth(100, Unit.PERCENTAGE);
		rowsByPageConfCmb.setWidth(55, Unit.PIXELS);
		rowsByPageConfCmb.addItems(pagerMode.equals(PagerMode.ITEM) ? Arrays.asList(1) : Arrays.asList(ROWS_BY_PAGE_ITEMS));
		rowsByPageConfCmb.setNullSelectionAllowed(false);
		rowsByPageConfCmb.setPropertyDataSource(rowsByPageProperty);
		rowsByPageProperty.setValue(pagerMode.equals(PagerMode.ITEM) ? 1 : ROWS_BY_PAGE_ITEMS[0]);
		currentPageTxt.setWidth(50, Unit.PIXELS);
		filler.setWidth(25, Unit.PIXELS);
		
		pageSizePanel.addComponents(rowsByPageConfLbl, rowsByPageConfCmb);
		currentPagePanel.addComponents(firstPLink, backPLink, currentPageLeftLbl, currentPageTxt, currentPageRightLbl, totalPagesLbl, nextPLink, lastPLink);
		
		pageSizePanel.setSpacing(true);
		currentPagePanel.setSpacing(true);
		
		//Estilos
		firstPLink.addStyleName(UIConstants.PAGER_FIRSTP_STYLENAME);
		firstPLink.addStyleName(ValoTheme.LABEL_SMALL);
		backPLink.addStyleName(UIConstants.PAGER_FIRSTP_STYLENAME);
		backPLink.addStyleName(ValoTheme.LABEL_SMALL);
		nextPLink.addStyleName(UIConstants.PAGER_FIRSTP_STYLENAME);
		nextPLink.addStyleName(ValoTheme.LABEL_SMALL);
		lastPLink.addStyleName(UIConstants.PAGER_FIRSTP_STYLENAME);
		lastPLink.addStyleName(ValoTheme.LABEL_SMALL);
		
		foundItemsLbl.addStyleName(ValoTheme.LABEL_SMALL);
		foundItemsLbl.addStyleName(ValoTheme.LABEL_BOLD);
		rowsByPageConfLbl.addStyleName(ValoTheme.LABEL_SMALL);
		rowsByPageConfCmb.addStyleName(ValoTheme.COMBOBOX_SMALL);
		currentPageLeftLbl.addStyleName(ValoTheme.LABEL_SMALL);
		currentPageTxt.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		currentPageTxt.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		currentPageRightLbl.addStyleName(ValoTheme.LABEL_SMALL);
		totalPagesLbl.addStyleName(ValoTheme.LABEL_SMALL);
		filler.addStyleName(ValoTheme.LABEL_SMALL);
		
		currentPageTxt.addValidator(rangeValidator);
		currentPageTxt.addValidator(new NullValidator("", false));
		currentPageTxt.setValidationVisible(false);
		
		addComponents(foundItemsLbl, pageSizePanel, filler, currentPagePanel);
		setSpacing(true);
		setExpandRatio(foundItemsLbl, 1.0f);
		addStyleName(UIConstants.PAGER_STYLENAME);
		
		setComponentAlignment(foundItemsLbl, Alignment.MIDDLE_LEFT);
		
		pageSizePanel.setComponentAlignment(rowsByPageConfLbl, Alignment.MIDDLE_LEFT);
		
		currentPagePanel.setComponentAlignment(firstPLink, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(backPLink, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(currentPageLeftLbl, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(currentPageRightLbl, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(totalPagesLbl, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(nextPLink, Alignment.MIDDLE_LEFT);
		currentPagePanel.setComponentAlignment(lastPLink, Alignment.MIDDLE_LEFT);
		
		rowsByPageConfLbl.setVisible(pagerMode == PagerMode.PAGE);
		rowsByPageConfCmb.setVisible(pagerMode == PagerMode.PAGE);
		
		currentPageTxt.setNullRepresentation("");
		currentPageTxt.setImmediate(true);
		rowsByPageConfCmb.setImmediate(true);
		
		bindEvents();
		resetConstrains();
		initialized = true;
		if(filterDto != null)
			fireFilterDtoChangeEvent();
	}
	
	private void bindEvents(){
		currentPageProperty.addValueChangeListener(this);
		pageCountProperty.addValueChangeListener(this);
		rowsByPageProperty.addValueChangeListener(this);
		firstPLink.setClickHandler(this);
		backPLink.setClickHandler(this);
		nextPLink.setClickHandler(this);
		lastPLink.setClickHandler(this);
	}
	
	private void currentPagePropertyValueChange(){
		currentCacheGroup = currentPage() > 0 ? currentCacheGroup : 0;
		if(currentPage() > 0){
			firePageChangeEvent();
			if(pageList.getRowCount() <= 0)
				currentPageProperty.setValue(0);
		}
		
	}
	
	private void pageCountPropertyValueChange(){
		resetConstrains();
	}
	
	private void rowsByPagePropertyValueChange(){
		fireFilterDtoChangeEvent();
	}
	
	private void resetConstrains(){
		rangeValidator.setMinValue(pageCount() > 0 ? 1 : 0);
		rangeValidator.setMaxValue(pageCount());
		firstPLink.setEnabled(pageCount() > 0 && currentPage() > rangeValidator.getMinValue());
		backPLink.setEnabled(pageCount() > 0 && currentPage() > rangeValidator.getMinValue());
		nextPLink.setEnabled(pageCount() > 0 && currentPage() < rangeValidator.getMaxValue());
		lastPLink.setEnabled(pageCount() > 0 && currentPage() < rangeValidator.getMaxValue());
	}
	
	public void reset(){
//		initContent();
		setFilterDto(null);
	}
	
	private int currentPage(){
		return currentPageProperty.getValue() != null ? currentPageProperty.getValue() : 0;
	}
	
	private int pageCount(){
		return pageCountProperty.getValue() != null ? pageCountProperty.getValue() : 0;
	}
	
	private int rowsByPage(){
		return rowsByPageProperty.getValue() != null ? rowsByPageProperty.getValue() : rowsByPage;
	}
	
	private void fireFilterDtoChangeEvent(){
		currentCacheGroup = 0;
		if(currentPage() != 1)
			currentPageProperty.setValue(1);
		else
			firePageChangeEvent();
	}
	
	private void firePageChangeEvent(){
		if(pageChangeHandler == null) return;
		
		if(!isCachedPage(currentPage())){
			currentCacheGroup = (int)Math.ceil((float)currentPage() / (float)maxCachedPages);
			pageList = pageChangeHandler.loadPage(createPageChangeEvent());
		}
		
		pageCountProperty.setValue((int)Math.ceil((float)pageList.getRowCount() / (float)rowsByPageProperty.getValue()));
		if(pageList.getRowCount() > 0)
			foundItemsLbl.setValue(MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NROWS_FOUND), pageList.getRowCount()));
		else
			foundItemsLbl.setValue(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
		setPageData(extractCurrentPageData());
	}
	
	private PageChangeEvent<FILTER_TYPE> createPageChangeEvent(){
		PageChangeEvent<FILTER_TYPE> event = new PageChangeEvent<FILTER_TYPE>();
		int calculatedPage = maxCachedPages == 1 ? currentPage() : ((maxCachedPages * currentCacheGroup) - maxCachedPages) + 1;
		event.setFilterDTO(this.filterDto);
		event.setPage(calculatedPage);
		event.setPager(null);
		event.setRowsByPage(rowsByPage());
		event.setPagesPerGroup(maxCachedPages);
		if(itemSorter instanceof Pager.PagerBeanItemSorter){
			event.setSortProperties(((PagerBeanItemSorter)itemSorter).getSortPropertyIds());
			event.setAscending(((PagerBeanItemSorter)itemSorter).getSortDirections());
		}
		
		return event;
	}
	
	private boolean isCachedPage(int page){
		int lastCachedPage = maxCachedPages * currentCacheGroup;
		int initialCachedPage = (lastCachedPage - maxCachedPages) + 1;
		return page >= initialCachedPage && page <= lastCachedPage;
	}
	
	private List<RESULT_TYPE> extractCurrentPageData(){
		if(rowsByPage() >= pageList.getRowCount())
			return pageList.getData();
		int initialCachedPage = ((maxCachedPages * currentCacheGroup) - maxCachedPages) + 1;
		int cachedGroupPage = (currentPage() - initialCachedPage) + 1;
		int lastIdx = rowsByPage() * cachedGroupPage;
		int firstIdx = lastIdx - rowsByPage();
		return pageList.getData().subList(firstIdx, lastIdx > pageList.getData().size() ? pageList.getData().size() : lastIdx);
	}
	
	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		if(currentPageProperty.equals(event.getProperty()))
			currentPagePropertyValueChange();
		else if(pageCountProperty.equals(event.getProperty()))
			pageCountPropertyValueChange();
		else if(rowsByPageProperty.equals(event.getProperty()))
			rowsByPagePropertyValueChange();
	}
	
	@Override
	public void handleClick(LinkClickEvent event) {
		if(firstPLink.equals(event.getLinkLabel()))
			currentPageProperty.setValue(rangeValidator.getMinValue());
		else if(backPLink.equals(event.getLinkLabel()))
			currentPageProperty.setValue(currentPage() - 1);
		else if(nextPLink.equals(event.getLinkLabel()))
			currentPageProperty.setValue(currentPage() + 1);
		else if(lastPLink.equals(event.getLinkLabel()))
			currentPageProperty.setValue(rangeValidator.getMaxValue());
	}
	
	public void setFilterDto(FILTER_TYPE filterDto){
//		initContent();
		this.filterDto = filterDto;
		if(initialized)
			fireFilterDtoChangeEvent();
	}
	
	public void setMaxCachedPages(int max){
		this.maxCachedPages = max < 1 ? 1 : max;
	}
	
	public void setPageChangeHandler(PageChangeHandler<FILTER_TYPE, RESULT_TYPE> pageChangeHandler) {
		this.pageChangeHandler = pageChangeHandler;
	}
	
	public void setPageDataTarget(PageDataTarget<RESULT_TYPE> pageDataTarget){
		this.pageDataTarget = pageDataTarget;
	}
	
	public void setPageDataTargetForGrid(Grid grid){
		this.pageDataTarget = new GridPageDataTarget<RESULT_TYPE>(grid);
		setItemSorterForGrid(grid);
	}
	
	public void setPageDataTargetForTable(Table table){
		this.pageDataTarget = new TablePageDataTarget<RESULT_TYPE>(table);
		Container containerDS = table.getContainerDataSource();
		if(containerDS instanceof SortableBeanContainer){
			if(itemSorter == null)
				itemSorter = new PagerBeanItemSorter();
			((SortableBeanContainer)containerDS).setSorter(itemSorter);
		}
	}
	
	private void setItemSorterForGrid(Grid grid){
		Indexed containerDS = grid.getContainerDataSource();
		if(containerDS instanceof GeneratedPropertyContainer){
			containerDS = ((GeneratedPropertyContainer)containerDS).getWrappedContainer();
		}
		if(containerDS instanceof SortableBeanContainer){
			if(itemSorter == null)
				itemSorter = new PagerBeanItemSorter();
			((SortableBeanContainer)containerDS).setSorter(itemSorter);
		}
	}

	private void setPageData(List<RESULT_TYPE> data){
		if(pageDataTarget != null)
			pageDataTarget.refreshPageData(data);
	}
	
	public int getRowsByPage(){
		return rowsByPage();
	}
	
	private class PagerBeanItemSorter implements BeanItemSorter{
		private String[] sortPropertyIds;
	    private boolean[] sortDirections;
	    private Container container;
		@Override public void sort(Sortable container, Object[] propertyId, boolean[] ascending) {
			this.container = container;
	        // Removes any non-sortable property ids
	        final List<String> ids = new ArrayList<String>();
	        final List<Boolean> orders = new ArrayList<Boolean>();
	        final Collection<?> sortable = container
	                .getSortableContainerPropertyIds();
	        for (int i = 0; i < propertyId.length; i++) {
	            if (sortable.contains(propertyId[i])) {
	                ids.add(propertyId[i].toString());
	                orders.add(Boolean.valueOf(i < ascending.length ? ascending[i] : true));
	            }
	        }
	        sortPropertyIds = ids.toArray(new String[ids.size()]);
	        sortDirections = new boolean[orders.size()];
	        for (int i = 0; i < sortDirections.length; i++) {
	            sortDirections[i] = (orders.get(i)).booleanValue();
	        }
	        
	        setFilterDto(filterDto);
		}
		/**
		 * @return the sortPropertyIds
		 */
		public String[] getSortPropertyIds() {
			return sortPropertyIds;
		}
		/**
		 * @return the sortDirections
		 */
		public boolean[] getSortDirections() {
			return sortDirections;
		}
		/**
		 * @return the container
		 */
		public Container getContainer() {
			return container;
		}
		
		
	}
}
