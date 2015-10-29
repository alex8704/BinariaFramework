package co.com.binariasystems.fmw.vweb.uicomponet;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigUIControl;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurationManager;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigurator;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.AuditFieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.FieldConfigData;
import co.com.binariasystems.fmw.entity.cfg.EntityConfigData.RelationFieldConfigData;
import co.com.binariasystems.fmw.entity.criteria.Criteria;
import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.entity.util.FMWEntityUtils;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.reflec.TypeHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager.PagerMode;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.PrimaryFieldColumnGenerator;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.RelationFieldColumnGenerator;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherResultWindow.SimpleFieldColumnGenerator;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class SearcherResultWindow2<T> extends Window implements ClickListener {
	private Class<T> entityClazz;
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
	private PageChangeHandler<Object, Object> pageChangeHanlder;
	private Grid resultsGrid;
	
	private static final int TEXTFIELD_MAX_LENGTH = 100;
	private static final int TEXTAREA_MAX_LENGTH = 4000;
	private static final float BUTTONS_WIDTH = 100.0f;
	private static final Unit BUTTONS_WIDTH_UNIT = Unit.PIXELS;
	private T selectedValue;
	private Criteria conditions;
	
	private MessageFormat labelsFmt;
	
	private MessageBundleManager entityStrings = MessageBundleManager.forPath(
			StringUtils.defaultIfBlank(IOCHelper.getBean(VWebCommonConstants.APP_ENTITIES_MESSAGES_FILE_IOC_KEY, String.class), VWebCommonConstants.ENTITY_STRINGS_PROPERTIES_FILENAME),
			IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
	
	private boolean attached;
	
	public SearcherResultWindow2(Class<T> entityClazz) {
		this.entityClazz = entityClazz;
	}
	
	@Override
	public void attach() {
		super.attach();
		if(!attached){
			initContent();
			attached = !attached;
		}
	}
	
	public void initContent(){
		configurator = EntityConfigurationManager.getInstance().getConfigurator(entityClazz);
		entityConfigData = configurator.configure();
		manager = EntityCRUDOperationsManager.getInstance(entityClazz);
		
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
		resultsGrid = new Grid("Resultados de Busqueda", resultsTableDataSource);//Table("Resultados de Busqueda");
		resultsGrid.setSelectionMode(SelectionMode.SINGLE);
		
		List<String> visibleCols = new LinkedList<String>();
		visibleCols.add(entityConfigData.getPkFieldName());
		boolean hasDescriptionFields = entityConfigData.getSearchDescriptionFields() != null && entityConfigData.getSearchDescriptionFields().size() > 0;
		
//		SimpleFieldColumnGenerator simpleColumnGenerator = new SimpleFieldColumnGenerator();
//		resultsTable.addGeneratedColumn(entityConfigData.getPkFieldName(), new PrimaryFieldColumnGenerator());
		
		for(FieldConfigData fieldCfg : entityConfigData.getFieldsData().values()){
			if(fieldCfg.getFieldName().equals(entityConfigData.getPkFieldName())) continue;
			if(hasDescriptionFields && entityConfigData.getSearchDescriptionFields().contains(fieldCfg.getFieldName())){
				visibleCols.add(fieldCfg.getFieldName());
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
		int index = 0;
		for(String fieldName : fieldsDataMap.keySet()){
			FieldConfigData fieldCfgdData = fieldsDataMap.get(fieldName);
			if(fieldCfgdData instanceof AuditFieldConfigData) continue;
			if(fieldName.equals(PKFieldName))
				resp.add(0, fieldCfgdData);
			else{
				for(index = 0; index < resp.size(); index++)
					if(fieldCfgdData.getFieldUIControl().ordinal() < resp.get(index).getFieldUIControl().ordinal()) break;
				resp.add(index, fieldCfgdData);
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
	
	private String getString(String key){
		return LocaleMessagesUtil.getLocalizedMessage(entityStrings, key);
	}
	
	
	@Override
	public void buttonClick(ClickEvent event) {
		
	}
}
