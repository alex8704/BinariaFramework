

package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;

import com.vaadin.ui.Component;

import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.mvp.Initializable;
import co.com.binariasystems.fmw.vweb.mvp.annotation.UIEventHandler;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ControllerInfo;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;

public class DefaultViewInstanceCreator implements ViewInstanceCreator {
	
	private ControllerInstantiator controllerInstantiator;
	
	public DefaultViewInstanceCreator(){
		controllerInstantiator = new IOCBasedControllerInstantiator();
	}
	

	@Override
	public ViewAndController createAndCongigureView(ViewInfo viewInfo, RequestData request) throws ViewInstantiationException {
		ViewAndController resp = null;
		Component uiContainer = null;
		Object controller = null;
		Object viewInstance = null;
		MessageBundleManager messageBundleManager = null;
		try {
			viewInstance = viewInfo.getViewClass().newInstance();
			boolean viewHasEventHandlers = false;
			for(Method method : viewInfo.getViewClass().getMethods()){
				if(method.getParameterTypes() != null && method.getParameterTypes().length > 0){
					if(MessageBundleManager.class.isAssignableFrom(method.getParameterTypes()[0])){
						messageBundleManager = MVPUtils.createMessageBundleManager(viewInfo);
						MethodUtils.getAccessibleMethod(method).invoke(viewInstance, messageBundleManager);
					}if(RequestData.class.isAssignableFrom(method.getParameterTypes()[0]))
						MethodUtils.getAccessibleMethod(method).invoke(viewInstance, request);
					if(request.getEventBus() != null && method.isAnnotationPresent(UIEventHandler.class))
						viewHasEventHandlers = true;
				}
			}
			
			if(viewHasEventHandlers)
				request.getEventBus().addHandler(viewInstance);
			
			if(viewInstance instanceof Initializable)
				((Initializable)viewInstance).init();
			else if(StringUtils.isNoneEmpty(viewInfo.getInitMethod()))
				MethodUtils.getAccessibleMethod(viewInfo.getViewClass().getMethod(viewInfo.getInitMethod())).invoke(viewInstance);
			
			if(!StringUtils.isEmpty(viewInfo.getViewBuildMethod())){
				uiContainer = (Component) MethodUtils.getAccessibleMethod(viewInfo.getViewClass(), viewInfo.getViewBuildMethod()).invoke(viewInstance);
			}else
				uiContainer = (Component)viewInstance;
			
			MVPUtils.applyConventionStringsAndValidationsForView(viewInstance, viewInfo, messageBundleManager);
			
			ControllerInfo controllerInfo = viewInfo.getControllerInfo();
			
			controller = (controllerInfo != null) ? controllerInstantiator.instantiateController(viewInfo, request, viewInstance) : null;
			
			
			resp = new ViewAndController(viewInstance, uiContainer, controller);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ParseException e) {
			throw new ViewInstantiationException(e);
		}

		return resp;
	}

	@Override
	public void init(Reflections reflections) {
		
	}
	
	public ControllerInstantiator getControllerInstantiator() {
		return controllerInstantiator;
	}


	public void setControllerInstantiator(ControllerInstantiator controllerInstantiator) {
		this.controllerInstantiator = controllerInstantiator;
	}
	
	//Se implementan equals y hashcode de esta manera
	//Para controlar que solo haya una instancia de DafaultViewInstanceProvider
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DefaultViewInstanceCreator;
	}
	
	@Override
	public int hashCode() {
		return DefaultViewInstanceCreator.class.getName().hashCode();
	}


	@Override
	public String getUrlPattern() {
		return VWebCommonConstants.DEFAULT_VIEWCREATOR_URLPATTERN;
	}


	@Override
	public boolean matches(String url) {
		// TODO Auto-generated method stub
		return true;
	}

}
