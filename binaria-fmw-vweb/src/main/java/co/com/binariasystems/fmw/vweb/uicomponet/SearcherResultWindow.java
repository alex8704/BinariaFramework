package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Page;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Alexander
 *
 */
public class SearcherResultWindow extends Window implements ClickListener{
	private Class<? extends Object> entityClass;
	private UIForm form;
	private Button cleanBtn;
	private Button searchBtn;
	private Button searchAllBtn;
	
	private EntityConfigData entityConfigData;
	private EntityConfigurator configurator;
	private EntityCRUDOperationsManager manager;
	private Map<String, Component> componentMap = new HashMap<String, Component>();
	private BeanItem beanItem;
	private Pager<Object> pager;
	private PageChangeHandler<Object> pageChangeHanlder;
	private Table resultsTable;
	
	private MessageBundleManager entityStrings = MessageBundleManager.forPath(
			StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME),
			IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
	
	private static final int TEXTFIELD_MAX_LENGTH = 100;
	private static final int TEXTAREA_MAX_LENGTH = 4000;
	private static final float BUTTONS_WIDTH = 100.0f;
	private static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	private Object selectedValue;
	private Criteria conditions;
	
	protected SearcherResultWindow(Class<? extends Object> entityClass){
		super();
		setModal(true);
		setWidth(900, Unit.PIXELS);
		setHeight(Page.getCurrent().getBrowserWindowHeight() - 25, Unit.PIXELS);
		this.entityClass = entityClass;
		try{
			initComponents();
			addAttachListener(new AttachListener() {
				
				public void attach(AttachEvent event) {
					//cleanBtn.click();
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
		String title = StringUtils.defaultIfEmpty(getString(titleKey), titleKey);
		setCaption("::: "+VWebUtils.getCommonString(VWebCommonConstants.SEARCH_WIN_CAPTION)+" :::");
		form = new UIForm(null,100, Unit.PERCENTAGE);
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
			form.add(comp, flags, widthPercent);
			componentMap.put(fcd.getFieldName(), comp);
			newRow = (flags & UIForm.LAST) != 0;
			flags = 0;
			widthPercent = 0;
		}
		
		pager = new Pager<Object>(PagerMode.PAGE_PAGINATION);
		searchBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHCAPTION));
		searchAllBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_SEARCHALLCAPTION));
		cleanBtn = new Button(VWebUtils.getCommonString(VWebCommonConstants.MASTER_CRUD_MSG_CLEANCAPTION));
		BeanItemContainer<Object> resultsTableDataSource = new BeanItemContainer<Object>((Class)entityConfigData.getEntityClass());
		resultsTable = new Table("Resultados de Busqueda");
		resultsTable.setContainerDataSource(resultsTableDataSource);
		resultsTable.setSelectable(true);
		
		List<String> visibleCols = new LinkedList<String>();
		visibleCols.add(entityConfigData.getPkFieldName());
		boolean hasDescriptionFields = entityConfigData.getSearchDescriptionFields() != null && entityConfigData.getSearchDescriptionFields().size() > 0;
		
		SimpleFieldColumnGenerator simpleColumnGenerator = new SimpleFieldColumnGenerator();
		resultsTable.addGeneratedColumn(entityConfigData.getPkFieldName(), new PrimaryFieldColumnGenerator());
		
		for(String fieldName : entityConfigData.getFieldsData().keySet()){
			FieldConfigData fieldCfg = entityConfigData.getFieldsData().get(fieldName);
			if(fieldName.equals(entityConfigData.getPkFieldName())) continue;
			if(hasDescriptionFields && entityConfigData.getSearchDescriptionFields().contains(fieldName)){
				visibleCols.add(fieldName);
				if(fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()))
					resultsTable.addGeneratedColumn(fieldName, new RelationFieldColumnGenerator((RelationFieldConfigData)fieldCfg));
				else
					resultsTable.addGeneratedColumn(fieldName, simpleColumnGenerator);
				resultsTable.setColumnHeader(fieldName, getString(new StringBuilder("entity.").append(entityConfigData.getEntityClass().getSimpleName()).append(".").append(fieldName).append(".caption").toString()));
			}else if(!hasDescriptionFields){
				visibleCols.add(fieldName);
				if(fieldCfg instanceof RelationFieldConfigData && !TypeHelper.isBasicType(fieldCfg.getFieldType()))
					resultsTable.addGeneratedColumn(fieldName, new RelationFieldColumnGenerator((RelationFieldConfigData)fieldCfg));
				else
					resultsTable.addGeneratedColumn(fieldName, simpleColumnGenerator);
				resultsTable.setColumnHeader(fieldName, getString(new StringBuilder("entity.").append(entityConfigData.getEntityClass().getSimpleName()).append(".").append(fieldName).append(".caption").toString()));
			}
			
		}
		
		
		resultsTable.setColumnAlignment(entityConfigData.getPkFieldName(), Align.CENTER);
		resultsTable.setVisibleColumns(visibleCols.toArray());
		
		resultsTable.setPageLength(pager.getRowsByPage());
		resultsTable.setMultiSelect(false);
		resultsTable.addStyleName(ValoTheme.TABLE_SMALL);
		form.addCentered();
		form.addCentered(BUTTONS_WIDTH, BUTTONS_WIDTH_UNIT, searchBtn, searchAllBtn, cleanBtn);
		form.add(resultsTable);
		form.add(pager.getContent());
		
		bindComponentsToModel();
		
		bindControlEvents();
		
		form.setSubmitButton(searchBtn);
		form.setResetButton(cleanBtn);
		
		setContent(form);
		handleClean();
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
			TextField widget = new TextField(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setImmediate(true);
			widget.setMaxLength(TEXTFIELD_MAX_LENGTH);
			widget.setNullRepresentation("");
			if(TypeHelper.isNumericType(fieldInfo.getFieldType()))
				widget.setConverter(fieldInfo.getFieldType());
			
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.PASSWORDFIELD){
			PasswordField widget = new PasswordField(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setMaxLength(TEXTFIELD_MAX_LENGTH);

			widget.setValidationVisible(true);
			widget.setNullRepresentation("");
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.TEXTAREA){
			TextArea widget = new TextArea(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setMaxLength(TEXTAREA_MAX_LENGTH);
			widget.setValidationVisible(true);
			widget.setNullRepresentation("");
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.DATEFIELD){
			DateField widget = new DateField(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setDateFormat(FMWConstants.DATE_DEFAULT_FORMAT);
			widget.setValidationVisible(true);
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.COMBOBOX){
			ComboBox widget = new ComboBox(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setValidationVisible(true);
			resp = widget;
			
		}
		else if(controlType == EntityConfigUIControl.RADIO){
			OptionGroup widget = new OptionGroup(StringUtils.defaultString(getString(captionKey), captionKey));
			widget.setValidationVisible(true);
			resp = widget;
			
		}
		else if(controlType == EntityConfigUIControl.CHECKBOX){
			CheckBox widget = new CheckBox(StringUtils.defaultString(getString(captionKey), captionKey));
			resp = widget;
		}
		else if(controlType == EntityConfigUIControl.SEARCHBOX){
			SearcherField widget = new SearcherField(((RelationFieldConfigData)fieldInfo).getRelationEntityClass(), StringUtils.defaultString(getString(captionKey), captionKey));
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
	}
	
	private void bindControlEvents(){
		searchBtn.addClickListener(this);
		searchAllBtn.addClickListener(this);
		cleanBtn.addClickListener(this);
		pageChangeHanlder = new PageChangeHandler<Object>() {
			public ListPage<Object> loadPage(PageChangeEvent event) throws Exception {
				return manager.searchForFmwComponent(event.getFilterDTO(), event.getInitialRow(), event.getRowsByPage(), conditions);
			}
		};
		
		pager.setPageChangeHandler(pageChangeHanlder);
		
		pager.addObserver(new Pager.TableDataLoadObserver(resultsTable));
		
		resultsTable.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					selectedValue = event.getItemId();
					close();
				}
			}
		});
	}
	
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
	
	private void handleSearch(Button button) throws Exception{
		if(button == searchAllBtn)
			handleClean();
		pager.setSearchDTO(BeanUtils.cloneBean(beanItem.getBean()));
	}

	private void handleClean() throws Exception{
		Object emptyBean = entityConfigData.getEntityClass().getConstructor().newInstance();
		for(Object propertyId : beanItem.getItemPropertyIds()){
			beanItem.getItemProperty(propertyId).setValue(PropertyUtils.getProperty(emptyBean, (String)propertyId));
		}
		pager.reset();
		selectedValue = null;
		form.initFocus();
	}
	
	public boolean openSearch(Object param, boolean openAlthoughFound){
		boolean wasOpened = true;
		cleanBtn.click();
		if(param != null){
			beanItem.getItemProperty(entityConfigData.getSearchFieldName()).setValue(param);
			searchBtn.click();
			if(resultsTable.getContainerDataSource().size() == 1 && !openAlthoughFound){
				selectedValue = resultsTable.firstItemId();
				wasOpened = false;
			}
		}
		
		if(wasOpened)
			UI.getCurrent().addWindow(this);
		return wasOpened;
	}
	
	public void openSearchByPk(Object param){
		cleanBtn.click();
		if(param != null){
			beanItem.getItemProperty(entityConfigData.getPkFieldName()).setValue(param);
			searchBtn.click();
			if(resultsTable.getContainerDataSource().size() == 1){
				selectedValue = resultsTable.firstItemId();
			}
		}
	}
	
	private String getString(String key){
		return entityStrings.getString(key);
	}
	
	public Object getSelectedValue(){
		return selectedValue;
	}
	
	public void clearValue(){
		selectedValue = null;
	}
	
	private class SimpleFieldColumnGenerator implements ColumnGenerator{
		public Object generateCell(Table source, final Object itemId, Object columnId){
			String resp = null;
			if(columnId != null){
				try {
					resp = TypeHelper.objectToString(source.getItem(itemId).getItemProperty(columnId).getValue());
				} catch (Exception e) {
					e.printStackTrace();
					Object propVal = source.getItem(itemId).getItemProperty(columnId).getValue();
					resp = propVal != null ? propVal.toString() : "-";
				}
			}
			return resp;
		}
	}
	
	private class RelationFieldColumnGenerator implements ColumnGenerator{
		private RelationFieldConfigData fieldCfg;
		
		private RelationFieldColumnGenerator(){
			
		}
		public RelationFieldColumnGenerator(RelationFieldConfigData fieldCfg){
			this();
			this.fieldCfg = fieldCfg;
		}
		
		public Object generateCell(Table source, final Object itemId, Object columnId){
			String resp = null;
			if(columnId != null){
				try {
					resp = FMWEntityUtils.generateStringRepresentationForField(source.getItem(itemId).getItemProperty(columnId).getValue(), FMWConstants.WHITE_SPACE);
				} catch (Exception e) {
					e.printStackTrace();
					Object propVal = source.getItem(itemId).getItemProperty(columnId).getValue();
					resp = propVal != null ? propVal.toString() : "-";
				}
			}
			return resp;
		}
	}
	
	
	/**
	 * Generador de la Columna del Campo Principal
	 * 
	 * @author Alexander
	 *
	 */
	private class PrimaryFieldColumnGenerator implements ColumnGenerator{
		public Object generateCell(Table source, final Object itemId, Object columnId){
			if(columnId != null){
				Button resp = new Button(source.getItem(itemId).getItemProperty(columnId).getValue().toString());
				resp.addStyleName(ValoTheme.BUTTON_LINK);
				resp.addClickListener(new ClickListener() {
					public void buttonClick(ClickEvent event) {
						selectedValue = itemId;
						SearcherResultWindow.this.close();
					}
				});
				return resp;
			}
			return null;
		}
	}


	public Criteria getConditions() {
		return conditions;
	}


	public void setConditions(Criteria conditions) {
		this.conditions = conditions;
	}
	
	
	
}
