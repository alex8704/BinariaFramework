package co.com.binariasystems.fmw.vweb.util;

import java.util.Locale;

import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.resources.messages.messages;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherField;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;


public final class VWebUtils {
	
	private static MessageBundleManager commonStringsManager = null;
	
	static{
		commonStringsManager = MessageBundleManager.forPath(VWebCommonConstants.COMMON_MESSAGES_PROPERTIES_FILENAME, messages.class);
	}
	
	public static String getContextPath(){
		return VaadinService.getCurrentRequest().getContextPath();
	}
	
	public static String getContextAbsolutePath(){
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	}
	
	public static String getCommonString(String key){
		return LocaleMessagesUtil.getLocalizedMessage(commonStringsManager, key);
	}
	
	public static Locale getCurrentUserLocale(){
		return UI.getCurrent().getPage().getWebBrowser().getLocale();
	}
	
	public static boolean isVField(Class<?> clazz){
		boolean resp = false;
		
		resp = SearcherField.class.isAssignableFrom(clazz) ||  UIForm.class.isAssignableFrom(clazz) ||
				TreeMenu.class.isAssignableFrom(clazz) || Upload.class.isAssignableFrom(clazz) ||
				Label.class.isAssignableFrom(clazz) || Link.class.isAssignableFrom(clazz) || 
				Button.class.isAssignableFrom(clazz) || Grid.class.isAssignableFrom(clazz) || 
				Field.class.isAssignableFrom(clazz);
		
		return resp;
	}
	
	public static boolean isFocusableControlClass(Class<?> fieldClass){
        return (Field.class.isAssignableFrom(fieldClass) || 
                Label.class.isAssignableFrom(fieldClass) ||
                Link.class.isAssignableFrom(fieldClass) ||
                Upload.class.isAssignableFrom(fieldClass) ||
                Button.class.isAssignableFrom(fieldClass));
    }
}
