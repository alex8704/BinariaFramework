package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditableEntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.security.auditory.AuditoryDataProvider;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.EntityConfigUtils;
import co.com.binariasystems.fmw.vweb.util.GridUtils;
import co.com.binariasystems.fmw.vweb.util.GridUtils.GenericStringPropertyGenerator;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.Renderer;

@SuppressWarnings("serial")
public class EntityCRUDPanel<T> extends FormPanel implements ClickListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityCRUDPanel.class);
	private Class<T> entityClass;
	private Grid dataGrid;
	private Button saveBtn;
	private Button editBtn;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	private Button deleteBtn;
	
	private EntityConfigData<T> entityConfigData;
	private EntityCRUDOperationsManager<T> manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private Pager<T,T> pager;
	private Object initialKeyValue;
	
	private GeneratedPropertyContainer gridContainer;
	private BeanItem<T> beanItem;
	
	private MessageFormat labelsFmt;
	private MessageFormat titleFmt;
	private MessageBundleManager entityStrings;
	
	private boolean attached;
	
	public EntityCRUDPanel(Class<T> entityClass) {
		super(2);
		setWidth(Dimension.percent(90.0f));
		this.entityClass = entityClass;
		labelsFmt = FMWEntityUtils.createEntityLabelsMessageFormat();
		titleFmt = FMWEntityUtils.createEntityFormTitleMessageFormat();
	}
	
	@Override
	public void attach() {
		super.attach();
		if(!attached){
			try {
				initContent();
			} catch (FMWException ex) {
				MessageDialog.showExceptions(ex);
			}
			attached = !attached;
		}
		searchAllBtn.click();
	}
	
	@Override
	public void detach() {
		super.detach();
		cleanBtn.click();
	}
	
	private void initContent() throws FMWException {
		entityConfigData = (EntityConfigData<T>) EntityConfigurationManager.getInstance().getConfigurator(entityClass).configure();
		manager = (EntityCRUDOperationsManager<T>) EntityCRUDOperationsManager.getInstance(entityClass);
		
		String titleKey =  StringUtils.defaultIfEmpty(entityConfigData.getTitleKey(), titleFmt.format(new String[]{entityConfigData.getEntityClass().getSimpleName()}));
		setTitle(LocaleMessagesUtil.getLocalizedMessage(getEntityStrings(), titleKey));
		
		List<FieldConfigData> sortedFields = FMWEntityUtils.sortByUIControlTypePriority(entityConfigData);
		
		for(FieldConfigData fieldCfg : sortedFields){
			Component comp = EntityConfigUtils.createComponentForCrudField(fieldCfg, entityConfigData, labelsFmt, getEntityStrings());
			add(comp, Dimension.percent(100));
			componentMap.put(fieldCfg.getFieldName(), comp);
		}
		
		gridContainer = new GeneratedPropertyContainer(new SortableBeanContainer<T>(entityConfigData.getEntityClass()));
		dataGrid = new Grid("-");
		pager = new Pager<T,T>(PagerMode.PAGE);
		saveBtn = new Button(FontAwesome.SAVE);
		saveBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SAVECAPTION));
		editBtn = new Button(FontAwesome.EDIT);
		editBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_EDITCAPTION));
		searchBtn = new Button(FontAwesome.SEARCH);
		searchBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
		searchAllBtn = new Button(FontAwesome.SEARCH_PLUS);
		searchAllBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
		cleanBtn = new Button(FontAwesome.ERASER);
		cleanBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
		
		configureGrid();
		
		addEmptyRow();
//		addCenteredOnNewRow(Dimension.fullPercent(), pager);
//		addEmptyRow();
		
		if(entityConfigData.isDeleteEnabled()){
			deleteBtn = new Button(FontAwesome.TRASH);
			deleteBtn.setDescription(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_DELETECAPTION));
			addCenteredOnNewRow(saveBtn, editBtn, searchBtn, searchAllBtn, deleteBtn, cleanBtn);
		}else
			addCenteredOnNewRow(saveBtn, editBtn, searchBtn, searchAllBtn, cleanBtn);
		
		addCenteredOnNewRow(Dimension.fullPercent(), dataGrid);
		addCenteredOnNewRow(Dimension.fullPercent(), pager);
		
		bindComponentsToModel();
		
		bindControlEvents();
		
		setSubmitButton(saveBtn);
		setResetButton(cleanBtn);
		cleanBtn.click();
	}
	
	private void configureGrid(){
		dataGrid.setContainerDataSource(gridContainer);
		List<String> columnIds = getGridColumnFields();
		GenericStringPropertyGenerator genericPropertyGenerator = new GenericStringPropertyGenerator();
		for(String columnId : columnIds){
			FieldConfigData fieldCfg = entityConfigData.getFieldData(columnId);
			Renderer<?> renderer = GridUtils.obtainRendererForType(fieldCfg.getFieldType());
			Column column = dataGrid.getColumn(columnId);
			column.setHeaderCaption(EntityConfigUtils.getFieldCaptionText(fieldCfg, entityConfigData, labelsFmt, getEntityStrings()));
			if(fieldCfg.isRelationField() && FMWEntityUtils.isEntityClass(fieldCfg.getFieldType())){
				gridContainer.addGeneratedProperty(columnId, genericPropertyGenerator);
			}
			if(renderer != null)
				column.setRenderer(renderer);	
		}
		
		dataGrid.setColumns(columnIds.toArray());
		dataGrid.setHeightMode(HeightMode.ROW);
		dataGrid.setHeightByRows(pager.getRowsByPage());
		dataGrid.addSelectionListener(new SelectionListener() {
			@Override public void select(SelectionEvent event) {
				T selected = (T)dataGrid.getSelectedRow();
				VWebUtils.resetBeanItemDS(beanItem, selected);
				toggleActionButtonsState();
			}
		});
	}
	
	private List<String> getGridColumnFields(){
		List<String> gridColumnFields = new ArrayList<String>();
		boolean hasGridFields = !entityConfigData.getGridColumnFields().isEmpty();
		Collection<String> columnFields = hasGridFields ? entityConfigData.getGridColumnFields() : entityConfigData.getFieldNames();
		for(String columnField : columnFields){
			if(!columnField.equals(entityConfigData.getPkFieldName()))
				gridColumnFields.add(columnField);
		}
		
		return gridColumnFields;
	}
	
	private void bindComponentsToModel() throws FMWException{
		T bean = null;
		try {
			bean = entityConfigData.getEntityClass().getConstructor().newInstance();
		
			beanItem = new BeanItem<T>(bean, componentMap.keySet());
			for(String fieldName :componentMap.keySet()){
				Component comp = componentMap.get(fieldName);
				if(comp instanceof Field)
					((Field<?>)comp).setPropertyDataSource(beanItem.getItemProperty(fieldName));
			}
			initialKeyValue = beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue();
		} catch (ReflectiveOperationException | SecurityException ex) {
			Throwable cause = FMWExceptionUtils.prettyMessageException(ex);
			throw new FMWException(cause.getMessage(), cause);
		}
	}
	
	private void bindControlEvents(){
		saveBtn.addClickListener(this);
		editBtn.addClickListener(this);
		searchBtn.addClickListener(this);
		searchAllBtn.addClickListener(this);
		cleanBtn.addClickListener(this);
		if(deleteBtn != null)
			deleteBtn.addClickListener(this);
			
		pager.setPageChangeHandler(new PageChangeHandler<T, T>() {
			@Override
			public ListPage<T> loadPage(PageChangeEvent<T> event) throws FMWUncheckedException{
				if(event.getFilterDTO() == null) return new ListPage<T>();
				try {
					return manager.search(event.getFilterDTO(), event.getInitialRow(), event.getRowsByPage(), null);
				} catch (Exception e) {
					throw new FMWUncheckedException(FMWExceptionUtils.prettyMessageException(e));
				}
			}
		});
		
		pager.setPageDataTargetForGrid(dataGrid);
//		pager.setPageDataTarget(new PageDataTarget<T>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void refreshPageData(List<T> pageData) {
//				if(pageData.isEmpty()) return;
//				VWebUtils.resetBeanItemDS(beanItem, pageData.get(0));
//			}
//		});
	}
	
	public void buttonClick(ClickEvent event) {
		try{
			if(event.getButton() == saveBtn)
				handleSave();
			else if(event.getButton() == editBtn)
				handleEdit();
			else if(event.getButton() == searchBtn || event.getButton() == searchAllBtn)
				handleSearch(event.getButton());
			else if(event.getButton() == cleanBtn)
				handleClean();
			else if(event.getButton() == deleteBtn)
				handleDelete();
		}catch(Exception ex){
			MessageDialog.showExceptions(ex, LOGGER);
		}finally{
			toggleActionButtonsState();
			initFocus();
		}
	}
	
	private void handleSave() throws Exception{
		if(!isValid())
			return;
		
		MessageDialog md = new MessageDialog(saveBtn.getDescription(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), saveBtn.getDescription()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					addAuditoryDataIfNecessary(true);
					Object resp = manager.save(beanItem.getBean());
					for(Object propertyId : beanItem.getItemPropertyIds())
						beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(resp, (String)propertyId));
					new MessageDialog(saveBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SUCCESSSAVING)).show();
				}catch(Exception ex){
					MessageDialog.showExceptions(ex, LOGGER);
				}
			}
		});
		
		md.show();
	}
	
	private void handleEdit() throws Exception{
		if((initialKeyValue != null && initialKeyValue.equals(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue())) ||
				beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() == null || !isValid())
			return;
		
		MessageDialog md = new MessageDialog(editBtn.getDescription(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), editBtn.getDescription()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					addAuditoryDataIfNecessary(false);
					manager.edit(beanItem.getBean());
					new MessageDialog(editBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SUCCESSUPDATE)).show();
				}catch(Exception ex){
					MessageDialog.showExceptions(ex, LOGGER);
				}
			}
		});
		
		md.show();
	}
	
	@SuppressWarnings("unchecked")
	private void handleSearch(Button btn) throws Exception{
		if(btn == searchAllBtn)
			handleClean();
		pager.setFilterDto((T) BeanUtils.cloneBean(beanItem.getBean()));
	}
	
	private void handleDelete() throws Exception{
		if((initialKeyValue != null && initialKeyValue.equals(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue())) ||
				beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() == null)
			return;
		
		MessageDialog md = new MessageDialog(deleteBtn.getDescription(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), deleteBtn.getDescription()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					manager.delete(beanItem.getBean());
					handleClean();
					new MessageDialog(deleteBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SUCCESSDELETE)).show();
				}catch(Exception ex){
					MessageDialog.showExceptions(ex, LOGGER);
				}
			}
		});
		
		md.show();
	}
	
	@SuppressWarnings("unchecked")
	private void handleClean(){
//		Object emptyBean = entityConfigData.getEntityClass().getConstructor().newInstance();
		VWebUtils.resetBeanItemDS(beanItem, null);
//		for(Object propertyId : beanItem.getItemPropertyIds())
//			beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(emptyBean, (String)propertyId));
		pager.reset();
		initFocus();
	}
	
	
	private void toggleActionButtonsState(){
		editBtn.setEnabled(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() != null);
		if(deleteBtn != null)
			deleteBtn.setEnabled(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() != null);
	}
	
	private MessageBundleManager getEntityStrings(){
		if(entityStrings == null)
			entityStrings = EntityConfigUtils.createMessageManagerEntityConfig(entityConfigData);
		return entityStrings;
	}
	
	private void addAuditoryDataIfNecessary(boolean isNew) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(!(entityConfigData instanceof AuditableEntityConfigData))return;
		AuditableEntityConfigData<T> configData = (AuditableEntityConfigData<T>)entityConfigData;
		AuditoryDataProvider<?> auditoryDataProvider = EntityConfigurationManager.getInstance().getAuditoryDataProvider();
		String creationUserProperty = configData.getCreationUserFieldCfg() != null ? configData.getCreationUserFieldCfg().getFieldName() : null;
		String modificationUserProperty = configData.getModificationUserFieldCfg() != null ? configData.getModificationUserFieldCfg().getFieldName() : null;
		String creationDateProperty = configData.getCreationDateFieldCfg() != null ? configData.getCreationDateFieldCfg().getFieldName() : null;
		String modificationDateProperty = configData.getModificationDateFieldCfg() != null ?configData.getModificationDateFieldCfg().getFieldName() : null;
		Object auditoryUser = auditoryDataProvider.gettCurrentAuditoryUserForEntityCRUD(VWebUtils.getCurrentHttpRequest());
		Date currentDate = auditoryDataProvider.getCurrentDate();
		//Creation User and Date
		if(isNew && StringUtils.isNotEmpty(creationUserProperty))
			PropertyUtils.setProperty(beanItem.getBean(), creationUserProperty, auditoryUser);
		if(isNew &&StringUtils.isNotEmpty(creationDateProperty))
			PropertyUtils.setProperty(beanItem.getBean(), creationDateProperty, currentDate);
		//Modification User and Date
		if(StringUtils.isNotEmpty(modificationUserProperty))
			PropertyUtils.setProperty(beanItem.getBean(), modificationUserProperty, auditoryUser);
		if(StringUtils.isNotEmpty(modificationDateProperty))
			PropertyUtils.setProperty(beanItem.getBean(), modificationDateProperty, currentDate);
		
	}
}
