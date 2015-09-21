package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

public class ControllerInfo {
	private Class<?> controllerClass;
	private String beforeLoadMethod;
	private String beforeUnloadMethod;
	private String initMethod;
	
	public Class<?> getControllerClass() {
		return controllerClass;
	}
	public void setControllerClass(Class<?> controllerClass) {
		this.controllerClass = controllerClass;
	}
	public String getBeforeLoadMethod() {
		return beforeLoadMethod;
	}
	public void setBeforeLoadMethod(String beforeLoadMethod) {
		this.beforeLoadMethod = beforeLoadMethod;
	}
	public String getBeforeUnloadMethod() {
		return beforeUnloadMethod;
	}
	public void setBeforeUnloadMethod(String beforeUnloadMethod) {
		this.beforeUnloadMethod = beforeUnloadMethod;
	}
	public String getInitMethod() {
		return initMethod;
	}
	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}
	
}
