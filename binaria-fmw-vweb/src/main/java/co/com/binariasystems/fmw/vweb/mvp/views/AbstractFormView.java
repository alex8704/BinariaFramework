package co.com.binariasystems.fmw.vweb.mvp.views;

import org.slf4j.Logger;

import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.mvp.event.UIEvent;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;

public abstract class AbstractFormView extends FormPanel {
	protected MessageBundleManager messages;
	protected EventBus eventBus;
	
	public AbstractFormView() {
		super();
	}

	public AbstractFormView(int columns, String title) {
		super(columns, title);
	}

	public AbstractFormView(int columns) {
		super(columns);
	}

	public AbstractFormView(String title) {
		super(title);
	}

	public void setMessages(MessageBundleManager messages) {
		this.messages = messages;
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	protected void fireEvent(UIEvent<?> event){
		eventBus.fireEvent(event);
	}

	public String getText(String key){
		String resp = null;
		if(messages != null)
			resp = LocaleMessagesUtil.getLocalizedMessage(messages, key);
		return resp;
	}
	
	public String conventionCaption(String fieldName){
		String resp = null;
		if(messages != null)
			resp = LocaleMessagesUtil.conventionCaption(getClass(), messages, fieldName);
		return resp;
	}
	
	public String conventionDescription(String fieldName){
		String resp = null;
		if(messages != null)
			resp = LocaleMessagesUtil.conventionDescription(getClass(), messages, fieldName);
		return resp;
	}
	
	public String conventionTitle(){
		String resp = null;
		if(messages != null)
			resp = LocaleMessagesUtil.conventionTitle(getClass(), messages);
		return resp;
	}
	
	protected void handleError(Throwable throwable, Logger logger){
		MessageDialog.showExceptions(throwable);
		if(logger != null)
			logger.error("error", throwable);
		else
			throwable.printStackTrace();
	}
}
