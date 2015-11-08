package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

public class ViewDispatcherImpl implements ViewDispatcher{
	private ViewProvider viewProvider;
	private EventBus eventBus;
	private Map<String, ViewAndController> loadedViews = new HashMap<String, ViewAndController>();
	private ViewInfo currentViewInfo;
	private ViewInfo currentRootViewInfo;
	
	public ViewDispatcherImpl(){
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
		if(viewInfo != null && viewInfo.isRootView()){
			handleRootViewDispatch(viewInfo, viewAndController);
			return;
		}
		
		handleViewDispatch(viewInfo, viewAndController);
	}
	
	
	private void handleViewDispatch(ViewInfo newViewInfo, ViewAndController newViewAndController) throws FMWException{
		try{
			if(currentViewInfo != null && currentViewInfo.getControllerInfo() != null){
				ViewAndController oldViewAndController = loadedViews.get(currentViewInfo.getUrl());
				String onUnloadMtd = currentViewInfo.getControllerInfo().getBeforeUnloadMethod();
				if(StringUtils.isNotEmpty(onUnloadMtd)){
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
				if(StringUtils.isNotEmpty(onLoadMtd)){
					MethodUtils.invokeExactMethod(newViewAndController.getController(), onLoadMtd);
				}
			}
			if(currentRootViewInfo != null && StringUtils.isNotEmpty(currentRootViewInfo.getContentSetterMethod())){
				ViewAndController rootView = loadedViews.get(currentRootViewInfo.getUrl());
				MethodUtils.invokeExactMethod(rootView.getView(), currentRootViewInfo.getContentSetterMethod(), new Component[]{newViewAndController.getUiContainer()}, new Class<?>[]{Component.class});
			}else{
				UI.getCurrent().setContent(newViewAndController.getUiContainer());
				currentRootViewInfo = null;
			}
			currentViewInfo = newViewInfo;
		}catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
			throw new ViewDispatchException(e);
		}
	}


	private void handleRootViewDispatch(ViewInfo newViewInfo, ViewAndController newViewAndController) throws FMWException {
		try{
			if(currentRootViewInfo != null && currentRootViewInfo.getControllerInfo() != null){
				ViewAndController oldViewAndController = loadedViews.get(currentRootViewInfo.getUrl());
				String onUnloadMtd = currentRootViewInfo.getControllerInfo().getBeforeUnloadMethod();
				if(StringUtils.isNotEmpty(onUnloadMtd)){
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
				if(StringUtils.isNotEmpty(onLoadMtd)){
					MethodUtils.invokeExactMethod(newViewAndController.getController(), onLoadMtd);
				}
			}
			
			UI.getCurrent().setContent(newViewAndController.getUiContainer());
			currentRootViewInfo = newViewInfo;
		}catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
			throw new ViewDispatchException(e);
		}
	}


	private RequestData buildRequestData(ViewDispatchRequest dispatchRequest){
		RequestData resp = new RequestData();
		String url = StringUtils.defaultString(dispatchRequest.getViewURL());
		int urlinfoIndex = url.indexOf("?");
		resp.setUrl((urlinfoIndex < 0) ? url : url.substring(0, urlinfoIndex));
		if(urlinfoIndex >= 0)
			resp.setPathInfo(url.substring(urlinfoIndex+1));
		
		ViewInfo viewInfo = viewProvider.getViewInfo(resp);
		if(viewInfo != null)
			resp.setUrl(viewInfo.getUrl());//Esta se hace para el caso del ResourceNotFount, Forbidden y Otros
		
		resp.setEventBus(eventBus);
		
		return resp;
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
	}
}
