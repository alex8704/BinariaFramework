package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class Pager2<FILTER_TYPE, RESULT_TYPE> extends HorizontalLayout implements ValueChangeListener {
	public static final Integer[] ROWS_BY_PAGE_ITEMS = {10, 15, 20};
	public static enum PagerMode {PAGE, ITEM}
	
	private ObjectProperty<Integer> pageCountProperty = new ObjectProperty<Integer>(0);
	private ObjectProperty<Integer> currentPageProperty = new ObjectProperty<Integer>(0);
	private ObjectProperty<Integer> rowsByPageProperty = new ObjectProperty<Integer>(ROWS_BY_PAGE_ITEMS[0]);
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
	private FILTER_TYPE filterDto;
	private int maxCachedPages=1;
	private int currentCacheGroup;
	
	
	public Pager2(){
		this(null);
	}
	
	public Pager2(PagerMode pagerMode){
		this(pagerMode, ROWS_BY_PAGE_ITEMS[0]);
	}
	
	public Pager2(PagerMode pMode, int rowsByPage){
		rowsByPageProperty.setValue(rowsByPage);
		this.pagerMode = (pagerMode != null) ? pagerMode : PagerMode.PAGE;
	}
	
	
	@Override
	public void attach() {
		super.attach();
		initContent();
	}
	
	private void initContent() {
		foundItemsLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
		rowsByPageConfLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_ROWS_CAPTION)+":");
		rowsByPageConfCmb = new ComboBox();
		firstPLink = new LinkLabel("<<");
		backPLink = new LinkLabel("<");
		currentPageLeftLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_PAGE_CAPTION)+":");
		currentPageTxt = new TextField(currentPageProperty);
		currentPageRightLbl = new Label(VWebUtils.getCommonString(VWebCommonConstants.PAGER_PAGE_OF_CAPTION));
		totalPagesLbl = new Label(pageCountProperty);
		nextPLink = new LinkLabel(">");
		lastPLink = new LinkLabel(">>");
		
		filler = new Label("");
		rangeValidator = new IntegerRangeValidator("", 0, 0);
		
		//Layouts auxiliares
		HorizontalLayout pageSizePanel = new HorizontalLayout();
		HorizontalLayout currentPagePanel = new HorizontalLayout();
		
		foundItemsLbl.setWidth(100, Unit.PERCENTAGE);
		rowsByPageConfCmb.setWidth(55, Unit.PIXELS);
		rowsByPageConfCmb.addItems(Arrays.asList(ROWS_BY_PAGE_ITEMS));
		rowsByPageConfCmb.setNullSelectionAllowed(false);
		rowsByPageConfCmb.setPropertyDataSource(rowsByPageProperty);
		currentPageTxt.setWidth(50, Unit.PIXELS);
		filler.setWidth(25, Unit.PIXELS);
		
		pageSizePanel.addComponents(rowsByPageConfLbl, rowsByPageConfCmb);
		currentPagePanel.addComponents(firstPLink, backPLink, currentPageLeftLbl, currentPageTxt, currentPageRightLbl, totalPagesLbl, nextPLink, lastPLink);
		
		
		pageSizePanel.setSpacing(true);
		currentPagePanel.setSpacing(true);
		
		//Estilos
		firstPLink.addStyleName(VWebCommonConstants.PAGER_FIRSTP_CLASS);
		firstPLink.addStyleName(ValoTheme.LABEL_SMALL);
		backPLink.addStyleName(VWebCommonConstants.PAGER_FIRSTP_CLASS);
		backPLink.addStyleName(ValoTheme.LABEL_SMALL);
		nextPLink.addStyleName(VWebCommonConstants.PAGER_FIRSTP_CLASS);
		nextPLink.addStyleName(ValoTheme.LABEL_SMALL);
		lastPLink.addStyleName(VWebCommonConstants.PAGER_FIRSTP_CLASS);
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
		addStyleName(VWebCommonConstants.PAGER_CLASS);
		
		currentPageTxt.setImmediate(true);
		rowsByPageConfCmb.setImmediate(true);
		
		bindEvents();
		resetConstrains();
		reset();
	}
	
	private void bindEvents(){
		currentPageProperty.addValueChangeListener(this);
		pageCountProperty.addValueChangeListener(this);
		rowsByPageProperty.addValueChangeListener(this);		
	}
	
	private void currentPagePropertyValueChange(){
		if(currentPage() > 0)
			firePageChangeEvent();
	}
	
	private void pageCountPropertyValueChange(){
		resetConstrains();
	}
	
	private void rowsByPagePropertyValueChange(){
		fireFilterDtoChangeEvent();
	}
	
	private void resetConstrains(){
		rangeValidator.setMinValue(pageList.getRowCount() > 0 ? 1 : 0);
		rangeValidator.setMaxValue(pageCount());
		firstPLink.setEnabled(currentPage() > rangeValidator.getMinValue());
		backPLink.setEnabled(currentPage() > rangeValidator.getMinValue());
		nextPLink.setEnabled(currentPage() < rangeValidator.getMaxValue());
		lastPLink.setEnabled(currentPage() < rangeValidator.getMaxValue());
	}
	
	public void reset(){
		setFilterDto(null);
	}
	
	private int currentPage(){
		return currentPageProperty.getValue() != null ? currentPageProperty.getValue() : 0;
	}
	
	private int pageCount(){
		return pageCountProperty.getValue() != null ? pageCountProperty.getValue() : 0;
	}
	
	private int rowsByPage(){
		return rowsByPageProperty.getValue() != null ? rowsByPageProperty.getValue() : 0;
	}
	
	private void fireFilterDtoChangeEvent(){
		if(currentPage() != 1)
			currentPageProperty.setValue(1);
		else
			firePageChangeEvent();
	}
	
	private void firePageChangeEvent(){
		if(isCachedPage(currentPage())){
			setPageData(extractCurrentPageData());
			return;
		}
		if(pageChangeHandler == null) return;
		
		pageList = pageChangeHandler.loadPage(createPageChangeEvent());
		pageCountProperty.setValue((int)Math.ceil((float)pageList.getRowCount() / (float)rowsByPageProperty.getValue()));
		if(pageList.getRowCount() > 0)
			foundItemsLbl.setValue(MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NROWS_FOUND), pageList.getRowCount()));
		else
			foundItemsLbl.setValue(VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW));
		
		currentCacheGroup = (int)Math.ceil((float)currentPage() / (float)maxCachedPages);
		setPageData(extractCurrentPageData());
		if(pageList.getRowCount() <= 0)
			currentPageProperty.setValue(0);
	}
	
	private PageChangeEvent<FILTER_TYPE> createPageChangeEvent(){
		PageChangeEvent<FILTER_TYPE> event = new PageChangeEvent<FILTER_TYPE>();
		event.setFilterDTO(this.filterDto);
		event.setPage(currentPage());
		event.setPager(null);
		event.setRowsByPage(rowsByPage());
		event.setPagesPerGroup(maxCachedPages);
		
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
		return pageList.getData().subList(firstIdx, lastIdx > pageList.getRowCount() ? pageList.getRowCount() : lastIdx);
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
	
	public void setFilterDto(FILTER_TYPE filterDto){
		this.filterDto = filterDto;
		fireFilterDtoChangeEvent();
	}
	
	public void setMaxCachedPages(int max){
		this.maxCachedPages = max < 1 ? 1 : max;
	}
	
	public void setPageChangeHandler(PageChangeHandler<FILTER_TYPE, RESULT_TYPE> pageChangeHandler) {
		this.pageChangeHandler = pageChangeHandler;
	}

	private void setPageData(List<RESULT_TYPE> data){
		
	}
}
