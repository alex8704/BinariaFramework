package co.com.binariasystems.fmw.vweb.mvp.dispatcher.data;

import com.vaadin.ui.Component;

public class ViewAndController {
	private Component uiContainer;
	private Object view;
	private Object controller;
	private ViewAndController(){
	}
	
	public ViewAndController(Object view, Component uiContainer){
		this(view, uiContainer, null);
	}
	
	public ViewAndController(Object view, Component uiContainer, Object controller){
		this.view = view;
		this.uiContainer = uiContainer;
		this.controller = controller;
	}
	
	
	public Component getUiContainer() {
		return uiContainer;
	}
	public void setUiContainer(Component uiContainer) {
		this.uiContainer = uiContainer;
	}
	public Object getController() {
		return controller;
	}
	public void setController(Object controller) {
		this.controller = controller;
	}

	public Object getView() {
		return view;
	}

	public void setView(Object view) {
		this.view = view;
	}
	
	
}
