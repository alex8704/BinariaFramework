package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.util.codec.Base64;
import co.com.binariasystems.fmw.vweb.mvp.annotation.UIEventHandler;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ControllerInfo;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.mvp.event.PopupViewCloseEvent;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class ViewDispatcherImpl implements ViewDispatcher{
	private Logger LOGGER = LoggerFactory.getLogger(ViewDispatcherImpl.class);
	private ViewProvider viewProvider;
	private EventBus eventBus;
	private Map<String, ViewAndController> loadedViews = new HashMap<String, ViewAndController>();
	private ViewInfo currentViewInfo;
	private ViewInfo currentRootViewInfo;
	private Window currentPopupView;
	private PopupViewCloseEventHandler popupViewCloseHandler;
	
	public ViewDispatcherImpl(){
		popupViewCloseHandler = new PopupViewCloseEventHandler();
	}
	
	@Override
	public void dispatch(ViewDispatchRequest dispatchRequest) throws FMWException {
		RequestData requestData = buildRequestData(dispatchRequest);
		ViewAndController viewAndController = loadedViews.get(requestData.getUrl());
		if(viewAndController == null){
			viewAndController = viewProvider.getView(requestData);
			loadedViews.put(requestData.getUrl(), viewAndController);
		}
		
		ViewInfo viewInfo = viewProvider.getViewInfo(requestData);
		handleControllerLoad(viewInfo, viewAndController, requestData);
		if(dispatchRequest.isPopup()){
			handlePopupViewDispatch(viewInfo, viewAndController, requestData);
			return;
		}
		if(viewInfo != null && viewInfo.isRootView()){
			handleRootViewDispatch(viewInfo, viewAndController, requestData);
			return;
		}
		
		handleViewDispatch(viewInfo, viewAndController, requestData);
	}
	
	private void handleControllerLoad(ViewInfo newViewInfo, ViewAndController newViewAndController, RequestData requestData) throws ViewDispatchException{
		try{
			if(currentViewInfo != null && currentViewInfo.getControllerInfo() != null){
				ViewAndController oldViewAndController = loadedViews.get(currentViewInfo.getUrl());
				String onUnloadMtd = currentViewInfo.getControllerInfo().getBeforeUnloadMethod();
				if(StringUtils.isNotEmpty(onUnloadMtd) && !requestData.isPopup()){
					MethodUtils.invokeExactMethod(oldViewAndController.getController(), onUnloadMtd);
				}
			}
			
			/*
			 * Este caso puede no darse cuando se trata de Views especiales autogeneradas
			 * Como por ejemplo las EntityCRUDViews, ya que no se definen como tal y no hay
			 * Informacion paara generar el ViewInfo Asi que se omiten estas funcionalidades
			 * para casos como estos
			 */
			if(newViewInfo != null && newViewInfo.getControllerInfo() != null){
				String onLoadMtd = newViewInfo.getControllerInfo().getBeforeLoadMethod();
				if(hasOnLoadWithoutArguments(newViewInfo.getControllerInfo())){
					MethodUtils.invokeExactMethod(newViewAndController.getController(), onLoadMtd);
				}else if(hasOnLoadWithArguments(newViewInfo.getControllerInfo())){
					MethodUtils.invokeExactMethod(newViewAndController.getController(), onLoadMtd, 
							new Object[]{requestData.getParameters()}, new Class<?>[]{Map.class});
				}
			}
		}catch(ReflectiveOperationException e){
			throw new ViewDispatchException(e);
		}
	}
	
	
	private void handleViewDispatch(ViewInfo newViewInfo, ViewAndController newViewAndController, RequestData requestData) throws ViewDispatchException {
		try{
			if(currentRootViewInfo != null && StringUtils.isNotEmpty(currentRootViewInfo.getContentSetterMethod())){
				ViewAndController rootView = loadedViews.get(currentRootViewInfo.getUrl());
				MethodUtils.invokeExactMethod(rootView.getView(), currentRootViewInfo.getContentSetterMethod(), new Component[]{newViewAndController.getUiContainer()}, new Class<?>[]{Component.class});
			}else{
				UI.getCurrent().setContent(newViewAndController.getUiContainer());
				currentRootViewInfo = null;
			}
			currentViewInfo = newViewInfo;
		}catch(ReflectiveOperationException e){
			throw new ViewDispatchException(e);
		}
	}


	private void handleRootViewDispatch(ViewInfo newViewInfo, ViewAndController newViewAndController, RequestData requestData) {
		UI.getCurrent().setContent(newViewAndController.getUiContainer());
		currentRootViewInfo = newViewInfo;
	}
	
	private void handlePopupViewDispatch(final ViewInfo newViewInfo, final ViewAndController newViewAndController, final RequestData requestData){
		UI ui = UI.getCurrent();
		Window window = new Window(ui.getCaption(), newViewAndController.getUiContainer());
		window.setWidth(80, Unit.PERCENTAGE);
		window.setModal(true);
		window.addCloseListener(new CloseListener() {
			@Override public void windowClose(CloseEvent e) {
				try{
					String onUnloadMtd = newViewInfo.getControllerInfo().getBeforeUnloadMethod();
					if(StringUtils.isNotEmpty(onUnloadMtd)){
						MethodUtils.invokeExactMethod(newViewAndController.getController(), onUnloadMtd);
					}
				}catch(ReflectiveOperationException ex){
					LOGGER.error("Has ocurred an unexpected error handling view onUnload method", ex);
				}
			}
		});
		ui.addWindow(window);
		currentPopupView = window;
	}

	private RequestData buildRequestData(ViewDispatchRequest dispatchRequest){
		RequestData resp = new RequestData();
		resp.setPopup(dispatchRequest.isPopup());
		String url = StringUtils.defaultString(dispatchRequest.getViewURL());
		int urlinfoIndex = url.indexOf("?");
		resp.setUrl((urlinfoIndex < 0) ? url : url.substring(0, urlinfoIndex));
		if(urlinfoIndex >= 0)
			resp.setPathInfo(url.substring(urlinfoIndex+1));
		
		if(MVPUtils.hasRequestParameters(dispatchRequest.getViewURL()))
			resp.setParameters(decodeAndBuildParametersMap(resp.getPathInfo()));
		
		ViewInfo viewInfo = viewProvider.getViewInfo(resp);
		if(viewInfo != null)
			resp.setUrl(viewInfo.getUrl());//Esta se hace para el caso del ResourceNotFount, Forbidden y Otros
		
		resp.setEventBus(eventBus);
		
		return resp;
	}
	
	private Map<String, String> decodeAndBuildParametersMap(String pathInfo) {
		StringTokenizer paramsTokens = new StringTokenizer(new String(Base64.base64ToByteArray(pathInfo)), "&");
		Map<String, String>  requestParameters = new HashMap<String, String>();
		while(paramsTokens.hasMoreTokens()){
			String[] keyValuePair = paramsTokens.nextToken().split("=");
			if(keyValuePair.length < 2)continue;
			requestParameters.put(keyValuePair[0].trim(), keyValuePair[1].trim());
		}
		return requestParameters;
	}

	private boolean hasOnLoadWithArguments(ControllerInfo controllerInfo){
		if(StringUtils.isEmpty(controllerInfo.getBeforeLoadMethod()))return false;
		try {
			controllerInfo.getControllerClass().getMethod(controllerInfo.getBeforeLoadMethod(), Map.class);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}
	
	private boolean hasOnLoadWithoutArguments(ControllerInfo controllerInfo){
		if(StringUtils.isEmpty(controllerInfo.getBeforeLoadMethod()))return false;
		try {
			controllerInfo.getControllerClass().getMethod(controllerInfo.getBeforeLoadMethod());
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}
	
	public ViewProvider getViewProvider() {
		return viewProvider;
	}


	public void setViewProvider(ViewProvider viewProvider) {
		this.viewProvider = viewProvider;
	}


	public EventBus getEventBus() {
		return eventBus;
	}


	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		if(eventBus!= null){
			eventBus.addHandler(popupViewCloseHandler);
		}
	}
	
	private class PopupViewCloseEventHandler{
		@UIEventHandler
		public void handlePopupViewClose(PopupViewCloseEvent event){
			if(currentPopupView != null)
				currentPopupView.close();
		}
	}
}
