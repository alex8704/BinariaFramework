package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;
import co.com.binariasystems.fmw.vweb.util.converter.DateToTimestampConverter;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class EntityCRUDPanel extends UIForm implements ClickListener{
	private Class entityClass;
	private Button saveBtn;
	private Button editBtn;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	private Button deleteBtn;
	
	private EntityConfigData entityConfigData;
	private EntityConfigurator configurator;
	private EntityCRUDOperationsManager manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private Pager<Object> pager;
	private PageChangeHandler<Object> pageChangeHanlder;
	private Object initialKeyValue;
	
	private BeanItem beanItem;
	
	private MessageFormat requiredMsgFmt = new MessageFormat(VWebUtils.getCommonString(VWebCommonConstants.UIFORM_REQUIRED_ERROR));
	private MessageBundleManager entityStrings = MessageBundleManager.forPath(
			StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME),
			IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
	
	
	private static final float BUTTONS_WIDTH = 100.0f;
	private static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	private static final int TEXTFIELD_MAX_LENGTH = 100;
	private static final int TEXTAREA_MAX_LENGTH = 4000;
	
	private EntityCRUDPanel() {
		super(null, 90.0f, Unit.PERCENTAGE);
	}
	
	public EntityCRUDPanel(Class entityClass) {
		super(null, 90.0f, Unit.PERCENTAGE);
		this.entityClass = entityClass;
		try{
			initComponents();
			addAttachListener(new AttachListener() {
				@Override
				public void attach(AttachEvent event) {
					cleanBtn.click();
				}
			});
		}catch(Exception ex){
			MessageDialog.showExceptions(ex);
			ex.printStackTrace();
		}
	}
	
	private void initComponents() throws Exception {
		configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClass);
		entityConfigData = configurator.configure();
		manager = EntityCRUDOperationsManager.getInstance(entityClass);
		
		String defTitleKey = new StringBuilder("entity.").append(entityConfigData.getEntityClass().getSimpleName()).append(".form.title").toString();
		String titleKey =  StringUtils.defaultIfEmpty(configurator.getTitleKey(), defTitleKey);
		setTitle(StringUtils.defaultIfEmpty(getString(titleKey), titleKey));
		
		List<FieldConfigData> controlsList = sortByUIControlTypePriority(entityConfigData.getFieldsData(), entityConfigData.getPkFieldName());
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
			
			Component comp = createComponentForField(fcd);
			add(comp, flags, widthPercent);
			componentMap.put(fcd.getFieldName(), comp);
			newRow = (flags & UIForm.LAST) != 0;
			flags = 0;
			widthPercent = 0;
		}
		
		pager = new Pager<Object>(PagerMode.ROW_PAGINATION);
		saveBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SAVECAPTION));
		editBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_EDITCAPTION));
		searchBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
		searchAllBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
		cleanBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
		
		addCentered();
		add(pager.getContent());
		addCentered();
		
		if(configurator.isDeleteEnabled()){
			deleteBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_DELETECAPTION));
			addCentered(BUTTONS_WIDTH, BUTTONS_WIDTH_UNIT, saveBtn, editBtn, searchBtn, searchAllBtn, deleteBtn, cleanBtn);
		}else
			addCentered(BUTTONS_WIDTH, BUTTONS_WIDTH_UNIT, saveBtn, editBtn, searchBtn, searchAllBtn, cleanBtn);
		
		bindComponentsToModel();
		
		bindControlEvents();
		
		setSubmitButton(saveBtn);
		setResetButton(cleanBtn);
	}
	
	/**
	 * Metodo usado internamente anter de crear los componentes graficos, para definir
	 * el orden de generacion de cada campo teniendo en cuenta un mecanismo de prioridades
	 * que permite ubicar de la mejor manera los elementos segun el tipo de campo a generar
	 * 
	 * @param fieldsDataMap
	 * @param PKFieldName
	 * @return
	 */
	private List<FieldConfigData> sortByUIControlTypePriority(Map<String, FieldConfigData> fieldsDataMap, String PKFieldName){
		List<FieldConfigData> resp = new ArrayList<EntityConfigData.FieldConfigData>();
		for(String fieldName : fieldsDataMap.keySet()){
			FieldConfigData fcd = fieldsDataMap.get(fieldName);
			if(fcd instanceof AuditFieldConfigData) continue;
			if(fieldName.equals(PKFieldName))
				resp.add(0, fcd);
			else{
				int i = 0;
				for(i = 0; i < resp.size(); i++){
					if(fcd.getFieldUIControl().ordinal() < resp.get(i).getFieldUIControl().ordinal())
						break;
				}
				resp.add(i, fcd);
			}
		}
		
		return resp;
	}
	
	
	
	
	private Component createComponentForField(FieldConfigData fieldInfo) throws Exception{
		Component resp = null;
		EntityConfigUIControl controlType = fieldInfo.getFieldUIControl();
		String captionKey = configurator.getFieldLabelMappings().get(fieldInfo.getFieldName());
		if(captionKey == null)
			captionKey = new StringBuilder("entity.").append(entityConfigData.getEntityClass().getSimpleName()).append(".").append(fieldInfo.getFieldName()).append(".caption").toString();
		
		if(controlType == EntityConfigUIControl.TEXTFIELD){
			TextField widget = new TextField(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			widget.setImmediate(true);
			widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
			
			widget.setRequired(fieldInfo.getFieldName().equals(entityConfigData.getPkFieldName()) ? false : fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(!fieldInfo.getFieldName().equals(entityConfigData.getPkFieldName()));
			widget.setInvalidCommitted(true);
			widget.setReadOnly(fieldInfo.getFieldName().equals(entityConfigData.getPkFieldName()));
			widget.setNullRepresentation("");
			if(TypeHelper.isNumericType(fieldInfo.getFieldType()))
				widget.setConverter(fieldInfo.getFieldType());
			
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.PASSWORDFIELD){
			PasswordField widget = new PasswordField(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
			
			widget.setRequired(fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(true);
			widget.setInvalidCommitted(true);
			widget.setNullRepresentation("");
			
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.TEXTAREA){
			TextArea widget = new TextArea(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			widget.setMaxLength(TEXTAREA_MAX_LENGTH);
			
			widget.setRequired(fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(true);
			widget.setInvalidCommitted(true);
			widget.setNullRepresentation("");
			
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.DATEFIELD){
			DateField widget = new DateField(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			widget.setDateFormat(FMWConstants.DATE_DEFAULT_FORMAT);
			if(Timestamp.class.isAssignableFrom(fieldInfo.getFieldType()))
				widget.setConverter(new DateToTimestampConverter());
			
			widget.setRequired(fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(true);
			widget.setInvalidCommitted(true);
			
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.COMBOBOX){
			ComboBox widget = new ComboBox(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			
			widget.setRequired(fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(true);
			widget.setInvalidCommitted(true);
			
			resp = widget;
			
		}
		else if(controlType == EntityConfigUIControl.RADIO){
			OptionGroup widget = new OptionGroup(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			
			widget.setRequired(fieldInfo.isMandatory());
			widget.setRequiredError(ValidationUtils.requiredErrorFor(widget.getCaption()));
			widget.setValidationVisible(true);
			widget.setInvalidCommitted(true);
			
			resp = widget;
			
		}
		else if(controlType == EntityConfigUIControl.CHECKBOX){
			CheckBox widget = new CheckBox(StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.SEARCHBOX){
			SearcherField widget = new SearcherField(((RelationFieldConfigData)fieldInfo).getRelationEntityClass(), StringUtils.defaultIfEmpty(getString(captionKey), captionKey));
			widget.setRequired(fieldInfo.isMandatory());
			resp = widget;
		}
		
		if(resp instanceof AbstractSelect){
			if(fieldInfo.isEnumType()){
				BeanItemContainer itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
				((AbstractSelect)resp).setContainerDataSource(itemContainer);
				Class<Enum> enumTypeClazz = (Class)fieldInfo.getFieldType();
				Method valuesMth = enumTypeClazz.getMethod("values", new Class[]{});
				Enum[] vals = (Enum[])valuesMth.invoke(null, new Object[]{});
				
				for(int i = 0; i < vals.length; i++){
					Item item = ((AbstractSelect)resp).addItem(vals[i]);
					((AbstractSelect)resp).setItemCaption(item, vals[i].name().replace("_", " "));
				}
			}else if(fieldInfo.getFixedValues() != null && fieldInfo.getFixedValues().length > 0){
				BeanItemContainer itemContainer = new BeanItemContainer(fieldInfo.getFieldType());
				((AbstractSelect)resp).setContainerDataSource(itemContainer);
				for(Listable value : fieldInfo.getFixedValues()){
					Item item = ((AbstractSelect)resp).addItem(value);
					((AbstractSelect)resp).setItemCaption(item, value.getDescription());
				}
			}else{
				EntityCRUDOperationsManager auxManager = EntityCRUDOperationsManager.getInstance(((RelationFieldConfigData)fieldInfo).getRelationEntityClass());
				List<Object> fieldValues = auxManager.searchWithoutPaging(null);
				BeanItemContainer itemContainer = new BeanItemContainer(fieldInfo.getFieldType(), fieldValues);
				((AbstractSelect)resp).setContainerDataSource(itemContainer);
				for(Object value : fieldValues){
					Item item = ((AbstractSelect)resp).addItem(value);
					((AbstractSelect)resp).setItemCaption(item, FMWEntityUtils.generateStringRepresentationForField(value, FMWConstants.PIPE));
				}
			}
		}
		return resp;
	}
	
	private void bindComponentsToModel() throws Exception{
		Object bean = null;
		bean = entityConfigData.getEntityClass().getConstructor().newInstance();
		beanItem = new BeanItem(bean, componentMap.keySet());
		for(String fieldName :componentMap.keySet()){
			Component comp = componentMap.get(fieldName);
			if(comp instanceof Field)
				((Field)comp).setPropertyDataSource(beanItem.getItemProperty(fieldName));
			else if(comp instanceof SearcherField)
				((SearcherField)comp).setPropertyDataSource(beanItem.getItemProperty(fieldName));
		}
		initialKeyValue = beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue();
	}
	
	private void bindControlEvents(){
		saveBtn.addClickListener(this);
		editBtn.addClickListener(this);
		searchBtn.addClickListener(this);
		searchAllBtn.addClickListener(this);
		cleanBtn.addClickListener(this);
		if(deleteBtn == null) return;
			deleteBtn.addClickListener(this);
			
		pageChangeHanlder = new PageChangeHandler<Object>() {
			@Override
			public ListPage<Object> loadPage(PageChangeEvent event) throws Exception {
				return manager.search(event.getFilterDTO(), event.getInitialRow(), event.getRowsByPage(), null);
			}
		};
		
		pager.setPageChangeHandler(pageChangeHanlder);
		
		pager.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				List<Object> pageList = (List<Object>)arg;
				if(pageList != null && pageList.size() > 0){
					try {
						for(Object propertyId : beanItem.getItemPropertyIds())
							beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(pageList.get(0), (String)propertyId));
					} catch (Exception ex) {
						handleExceptions(ex);
					}
					return;
				}
				new MessageDialog(searchBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.PAGER_NO_ROWS_FORSHOW), Type.WARNING).show();
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
			handleExceptions(ex);
		}finally{
			initFocus();
		}
	}
	
	private void handleSave() throws Exception{
		if(!validate())
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
					handleExceptions(ex);
				}
			}
		});
		
		md.show();
	}
	
	private void handleEdit() throws Exception{
		if((initialKeyValue != null && initialKeyValue.equals(beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue())) ||
				beanItem.getItemProperty(entityConfigData.getPkFieldName()).getValue() == null)
			return;
		
		if(!validate())
			return;
		
		MessageDialog md = new MessageDialog(editBtn.getCaption(), MessageFormat.format(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_AREYOU_SURE_CONTINUE), editBtn.getCaption()), Type.QUESTION);
		md.addYesClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					manager.edit(beanItem.getBean());
					new MessageDialog(editBtn.getCaption(), VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SUCCESSUPDATE)).show();
				}catch(Exception ex){
					handleExceptions(ex);
				}
			}
		});
		
		md.show();
	}
	
	private void handleSearch(Button btn) throws Exception{
		if(btn == searchAllBtn)
			handleClean();
		pager.setSearchDTO(BeanUtils.cloneBean(beanItem.getBean()));
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
					handleExceptions(ex);
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
	
	private void handleExceptions(Exception ex){
		MessageDialog.showExceptions(ex);
		ex.printStackTrace();
	}
	
	private String getString(String key){
		return entityStrings.getString(key);
	}

}
