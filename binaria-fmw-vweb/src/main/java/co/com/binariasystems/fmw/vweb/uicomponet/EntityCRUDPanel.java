package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager2.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.EntityConfigUtils;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class EntityCRUDPanel<T> extends UIForm implements ClickListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityCRUDPanel.class);
	private Class<T> entityClass;
	private Button saveBtn;
	private Button editBtn;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	private Button deleteBtn;
	
	private EntityConfigData<T> entityConfigData;
	private EntityCRUDOperationsManager<T> manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private Pager2<T,T> pager;
	private PageChangeHandler<T, T> pageChangeHanlder;
	private Object initialKeyValue;
	
	private BeanItem<T> beanItem;
	
	private MessageFormat labelsFmt;
	private MessageFormat titleFmt;
	private MessageBundleManager entityStrings = MessageBundleManager.forPath(
			StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME),
			IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
	
	private boolean attached;
	
	
	private static final float BUTTONS_WIDTH = 100.0f;
	private static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	private static final int TEXTFIELD_MAX_LENGTH = 100;
	private static final int TEXTAREA_MAX_LENGTH = 4000;
	
	private EntityCRUDPanel() {
		super(null, 90.0f, Unit.PERCENTAGE);
	}
	
	public EntityCRUDPanel(Class<T> entityClass) {
		super(null, 90.0f, Unit.PERCENTAGE);
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
	}
	
	@Override
	public void detach() {
		super.detach();
		cleanBtn.click();
	}
	
	@SuppressWarnings("unchecked")
	private void initContent() throws FMWException {
		entityConfigData = (EntityConfigData<T>) EntityConfigurationManager.getInstance().getConfigurator(entityClass).configure();
		manager = (EntityCRUDOperationsManager<T>) EntityCRUDOperationsManager.getInstance(entityClass);
		
		String titleKey =  StringUtils.defaultIfEmpty(entityConfigData.getTitleKey(), titleFmt.format(new String[]{entityConfigData.getEntityClass().getSimpleName()}));
		setTitle(LocaleMessagesUtil.getLocalizedMessage(entityStrings, titleKey));
		
		List<FieldConfigData> controlsList = FMWEntityUtils.sortByUIControlTypePriority(entityConfigData.getFieldsData(), entityConfigData.getPkFieldName());
		float widthPercent = 0;
		int flags = 0;
		boolean newRow = true;
		
		for(int i = 0; i < controlsList.size(); i++){
			FieldConfigData fcd = controlsList.get(i);
			if(fcd.getFieldUIControl() == EntityConfigUIControl.RADIO || fcd.getFieldUIControl() == EntityConfigUIControl.CHECKBOX || fcd.getFieldName().equals(entityConfigData.getPkFieldName())){
				flags = UIForm.FIRST | UIForm.LAST;
				widthPercent = 100;
			}else{
				flags = newRow ? (flags | UIForm.FIRST) : flags;
				if(i == controlsList.size() - 1 || controlsList.get(i+1).getFieldUIControl() == EntityConfigUIControl.RADIO || 
						controlsList.get(i+1).getFieldUIControl() == EntityConfigUIControl.CHECKBOX || !newRow)
					flags = flags | UIForm.LAST;
				widthPercent = ((flags & UIForm.FIRST) != 0 && (flags & UIForm.LAST) != 0 && i < controlsList.size() - 1) ? 100 : 50;
			}
			
			Component comp = EntityConfigUtils.createComponentForField(fcd, entityConfigData, labelsFmt, entityStrings);
			add(comp, flags, widthPercent);
			componentMap.put(fcd.getFieldName(), comp);
			newRow = (flags & UIForm.LAST) != 0;
			flags = 0;
			widthPercent = 0;
		}
		
		pager = new Pager2<T,T>(PagerMode.ITEM);
		saveBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SAVECAPTION));
		editBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_EDITCAPTION));
		searchBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
		searchAllBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
		cleanBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
		
		addCentered();
		add(pager);
		addCentered();
		
		if(entityConfigData.isDeleteEnabled()){
			deleteBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_DELETECAPTION));
			addCentered(BUTTONS_WIDTH, BUTTONS_WIDTH_UNIT, saveBtn, editBtn, searchBtn, searchAllBtn, deleteBtn, cleanBtn);
		}else
			addCentered(BUTTONS_WIDTH, BUTTONS_WIDTH_UNIT, saveBtn, editBtn, searchBtn, searchAllBtn, cleanBtn);
		
		bindComponentsToModel();
		
		bindControlEvents();
		
		setSubmitButton(saveBtn);
		setResetButton(cleanBtn);
		cleanBtn.click();
	}
	
	private void bindComponentsToModel() throws FMWException{
		Object bean = null;
		try {
			bean = entityConfigData.getEntityClass().getConstructor().newInstance();
		
			beanItem = new BeanItem(bean, componentMap.keySet());
			for(String fieldName :componentMap.keySet()){
				Component comp = componentMap.get(fieldName);
				if(comp instanceof Field)
					((Field<?>)comp).setPropertyDataSource(beanItem.getItemProperty(fieldName));
			}
			initialKeyValue = beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			throw new FMWException(ex);
		}
	}
	
	private void bindControlEvents(){
		saveBtn.addClickListener(this);
		editBtn.addClickListener(this);
		searchBtn.addClickListener(this);
		searchAllBtn.addClickListener(this);
		cleanBtn.addClickListener(this);
		if(deleteBtn == null) return;
			deleteBtn.addClickListener(this);
			
		pager.setPageChangeHandler(new PageChangeHandler<T, T>() {
			@Override
			public ListPage<T> loadPage(PageChangeEvent<T> event) throws FMWUncheckedException{
				if(event.getFilterDTO() == null) return new ListPage<T>();
				try {
					return manager.search(event.getFilterDTO(), event.getInitialRow(), event.getRowsByPage(), null);
				} catch (Exception e) {
					Throwable cause = FMWExceptionUtils.prettyMessageException(e);
					throw new FMWUncheckedException(cause.getMessage(), cause);
				}
			}
		});
		
		pager.setPageDataTarget(new PageDataTarget<T>() {
			@Override
			public void refreshPageData(List<T> pageData) {
				try{
					if(pageData.isEmpty()) return;
					for(Object propertyId : beanItem.getItemPropertyIds())
						beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(pageData.get(0), (String)propertyId));
				}catch(ReadOnlyException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex){
					MessageDialog.showExceptions(ex, LOGGER);
				}
			}
		});
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
			initFocus();
		}
	}
	
	private void handleSave() throws Exception{
		if(!isValid())
			return;
		
		MessageDialog md = new MessageDialog(saveBtn.getCaption(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), saveBtn.getCaption()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
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
				beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() == null)
			return;
		
		if(!isValid())
			return;
		
		MessageDialog md = new MessageDialog(editBtn.getCaption(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), editBtn.getCaption()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					manager.edit(beanItem.getBean());
					new MessageDialog(editBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SUCCESSUPDATE)).show();
				}catch(Exception ex){
					MessageDialog.showExceptions(ex, LOGGER);
				}
			}
		});
		
		md.show();
	}
	
	private void handleSearch(Button btn) throws Exception{
		if(btn == searchAllBtn)
			handleClean();
		pager.setFilterDto((T) BeanUtils.cloneBean(beanItem.getBean()));
	}
	
	private void handleDelete() throws Exception{
		if((initialKeyValue != null && initialKeyValue.equals(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue())) ||
				beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() == null)
			return;
		
		MessageDialog md = new MessageDialog(deleteBtn.getCaption(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), deleteBtn.getCaption()), Type.QUESTION);
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
	
	private void handleClean() throws Exception{
		Object emptyBean = entityConfigData.getEntityClass().getConstructor().newInstance();
		for(Object propertyId : beanItem.getItemPropertyIds())
			beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(emptyBean, (String)propertyId));
		pager.reset();
		initFocus();
	}
}
