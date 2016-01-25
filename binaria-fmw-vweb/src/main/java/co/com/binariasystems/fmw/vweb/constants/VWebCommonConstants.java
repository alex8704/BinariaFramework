package co.com.binariasystems.fmw.vweb.constants;

import co.com.binariasystems.fmw.vweb.resources.resources;

public interface VWebCommonConstants {
	final String COMMON_MESSAGES_PROPERTIES_FILENAME = resources.messagesPackage() + ".common_messages";
	final String DEFAULT_AUTH_MESSAGES_PATH = resources.messagesPackage() + ".auth_messages";
	final String ENTITY_STRINGS_PROPERTIES_FILENAME = resources.messagesPackage() + ".entities_strings";
	final String APP_MVP_VIEWPROVIDER_VIEWS_PACKAGES_IOC_KEY = "application.mvp.viewProvider.viewsPackages";
	final String ENTITYCRUD_VIEWCREATOR_URLPATTERN = "((/)[a-zA-Z_0-9]+)*((/_masterentity/)([a-zA-Z_0-9]*))";
	final String DEFAULT_VIEWCREATOR_URLPATTERN = "((/)[a-z\\-\\$\\|_0-9]+)+";
	
	final String CONVENTION_PROPERTY_CAPTION = "caption";
	final String CONVENTION_PROPERTY_DESCRIPTION = "description";
	final String CONVENTION_PROPERTY_TITLE = "title";
	final String UI_CONVENTION_STRINGS_TEMPLATE = "{0}.{1}.{2}";
	
	//Id con el cual se obtendra del contexto de IOC la ruta del archivo de personalizacion de Entities
	final String APP_ENTITIES_MESSAGES_FILE_IOC_KEY = "entities_strings.filePath";
	final String MASTER_CRUD_MSG_SAVECAPTION = "masterCRUD.saveBtn.caption";
	final String MASTER_CRUD_MSG_EDITCAPTION = "masterCRUD.editBtn.caption";
	final String MASTER_CRUD_MSG_SEARCHCAPTION = "masterCRUD.searchBtn.caption";
	final String MASTER_CRUD_MSG_SEARCHALLCAPTION = "masterCRUD.searchAllBtn.caption";
	final String MASTER_CRUD_MSG_CLEANCAPTION = "masterCRUD.cleanBtn.caption";
	final String MASTER_CRUD_MSG_DELETECAPTION = "masterCRUD.deleteBtn.caption";
	final String MASTER_CRUD_MSG_SUCCESSSAVING = "masterCRUD.msg.save.success";
	final String MASTER_CRUD_MSG_SUCCESSUPDATE = "masterCRUD.msg.update.success";
	final String MASTER_CRUD_MSG_SUCCESSDELETE = "masterCRUD.msg.delete.success";
	final String MASTER_CRUD_AREYOU_SURE_CONTINUE = "masterCrud.msg.areYouSureContinue";
	final String SEARCH_WIN_CAPTION = "searchResultWin.caption";
	final String SEARCH_WIN_TABLE_CAPTION = "searchResultWin.table.caption";
	final String SEARCH_WIN_CHOOSE_CAPTION = "seracherWindow.chooseColumn.caption";
	
	final String CTX_PARAM_APP_SHORTNAME = "applicationShortName";
	final String CTX_PARAM_APP_COMPLETENAME = "applicationCompleteName";
	
	final String ERROR404PAGE_STATUS_TITLE_KEY = "error404ui.labelTop.caption";
	final String ERROR404PAGE_DETAIL_HEAD_KEY = "error404ui.detailHead.caption";
	final String ERROR404PAGE_DETAIL_MESSAGE_KEY = "error404ui.detailMessage.caption";
	
	
	final String ERROR403PAGE_STATUS_TITLE_KEY = "error403ui.labelTop.caption";
	final String ERROR403PAGE_DETAIL_HEAD_KEY = "error403ui.detailHead.caption";
	final String ERROR403PAGE_DETAIL_MESSAGE_KEY = "error403ui.detailMessage.caption";
	
	final String EVENTBUS_LOOKUP_ID = "eventBus";
	
	final String UIFORM_REQUIRED_ERROR = "uiform.requirederror";
	final String LINK_LABEL_TEMPLATE_TEXT = "linkLabel.templateText";
	final String LINK_LABEL_DISABLE_TEMPLATE_TEXT = "linkLabel.disable.templateText";
	final String ACTION_LINK_TEMPLATE_TEXT = "actionLink.templateText";
	final String ACTION_LINK_DISALE_TEMPLATE_TEXT = "actionLink.disable.templateText";
	final String ACTION_ICON_TEMPLATE_TEXT = "actionIcon.templateText";
	final String ACTION_ICON_DISALE_TEMPLATE_TEXT = "actionIcon.disable.templateText";
	
	final String DEFMSG_ERRORS_FOUND = "defaultmsg.errors.found";
	final String DEFMSG_NUMBERRANGE_VIOLATED = "defaultmsg.numberrange.violated";
	final String DEFMSG_STRINGLENGTH_VIOLATED = "defaultmsg.stringlength.violated";
	
	final String PAGER_NROWS_FOUND = "pager.wasNrows.found";
	final String PAGER_NO_ROWS_FORSHOW = "pager.noRows.forShow";
	final String PAGER_PAGE_CAPTION = "pager.page.caption";
	final String PAGER_PAGE_OF_CAPTION = "pager.page_of.caption";
	final String PAGER_ROWS_CAPTION = "pager.rows.caption";
	
	final String DOWNLOADS_TMP_DIR = "/opt/app/siasms_externals/downloads";
	final String COMPANY_IMAGES_DIR = "/opt/app/siasms_externals/company/img";
	final String APPLICATION_LOGS_DIR = "/opt/app/siasms_externals/logs";
	
	final String MINUTE_24HOURDATE_FORMAT = "dd/MM/yyyy HH:mm";
	final String SHORT_DATE_FORMAT = "dd/MM/yyyy";
	
	final String COMMON_COMPONENTS_SEARCH_LABEL = "common.components.searchLabel";
	final String FIELD_CONVERSION_ERROR_DEFAULT_MSG = "field.convertion_error.defaultMessage";
	
	
	final String USER_LANGUAGE_APPCOOKIE = "binaria-webapp-user-lang";
	final String USER_LANGUAGE_SESSION_ATTRIBUTE = "_VWEBAPP_CURRENT_USER_LANGUAJE";
	
	final String SECURITY_SUBJECT_ATTRIBUTE = org.atmosphere.cpr.FrameworkConfig.SECURITY_SUBJECT;
	
	final String SEPARATOR_NBSP = "&nbsp;";
	
	final String YES_NO_COLUMN_TEMPLATE = "yesNoImageColumn.templateText";
	
	final String VALIDATION_ERRORLIST_TEMPLATE = "form.validationErrors.listTemplate";
	final String VALIDATION_ERRORITEM_TEMPLATE = "form.validationErrors.item";
	
	final String ADDRESS_MAIN_VIA_KEY = "adressEditor.label.main_via";
	final String ADDRESS_SECONDARY_VIA_KEY = "adressEditor.label.secondary_via";
	final String ADDRESS_COMPLEMENTARY_VIA_KEY = "adressEditor.label.complementary_via";
	final String ADDRESS_VIA_TYPE_KEY = "adressEditor.label.via_type";
	final String ADDRESS_NUMBER_KEY = "adressEditor.label.number";
	final String ADDRESS_LETTERS_KEY = "adressEditor.label.letters";
	final String ADDRESS_BIS_KEY = "adressEditor.label.bis";
	final String ADDRESS_QUADRANT_KEY = "adressEditor.label.quadrant";
	final String ADDRESS_COMPLEMENT_KEY = "adressEditor.label.complement";
	final String ADDRESS_GENERATED_KEY = "adressEditor.label.generated_address";
	
	final String COMBOBOX_NOSELEC_SHORT_DESCRIPTION = "comboBox.noSelection.short_description";
	final String COMBOBOX_NOSELEC_DESCRIPTION = "comboBox.noSelection.description";
}
