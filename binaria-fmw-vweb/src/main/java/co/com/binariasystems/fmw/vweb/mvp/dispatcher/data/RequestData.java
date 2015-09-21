package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;

public class RequestData {
	private String url;
	private String pathInfo;
	private EventBus eventBus;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPathInfo() {
		return pathInfo;
	}
	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}
	public EventBus getEventBus() {
		return eventBus;
	}
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
}
