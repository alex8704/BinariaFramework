package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

import java.util.LinkedList;
import java.util.List;

public class NamespaceInfo {
	private String path;
	private String messages;
	private List<ViewInfo> views = new LinkedList<ViewInfo>();
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public List<ViewInfo> getViews() {
		return views;
	}
	public void setViews(List<ViewInfo> views) {
		this.views = views;
	}
	
	
}
