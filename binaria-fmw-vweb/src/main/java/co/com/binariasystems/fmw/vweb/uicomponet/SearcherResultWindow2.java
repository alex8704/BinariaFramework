package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager2.PagerMode;
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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.server.Page;
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

public class SearcherResultWindow2<T> extends Window implements CloseListener, ClickListener, ActionHandler {
	private Class<T> entityClazz;
	private FormPanel form;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	
	private EntityConfigData<T> entityConfigData;
	private EntityCRUDOperationsManager<T> manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private BeanItem<T> beanItem;
	private Pager2<T, T> pager;
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
	
	private MessageBundleManager entityStrings = MessageBundleManager.forPath(
			StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME),
			IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
	
	public static enum SearchType{PK, FILTER, BUTTON};
	
	public SearcherResultWindow2(Class<T> entityClazz) {
		this.entityClazz = entityClazz;
		labelsFmt = FMWEntityUtils.createEntityLabelsMessageFormat();
		try {
			initContent();
			this.addCloseListener(this);
		} catch (FMWException ex) {
			MessageDialog.showExceptions(ex);
		}
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
	
	@SuppressWarnings("unchecked")
	public void initContent() throws FMWException{
		entityConfigData = (EntityConfigData<T>)EntityConfigurationManager.getInstance().getConfigurator(entityClazz).configure();
		manager = (EntityCRUDOperationsManager<T>)EntityCRUDOperationsManager.getInstance(entityClazz);
		selectionEventFunction = SearcherResultWindow2.class.getSimpleName()+"_fn"+Math.abs(hashCode());
		
		setCaption(VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_CAPTION));
		form = new FormPanel(2);//new UIForm(null,100, Unit.PERCENTAGE);
		List<FieldConfigData> sortedFields = FMWEntityUtils.sortByUIControlTypePriority(entityConfigData.getFieldsData(), entityConfigData.getPkFieldName());
		
		for(FieldConfigData fieldCfg : sortedFields){
			Component comp = EntityConfigUtils.createComponentForField(fieldCfg, entityConfigData, labelsFmt, entityStrings);
			form.add(comp, Dimension.percent(100));
			componentMap.put(fieldCfg.getFieldName(), comp);
		}
		
		pager = new Pager2<T, T>(PagerMode.PAGE);
		searchBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
		searchAllBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
		cleanBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
		gridContainer = new GeneratedPropertyContainer(new BeanItemContainer<Object>((Class<? super Object>) entityConfigData.getEntityClass()));
		gridContainer.addGeneratedProperty("actions", new GridUtils.ActionLinkValueGenerator(entityConfigData.getPkFieldName(), null, this, selectionEventFunction, new ActionLinkInfo("select","Seleccionar")));
		
		resultsGrid = new Grid(VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_TABLE_CAPTION), new GeneratedPropertyContainer(gridContainer));
		resultsGrid.setSelectionMode(SelectionMode.NONE);
		resultsGrid.removeAllColumns();
		
		resultsGrid.addColumn("actions")
		.setHeaderCaption("Seleccionar")
		.setRenderer(new HtmlRenderer());
		
		Collection<String> descriptionFields = entityConfigData.getSearchDescriptionFields().isEmpty() ? entityConfigData.getFieldsData().keySet() : entityConfigData.getSearchDescriptionFields();
		GenericStringPropertyGenerator genericPropertyGenerator = new GenericStringPropertyGenerator();
		for(String fieldName : descriptionFields){
			FieldConfigData fieldCfg = entityConfigData.getFieldData(fieldName);
			Renderer<?> renderer = GridUtils.obtainRendererForType(fieldCfg.getFieldType());
			Column column = resultsGrid.addColumn(fieldName);
			column.setHeaderCaption(EntityConfigUtils.getFieldCaptionText(fieldCfg, entityConfigData, labelsFmt, entityStrings));
			if(fieldCfg.isRelationField() && FMWEntityUtils.isEntityClass(fieldCfg.getFieldType())){
				gridContainer.addGeneratedProperty(fieldName, genericPropertyGenerator);
			}
			if(renderer != null)
				column.setRenderer(renderer);	
		}
		
		resultsGrid.setCellStyleGenerator(new SimpleCellStyleGenerator(new SimpleStyleInfo("actions", UIConstants.CENTER_ALIGN_STYLE), new SimpleStyleInfo(entityConfigData.getPkFieldName(), UIConstants.CENTER_ALIGN_STYLE)));
		resultsGrid.setHeightMode(HeightMode.ROW);
		resultsGrid.setHeightByRows(pager.getRowsByPage());
		
		form.addEmptyRow();
		form.addCenteredOnNewRow(Dimension.pixels(EntityConfigUtils.BUTTONS_WIDTH), searchBtn, searchAllBtn, cleanBtn);
		form.add(resultsGrid, 2, Dimension.fullPercent());
		form.addCenteredOnNewRow(Dimension.fullPercent(), pager);
		
		bindComponentsToModel();
		
		bindControlEvents();
		
		form.setSubmitButton(searchBtn);
		form.setResetButton(cleanBtn);
		
		setContent(form);
		setWidth(1000, Unit.PIXELS);
		//setHeight(Page.getCurrent().getBrowserWindowHeight() - 25, Unit.PIXELS);
		center();
		setModal(true);
		
		cleanBtn.click();
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
		}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex){
			throw new FMWException(ex);
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
			ex.printStackTrace();
		}finally{
			form.initFocus();
		}
	}
	
	@Override
	public void windowClose(CloseEvent e) {
		fireSelectionChangeEvent();
	}
	
	private void handleSearch(Button button) throws Exception{
		if(button == searchAllBtn)
			handleClean();
		pager.setFilterDto((T)BeanUtils.cloneBean(beanItem.getBean()));
	}

	private void handleClean() throws Exception{
		Object emptyBean = entityConfigData.getEntityClass().getConstructor().newInstance();
		for(Object propertyId : beanItem.getItemPropertyIds()){
			beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(emptyBean, (String)propertyId));
		}
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
		if(searchType.equals(SearchType.FILTER))
			doFilterSearch(filterValue);
		else if(searchType.equals(SearchType.BUTTON))
			doButtonSearch(filterValue);
		else
			doPKSearch(filterValue);
	}
	
	
	//Para el boton de "buscar" siempre se busca y se abre la ventana
	private void doButtonSearch(Object filterValue){
		if(filterValue != null)
			beanItem.getItemProperty(entityConfigData.getSearchFieldName()).setValue(filterValue);
		searchBtn.click();
		show();
	}
	
	//Busca y abre la ventana solo si no encuentra resultado
	private void doFilterSearch(Object filterValue){
		if(filterValue == null || (filterValue instanceof CharSequence && StringUtils.isEmpty((CharSequence)filterValue))){
			fireResetSelectionEvent();
			return;
		}
		beanItem.getItemProperty(entityConfigData.getSearchFieldName()).setValue(filterValue);
		searchBtn.click();
		if(gridContainer.size() != 1)
			show();
		else{
			selectedValue = (T)gridContainer.firstItemId();
			fireSelectionChangeEvent();
		}
	}
	
	//Busca y retorna unicamente
	private void doPKSearch(Object pkValue){
		if(pkValue == null || (pkValue instanceof CharSequence && StringUtils.isEmpty((CharSequence)pkValue))){
			fireResetSelectionEvent();
			return;
		}
		beanItem.getItemProperty(entityConfigData.getPkFieldName()).setValue(pkValue);
		searchBtn.click();
		if(gridContainer.size() == 1)
			selectedValue = (T)gridContainer.firstItemId();
		fireSelectionChangeEvent();
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
		cleanBtn.click();
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
