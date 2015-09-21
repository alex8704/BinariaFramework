package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

public class ViewInfo {
	private String url;
	private Class<?> viewClass;
	private String messages;
	private boolean publicView;
	private boolean rootView;
	private ControllerInfo controllerInfo;
	private String viewBuildMethod;
	private String initMethod;
	private String contentSetterMethod;
	private boolean viewStringsByConventions;
	
	public ViewInfo(){}
	public ViewInfo(String url){
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {	
		this.url = url;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public boolean isPublicView() {
		return publicView;
	}
	public void setPublicView(boolean publicView) {
		this.publicView = publicView;
	}
	public boolean isRootView() {
		return rootView;
	}
	public void setRootView(boolean rootView) {
		this.rootView = rootView;
	}
	public ControllerInfo getControllerInfo() {
		return controllerInfo;
	}
	public void setControllerInfo(ControllerInfo controllerInfo) {
		this.controllerInfo = controllerInfo;
	}
	public Class<?> getViewClass() {
		return viewClass;
	}
	public void setViewClass(Class<?> viewClass) {
		this.viewClass = viewClass;
	}
	public String getViewBuildMethod() {
		return viewBuildMethod;
	}
	public void setViewBuildMethod(String viewBuildMethod) {
		this.viewBuildMethod = viewBuildMethod;
	}
	public String getInitMethod() {
		return initMethod;
	}
	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}
	public String getContentSetterMethod() {
		return contentSetterMethod;
	}
	public void setContentSetterMethod(String contentSetterMethod) {
		this.contentSetterMethod = contentSetterMethod;
	}
	public boolean isViewStringsByConventions() {
		return viewStringsByConventions;
	}
	public void setViewStringsByConventions(boolean viewStringsByConventions) {
		this.viewStringsByConventions = viewStringsByConventions;
	}
	
}
