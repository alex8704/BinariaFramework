package co.com.binariasystems.fmw.vweb.constants;

import co.com.binariasystems.fmw.vweb.resources.messages.messages;

public interface VWebCommonConstants {
	public static final String COMMON_MESSAGES_PROPERTIES_FILENAME = messages.class.getPackage().getName() + ".common_messages";
	public static final String ENTITY_STRINGS_PROPERTIES_FILENAME = messages.class.getPackage().getName() + ".entities_strings";
	public static final String APP_MVP_VIEWPROVIDER_VIEWS_PACKAGES_IOC_KEY = "application.mvp.viewProvider.viewsPackages";
	public static final String ENTITYCRUD_VIEWCREATOR_URLPATTERN = "((/)[a-zA-Z_0-9]+)*((/_masterentity/)([a-zA-Z_0-9]*))";
	public static final String DEFAULT_VIEWCREATOR_URLPATTERN = "((/)[a-z\\-\\$\\|_0-9]+)+";
	
	//Id con el cual se obtendra del contexto de IOC la ruta del archivo de personalizacion de Entities
	public static final String APP_ENTITIES_MESSAGES_FILE_IOC_KEY = "entities_strings.filePath";
	public static final String MASTER_CRUD_MSG_SAVECAPTION = "masterCRUD.saveBtn.caption";
	public static final String MASTER_CRUD_MSG_EDITCAPTION = "masterCRUD.editBtn.caption";
	public static final String MASTER_CRUD_MSG_SEARCHCAPTION = "masterCRUD.searchBtn.caption";
	public static final String MASTER_CRUD_MSG_SEARCHALLCAPTION = "masterCRUD.searchAllBtn.caption";
	public static final String MASTER_CRUD_MSG_CLEANCAPTION = "masterCRUD.cleanBtn.caption";
	public static final String MASTER_CRUD_MSG_DELETECAPTION = "masterCRUD.deleteBtn.caption";
	public static final String MASTER_CRUD_MSG_SUCCESSSAVING = "masterCRUD.msg.save.success";
	public static final String MASTER_CRUD_MSG_SUCCESSUPDATE = "masterCRUD.msg.update.success";
	public static final String MASTER_CRUD_MSG_SUCCESSDELETE = "masterCRUD.msg.delete.success";
	public static final String MASTER_CRUD_AREYOU_SURE_CONTINUE = "masterCrud.msg.areYouSureContinue";
	public static final String SEARCH_WIN_CAPTION = "searchResultWin.caption";
	
	public static final String CTX_PARAM_APP_SHORTNAME = "applicationShortName";
	public static final String CTX_PARAM_APP_COMPLETENAME = "applicationCompleteName";
	
	public static final String ERROR404PAGE_STATUS_TITLE_KEY = "error404ui.labelTop.caption";
	public static final String ERROR404PAGE_DETAIL_HEAD_KEY = "error404ui.detailHead.caption";
	public static final String ERROR404PAGE_DETAIL_MESSAGE_KEY = "error404ui.detailMessage.caption";
	
	
	public static final String ERROR403PAGE_STATUS_TITLE_KEY = "error403ui.labelTop.caption";
	public static final String ERROR403PAGE_DETAIL_HEAD_KEY = "error403ui.detailHead.caption";
	public static final String ERROR403PAGE_DETAIL_MESSAGE_KEY = "error403ui.detailMessage.caption";
	
	public static final String EVENTBUS_LOOKUP_ID = "eventBus";
	
	public static final String UIFORM_REQUIRED_ERROR = "uiform.requirederror";
	
	public static final String DEFMSG_ERRORS_FOUND = "defaultmsg.errors.found";
	public static final String DEFMSG_NUMBERRANGE_VIOLATED = "defaultmsg.numberrange.violated";
	public static final String DEFMSG_STRINGLENGTH_VIOLATED = "defaultmsg.stringlength.violated";
	
	public static final String PAGER_NROWS_FOUND = "pager.wasNrows.found";
	public static final String PAGER_NO_ROWS_FORSHOW = "pager.noRows.forShow";
	public static final String PAGER_PAGE_CAPTION = "pager.page.caption";
	public static final String PAGER_ROWS_CAPTION = "pager.rows.caption";
	
	public static final String DOWNLOADS_TMP_DIR = "/opt/app/siasms_externals/downloads";
	public static final String COMPANY_IMAGES_DIR = "/opt/app/siasms_externals/company/img";
	public static final String APPLICATION_LOGS_DIR = "/opt/app/siasms_externals/logs";
	
	public static final String MINUTE_24HOURDATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final String SHORT_DATE_FORMAT = "dd/MM/yyyy";
	
	public static final String COMMON_COMPONENTS_SEARCH_LABEL = "common.components.searchLabel";
	public static final String FIELD_CONVERSION_ERROR_DEFAULT_MSG = "field.convertion_error.defaultMessage";
	
	
	public static final String USER_LANGUAGE_APPCOOKIE = "binaria-webapp-user-lang";
	public static final String USER_LANGUAGE_SESSION_ATTRIBUTE = "_VWEBAPP_CURRENT_USER_LANGUAJE";
	
	
}
