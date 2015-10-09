package co.com.binariasystems.fmw.vweb.mvp.controller;

import java.io.Serializable;
import java.util.Observable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.mvp.event.UIEvent;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;

public abstract class AbstractViewController extends Observable implements Serializable {
	protected MessageBundleManager messages;
	protected EventBus eventBus;
	
	protected <C> C lookupBean(Class<C> daoClazz){
		return IOCHelper.getBean(daoClazz);
	}

	public MessageBundleManager getMessages() {
		return messages;
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
			resp = StringUtils.defaultIfEmpty(LocaleMessagesUtil.getLocalizedMessage(messages, key), key);
		return resp;
	}
	
	protected void handleError(Throwable throwable, Logger logger){
		MessageDialog.showExceptions(throwable);
		if(logger != null)
			logger.error("error", throwable);
	}
	
	protected void notifyObservableEvent(UIEvent<?> observableEvent){
		setChanged();
		notifyObservers(observableEvent);
	}
	
}
