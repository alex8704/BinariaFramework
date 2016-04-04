package co.com.binariasystems.fmw.vweb.uicomponet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.EntityConfigUtils;
import co.com.binariasystems.fmw.vweb.util.GridUtils;
import co.com.binariasystems.fmw.vweb.util.GridUtils.ActionHandler;
import co.com.binariasystems.fmw.vweb.util.GridUtils.ActionLinkInfo;
import co.com.binariasystems.fmw.vweb.util.GridUtils.GenericStringPropertyGenerator;
import co.com.binariasystems.fmw.vweb.util.GridUtils.SimpleCellStyleGenerator;
import co.com.binariasystems.fmw.vweb.util.GridUtils.SimpleStyleInfo;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.Renderer;

public class SearcherResultWindow<T> extends Window implements CloseListener, ClickListener, ActionHandler {
	public static enum SearchType{PK, FILTER, BUTTON};
	private Class<T> entityClazz;
	private FormPanel form;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	
	private EntityConfigData<T> entityConfigData;
	private EntityCRUDOperationsManager<T> manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private BeanItem<T> beanItem;
	private Pager<T, T> pager;
	private PageChangeHandler<T, T> pageChangeHanlder;
	private Grid resultsGrid;
	private GeneratedPropertyContainer gridContainer;
	
	private SearchType currentSearchType;
	private T oldValue;
	private T selectedValue;
	private Criteria conditions;
	
	private MessageFormat labelsFmt;
	private String selectionEventFunction;
	private SearchSelectionChangeListener<T> selectionChangeListener;
	private boolean wasChoice;
	private boolean initialized;
	private static final String ACTIONS_COLUM_ID = "actions";
	
	private MessageBundleManager entityStrings;
	private Object currentFilterValue;
	
	
	public SearcherResultWindow(Class<T> entityClazz) {
		this.entityClazz = entityClazz;
		labelsFmt = FMWEntityUtils.createEntityLabelsMessageFormat();
		this.addCloseListener(this);
		try {
			entityConfigData = (EntityConfigData<T>)EntityConfigurationManager.getInstance().getConfigurator(entityClazz).configure();
		} catch (FMWException ex) {
			MessageDialog.showExceptions(ex);
			return;
		}
		manager = (EntityCRUDOperationsManager<T>)EntityCRUDOperationsManager.getInstance(entityClazz);
		selectionEventFunction = SearcherResultWindow.class.getSimpleName()+"_fn"+Math.abs(hashCode());
	}
	
	@Override
	public void attach() {
		super.attach();
		initContent();
		center();
		beanItem.getItemProperty(entityConfigData.getSearchFieldName()).setValue(currentFilterValue);
		searchBtn.click();
	}
	
	public void setSelectionChangeListener(SearchSelectionChangeListener<T> selectionChangeListener) {
		this.selectionChangeListener = selectionChangeListener;
	}
	public Criteria getConditions() {
		return conditions;
	}
	public void setConditions(Criteria conditions) {
		this.conditions = conditions;
	}
	
	private void initContent(){
		if(initialized) return;
		try{
			setCaption(VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_CAPTION));
			form = new FormPanel(2);
			List<FieldConfigData> sortedFields = FMWEntityUtils.sortByUIControlTypePriority(entityConfigData);
			
			for(FieldConfigData fieldCfg : sortedFields){
				Component comp = EntityConfigUtils.createComponentForField(fieldCfg, entityConfigData, labelsFmt, getEntityStrings());
				form.add(comp, Dimension.percent(100));
				componentMap.put(fieldCfg.getFieldName(), comp);
			}
			
			pager = new Pager<T, T>(PagerMode.PAGE);
			searchBtn = new Button(FontAwesome.SEARCH);
			searchBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
			searchAllBtn = new Button(FontAwesome.SEARCH_PLUS);
			searchAllBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
			cleanBtn = new Button(FontAwesome.ERASER);
			cleanBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
			
			gridContainer = new GeneratedPropertyContainer(new SortableBeanContainer<T>(entityConfigData.getEntityClass()));
			gridContainer.addGeneratedProperty(ACTIONS_COLUM_ID, new GridUtils.ActionLinkValueGenerator(entityConfigData.getPkFieldName(), null, this, selectionEventFunction, new ActionLinkInfo("select",VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_CHOOSE_CAPTION))));
			resultsGrid = new Grid(VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_TABLE_CAPTION), gridContainer);
			resultsGrid.setSelectionMode(SelectionMode.NONE);
			
			resultsGrid.getColumn(ACTIONS_COLUM_ID)
			.setHeaderCaption(VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_CHOOSE_CAPTION))
			.setRenderer(new HtmlRenderer());
			
			List<String> columnIds = getGridColumnFields();
			GenericStringPropertyGenerator genericPropertyGenerator = new GenericStringPropertyGenerator();
			for(String columnId : columnIds){
				if(columnId.equals(ACTIONS_COLUM_ID)) continue;
				FieldConfigData fieldCfg = entityConfigData.getFieldData(columnId);
				Renderer<?> renderer = GridUtils.obtainRendererForType(fieldCfg.getFieldType());
				Column column = resultsGrid.getColumn(columnId);
				column.setHeaderCaption(EntityConfigUtils.getFieldCaptionText(fieldCfg, entityConfigData, labelsFmt, getEntityStrings()));
				if(fieldCfg.isRelationField() && FMWEntityUtils.isEntityClass(fieldCfg.getFieldType())){
					gridContainer.addGeneratedProperty(columnId, genericPropertyGenerator);
				}
				if(renderer != null)
					column.setRenderer(renderer);	
			}
			
			resultsGrid.setColumns(columnIds.toArray());
			resultsGrid.setCellStyleGenerator(new SimpleCellStyleGenerator(new SimpleStyleInfo("actions", UIConstants.CENTER_ALIGN_STYLE), new SimpleStyleInfo(entityConfigData.getPkFieldName(), UIConstants.CENTER_ALIGN_STYLE)));
			resultsGrid.setHeightMode(HeightMode.ROW);
			resultsGrid.setHeightByRows(pager.getRowsByPage());
			
			form.addEmptyRow();
			form.addCenteredOnNewRow(searchBtn, searchAllBtn, cleanBtn);
			form.add(resultsGrid, 2, Dimension.fullPercent());
			form.addCenteredOnNewRow(Dimension.fullPercent(), pager);
			
			bindComponentsToModel();
			
			bindControlEvents();
			
			form.setSubmitButton(searchBtn);
			form.setResetButton(cleanBtn);
			
			setContent(form);
			setWidth(1000, Unit.PIXELS);
			//setHeight(Page.getCurrent().getBrowserWindowHeight() - 25, Unit.PIXELS);
			setModal(true);
			
			cleanBtn.click();
			initialized = true;
		}catch (FMWException ex) {
			MessageDialog.showExceptions(ex);
		}
	}
	
	private List<String> getGridColumnFields(){
		List<String> gridColumnFields = new ArrayList<String>();
		gridColumnFields.add(ACTIONS_COLUM_ID);
		gridColumnFields.add(entityConfigData.getPkFieldName());
		boolean hasGridFields = !entityConfigData.getGridColumnFields().isEmpty();
		Collection<String> columnFields = hasGridFields ? entityConfigData.getGridColumnFields() : entityConfigData.getFieldNames();
		for(String columnField : columnFields){
			if(!columnField.equals(entityConfigData.getPkFieldName()))
				gridColumnFields.add(columnField);
		}
		
		return gridColumnFields;
	}
	
	private MessageBundleManager getEntityStrings(){
		if(entityStrings == null)
			entityStrings = EntityConfigUtils.createMessageManagerEntityConfig(entityConfigData);
		return entityStrings;
	}
	
	private void bindComponentsToModel() throws FMWException{
		Object bean = null;
		try{
			bean = entityConfigData.getEntityClass().getConstructor().newInstance();
			beanItem = new BeanItem<T>((T)bean, componentMap.keySet());
			for(String fieldName :componentMap.keySet()){
				Component comp = componentMap.get(fieldName);
				if(comp instanceof Field)
					((Field<?>)comp).setPropertyDataSource(beanItem.getItemProperty(fieldName));
			}
		}catch(ReflectiveOperationException | SecurityException ex){
			Throwable cause = FMWExceptionUtils.prettyMessageException(ex);
			throw new FMWException(cause.getMessage(), cause);
		}
	}
	
	private void bindControlEvents(){
		searchBtn.addClickListener(this);
		searchAllBtn.addClickListener(this);
		cleanBtn.addClickListener(this);
		pageChangeHanlder = new PageChangeHandler<T, T>() {
			public ListPage<T> loadPage(PageChangeEvent<T> event) throws FMWUncheckedException {
				if(event.getFilterDTO() == null) return new ListPage<T>();
				try {
					return manager.searchForFmwComponent(event.getFilterDTO(), event.getInitialRow(), event.getRowsByPage() * event.getPagesPerGroup(), conditions);
				} catch (Exception e) {
					Throwable cause = FMWExceptionUtils.prettyMessageException(e);
					throw new FMWUncheckedException(cause.getMessage(), cause);
				}
			}
		};
		
		pager.setPageChangeHandler(pageChangeHanlder);
		pager.setPageDataTargetForGrid(resultsGrid);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		try{
			if(event.getButton() == searchBtn || event.getButton() == searchAllBtn)
				handleSearch(event.getButton());
			else if(event.getButton() == cleanBtn)
				handleClean();
		}catch(Exception ex){
			MessageDialog.showExceptions(ex);
		}finally{
			form.initFocus();
		}
	}
	
	@Override
	public void windowClose(CloseEvent e) {
		fireSelectionChangeEvent();
		cleanBtn.click();
	}
	
	private T handleBackgroundSearch(SearchType searchType){
		try{
			T searchBean = null;
			if(entityClazz.isAssignableFrom(currentFilterValue.getClass()))
				searchBean = (T)currentFilterValue;
			else{
				searchBean = entityConfigData.getEntityClass().getConstructor().newInstance();
				PropertyUtils.setProperty(searchBean, (SearchType.FILTER == searchType ? entityConfigData.getSearchFieldName() : entityConfigData.getPkFieldName()), currentFilterValue);
			}
			ListPage<T> resultPage = manager.searchForFmwComponent(searchBean, 0, 10, null);
			return resultPage.getRowCount() != 1 ? null : resultPage.getData().get(0);
		}catch(ReflectiveOperationException | FMWException ex){
			MessageDialog.showExceptions(ex);
		}
		return null;
	}
	
	private void handleSearch(Button button) throws Exception{
		if(button == searchAllBtn)
			handleClean();
		pager.setFilterDto((T)BeanUtils.cloneBean(beanItem.getBean()));
	}
	
	private void handlePKSearch(){
		selectedValue = handleBackgroundSearch(SearchType.PK);
		fireSelectionChangeEvent();
	}
	
	private void handleFilterSearch(){
		selectedValue = handleBackgroundSearch(SearchType.FILTER);
		if(selectedValue == null)
			show();
		else
			fireSelectionChangeEvent();
	}

	private void handleClean() throws Exception{
		VWebUtils.resetBeanItemDS(beanItem, null);
		pager.reset();
		if(wasChoice){
			oldValue = selectedValue;
			selectedValue = null;
		}
		wasChoice = false;
		form.initFocus();
	}
	
	//Busca y abre la ventana solo si no encuentra sesultado
	public void search(Object filterValue, SearchType searchType){
		this.currentSearchType = searchType != null ? searchType : SearchType.PK;
		this.currentFilterValue = filterValue;
		if(SearchType.BUTTON.equals(currentSearchType)){
			show();
			return;
		}
		if(currentFilterValue == null || (currentFilterValue instanceof CharSequence && StringUtils.isEmpty((CharSequence)currentFilterValue))){
			fireResetSelectionEvent();
			return;
		}	
		if(currentSearchType.equals(SearchType.FILTER))
			handleFilterSearch();
		else
			handlePKSearch();
	}

	@Override
	public void handleAction(String selectedId, String actionId) {
		for(Object bean : gridContainer.getItemIds()){
			Item item = gridContainer.getItem(bean);
			if(selectedId.equals(item.getItemProperty(entityConfigData.getPkFieldName()).getValue().toString())){
				selectedValue = (T)bean;
				break;
			}
		}
		wasChoice = true;
		close();
	}
	
	
	///Se pasa el parametro wasChoice, indicando si se selecciono algun item o no
	private void fireSelectionChangeEvent(){
		doSelectionChangeEvent(false);
	}
	
	private void fireResetSelectionEvent(){
		doSelectionChangeEvent(true);
	}
	
	private void doSelectionChangeEvent(boolean isReset){
		if(isReset){
			oldValue = null;
			selectedValue = null;
		}
		SearchSelectionChangeEvent<T> event = new SearchSelectionChangeEvent<T>(oldValue, selectedValue, currentSearchType, isReset);
		if(selectionChangeListener != null)
			selectionChangeListener.selectionChange(event);
	}
	
	private void show(){
		UI.getCurrent().addWindow(this);
	}
	
	public static class SearchSelectionChangeEvent<T>{
		private T oldValue;
		private T newValue;
		private SearchType searchType;
		private boolean reset;
		
		
		public SearchSelectionChangeEvent(T oldValue, T newValue, SearchType searchType, boolean reset) {
			super();
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.searchType = searchType;
			this.reset = reset;
		}
		public T getOldValue() {
			return oldValue;
		}
		public void setOldValue(T oldValue) {
			this.oldValue = oldValue;
		}
		public T getNewValue() {
			return newValue;
		}
		public void setNewValue(T newValue) {
			this.newValue = newValue;
		}
		public SearchType getSearchType() {
			return searchType;
		}
		public void setSearchType(SearchType searchType) {
			this.searchType = searchType;
		}
		public boolean isReset() {
			return reset;
		}
		public void setReset(boolean reset) {
			this.reset = reset;
		}
		
	}
	
	public static interface SearchSelectionChangeListener<T>{
		public void selectionChange(SearchSelectionChangeEvent<T> event);
	}

}
