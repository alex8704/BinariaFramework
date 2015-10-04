package co.com.binariasystems.fmw.vweb.util;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.UI;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;

public class LocaleMessagesUtil {
	private static MessageFormat UI_CONVENTION_MF;
	public static String getLocalizedMessage(MessageBundleManager messages, String key){
		return messages.getString(key, UI.getCurrent().getPage().getWebBrowser().getLocale());
	}
	
	public static String getLocalizedMessage(String messagesFile, String key){
		String resp = null;
		MessageBundleManager messages = MessageBundleManager.forPath(messagesFile, IOCHelper.getBean(FMWConstants.APPLICATION_DEFAULT_CLASS_FOR_RESOURCE_LOAD_IOC_KEY, Class.class));
		if(messages != null)
			resp = getLocalizedMessage(messages, key);
		return resp;
	}
	
	private static MessageFormat uiConventionMsgFmt(){
		if(UI_CONVENTION_MF == null){
			synchronized (LocaleMessagesUtil.class) {
				UI_CONVENTION_MF = new MessageFormat(UIConstants.UI_CONVENTION_STRINGS_TEMPLATE);
			}
		}
		return UI_CONVENTION_MF;
	}
	
	
	/*
	 * Metodos Utiles para Obtener las cadenas Internacionalizadas para los Labels de los diferentes
	 * Componentes de Interfaz de Usuario usando las convenciones {ViewClassName}.{FieldName}.{Property}
	 * Ej: AuthenticationView.usernameField.caption
	 */
	public static String conventionCaption(Class<?> viewClass, MessageBundleManager messages, String fieldName){
		String key = uiConventionMsgFmt().format(new Object[]{viewClass.getSimpleName(), fieldName, UIConstants.CONVENTION_PROPERTY_CAPTION});
		return getLocalizedMessage(messages, key);
	}
	
	public static String conventionDescription(Class<?> viewClass, MessageBundleManager messages, String fieldName){
		String key = uiConventionMsgFmt().format(new Object[]{viewClass.getSimpleName(), fieldName, UIConstants.CONVENTION_PROPERTY_DESCRIPTION});
		String resp = getLocalizedMessage(messages, key);
		return StringUtils.defaultString(resp).equals(key) ? "" : resp;
	}
	
	public static String conventionTitle(Class<?> viewClass, MessageBundleManager messages){
		String key = uiConventionMsgFmt().format(new Object[]{viewClass.getSimpleName(), "form", UIConstants.CONVENTION_PROPERTY_TITLE});
		return getLocalizedMessage(messages, key);
	}
}
