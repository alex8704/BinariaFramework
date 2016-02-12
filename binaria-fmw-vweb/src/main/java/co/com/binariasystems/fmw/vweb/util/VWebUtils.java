package co.com.binariasystems.fmw.vweb.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.resources.resources;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action.Notifier;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;


public final class VWebUtils {
	
	private static MessageBundleManager commonStringsManager = null;
	
	static{
		commonStringsManager = MessageBundleManager.forPath(VWebCommonConstants.COMMON_MESSAGES_PROPERTIES_FILENAME, resources.class);
	}
	
	public static HttpServletRequest getCurrentHttpRequest(){
		VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
		return vaadinRequest.getHttpServletRequest();
	}
	
	public static String getContextPath(){
		return ((VaadinServletService)VaadinService.getCurrent()).getServlet().getServletContext().getContextPath();
	}
	
	public static String getContextAbsolutePath(){
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	}
	
	public static String getCommonString(String key){
		return LocaleMessagesUtil.getLocalizedMessage(commonStringsManager, key);
	}
	
	public static Locale getCurrentUserLocale(){
		return UI.getCurrent().getLocale();
	}
	
	public static String getComboBoxNoSelectionShortDescription(){
		return getCommonString(VWebCommonConstants.COMBOBOX_NOSELEC_SHORT_DESCRIPTION);
	}
	
	public static String getComboBoxNoSelectionDescription(){
		return getCommonString(VWebCommonConstants.COMBOBOX_NOSELEC_DESCRIPTION);
	}
	
	public static boolean isVField(Class<?> clazz){
		boolean resp = false;
		
		resp = FormPanel.class.isAssignableFrom(clazz) || Panel.class.isAssignableFrom(clazz) ||
				TreeMenu.class.isAssignableFrom(clazz) || Upload.class.isAssignableFrom(clazz) ||
				Label.class.isAssignableFrom(clazz) || Link.class.isAssignableFrom(clazz) || 
				Button.class.isAssignableFrom(clazz) || Grid.class.isAssignableFrom(clazz) || 
				Field.class.isAssignableFrom(clazz);
		
		return resp;
	}
	
	public static boolean isFocusableControlClass(Class<?> fieldClass){
        return (Field.class.isAssignableFrom(fieldClass) || 
                Label.class.isAssignableFrom(fieldClass) ||
//                Link.class.isAssignableFrom(fieldClass) ||
                Upload.class.isAssignableFrom(fieldClass) ||
                Button.class.isAssignableFrom(fieldClass));
    }
	
	public static boolean isArrowUpNavigableField(Class<?> clazz){
		return (isArrowDownNavigableField(clazz) || DateField.class.isAssignableFrom(clazz));
	}
	
	public static  boolean isArrowDownNavigableField(Class<?> clazz){
		return (Button.class.isAssignableFrom(clazz) || CheckBox.class.isAssignableFrom(clazz) || CustomField.class.isAssignableFrom(clazz) ||
				TextField.class.isAssignableFrom(clazz) || PasswordField.class.isAssignableFrom(clazz) || Upload.class.isAssignableFrom(clazz));
	}
	
	public static boolean isEnterNavigableField(Class<?> clazz){
		return (DateField.class.isAssignableFrom(clazz) || AbstractSelect.class.isAssignableFrom(clazz) ||
		Slider.class.isAssignableFrom(clazz) || ProgressBar.class.isAssignableFrom(clazz) ||
		isArrowDownNavigableField(clazz) || OptionGroup.class.isAssignableFrom(clazz));
	}
	
	public static List<Component> getKeyNavigableChilds(ComponentContainer container){
		List<Component> navigableChilds = new ArrayList<Component>();
		Component child = null;
		Iterator<Component> iterator = container.iterator();
		while(iterator.hasNext()){
			child = iterator.next();
			if(child instanceof ComponentContainer)
				navigableChilds.addAll(getKeyNavigableChilds((ComponentContainer)child));
			else if(isFocusableControlClass(child.getClass()))
				navigableChilds.add(child);
		}
		return navigableChilds;
	}
	
	public static void applyNavigationActions(Notifier notifier, ComponentContainer container){
		applyNavigationActions(notifier, container, null);
	}
	
	public static void applyNavigationActions(Notifier notifier, ComponentContainer container, final Button submitButton){
		final List<Component> childComponents = getKeyNavigableChilds(container);
		Component auxlastComponent = null;
		Field<?> auxFirstFocusComp = null;
		
		for(Component child : childComponents){
			if(child instanceof Field &&  ((Field<?>)child).isEnabled()  && !((Field<?>)child).isReadOnly())
				auxFirstFocusComp = (Field<?>)child;
			if(child instanceof TextField || child instanceof PasswordField || child instanceof DateField)
				auxlastComponent = child;
		}
		
		final Field<?> firstFocusComp = auxFirstFocusComp;
		final Component lastComponent = auxlastComponent;
		
		notifier.addAction(new ShortcutListener("ControlsNavDown@"+notifier.hashCode(), KeyCode.ARROW_DOWN, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(VWebUtils.isArrowDownNavigableField(target.getClass())){
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i = targetIdx + 1; i < childComponents.size(); i++){
						Component comp = childComponents.get(i);
						if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
							((Focusable)comp).focus();
							return;
						}
					}
					if(firstFocusComp != null)
						firstFocusComp.focus();
				}
			}
		});
		
		//Navegacion con Tecla ARROW_UP
		notifier.addAction(new ShortcutListener("ControlsNavUp@"+notifier.hashCode(), KeyCode.ARROW_UP, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(VWebUtils.isArrowUpNavigableField(target.getClass())){
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i= targetIdx - 1; i >= 0; i--){
						Component comp = childComponents.get(i);
						if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
							((Focusable)comp).focus();
							return;
						}
					}
				}
			}
		});
		
		//Navegacion con Tecla ENTER
		notifier.addAction(new ShortcutListener("EnterPress@"+notifier.hashCode(), KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if(target == lastComponent && lastComponent.isEnabled() && !lastComponent.isReadOnly() && submitButton != null)
					submitButton.click();
				if(VWebUtils.isEnterNavigableField(target.getClass())){
					int targetIdx = childComponents.indexOf(target);
					if(targetIdx < 0)return;
					
					for(int i = targetIdx + 1; i < childComponents.size(); i++){
						Component comp = childComponents.get(i);
						if(comp instanceof Focusable && ((Focusable)comp).isEnabled() && !((Focusable)comp).isReadOnly() ){
							((Focusable)comp).focus();
							return;
						}
					}
					
					if(firstFocusComp != null)
						((Field<?>)firstFocusComp).focus();
				}
			}
		});
	}
	
	public static <T> void resetBeanItemDS(BeanItem<T> beanItem, T newBean) throws FMWUncheckedException{
		try {
			for(Object propertyId : beanItem.getItemPropertyIds())
				beanItem.getItemProperty(propertyId).setValue((newBean == null) ? null : PropertyUtils.getProperty(newBean, propertyId.toString()));
		} catch (ReflectiveOperationException e) {
			throw new FMWUncheckedException(e);
		}
	}
}
