package co.com.binariasystems.fmw.vweb.mvp.views;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;
import co.com.binariasystems.fmw.vweb.mvp.event.UIEvent;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;

public abstract class AbstractView {
	private static MessageFormat UI_CONVENTION_MF;
	protected MessageBundleManager messages;
	protected EventBus eventBus;

	public void setMessages(MessageBundleManager messages) {
		this.messages = messages;
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	protected void fireEvent(UIEvent event){
		eventBus.fireEvent(event);
	}

	public String getText(String key){
		String resp = null;
		if(messages != null)
			resp = StringUtils.defaultIfEmpty(LocaleMessagesUtil.getLocalizedMessage(messages, key), key);
		return resp;
	}
	
	public String conventionCaption(String fieldName){
		if(UI_CONVENTION_MF == null){
			synchronized (this) {
				UI_CONVENTION_MF = new MessageFormat(UIConstants.UI_CONVENTION_STRINGS_TEMPLATE);
			}
		}
		String key = UI_CONVENTION_MF.format(new Object[]{getClass().getSimpleName(), fieldName, UIConstants.CONVENTION_PROPERTY_CAPTION});
		return getText(key);
	}
	
	public String conventionDescription(String fieldName){
		if(UI_CONVENTION_MF == null){
			synchronized (this) {
				UI_CONVENTION_MF = new MessageFormat(UIConstants.UI_CONVENTION_STRINGS_TEMPLATE);
			}
		}
		String key = UI_CONVENTION_MF.format(new Object[]{getClass().getSimpleName(), fieldName, UIConstants.CONVENTION_PROPERTY_DESCRIPTION});
		return getText(key);
	}
	
	public String conventionTitle(){
		if(UI_CONVENTION_MF == null){
			synchronized (this) {
				UI_CONVENTION_MF = new MessageFormat(UIConstants.UI_CONVENTION_STRINGS_TEMPLATE);
			}
		}
		String key = UI_CONVENTION_MF.format(new Object[]{getClass().getSimpleName(), "form", UIConstants.CONVENTION_PROPERTY_TITLE});
		return getText(key);
	}
	
	protected void handleError(Throwable throwable, Logger logger){
		MessageDialog.showExceptions(throwable);
		if(logger != null)
			logger.error("error", throwable);
		else
			throwable.printStackTrace();
	}
}
