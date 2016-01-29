package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

import java.util.HashMap;
import java.util.Map;

import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;

public class RequestData {
	private String url;
	private String pathInfo;
	private EventBus eventBus;
	private Map<String, String> parameters;
	private boolean popup;
	
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
	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		if(parameters == null)
			parameters = new HashMap<String, String>();
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	/**
	 * @return the popup
	 */
	public boolean isPopup() {
		return popup;
	}
	/**
	 * @param popup the popup to set
	 */
	public void setPopup(boolean popup) {
		this.popup = popup;
	}
	
	
	
}
