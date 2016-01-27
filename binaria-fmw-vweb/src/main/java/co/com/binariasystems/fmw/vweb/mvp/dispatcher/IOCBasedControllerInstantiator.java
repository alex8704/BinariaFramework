package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.mvp.Initializable;
import co.com.binariasystems.fmw.vweb.mvp.annotation.UIEventHandler;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ControllerInfo;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.mvp.eventbus.EventBus;

public class IOCBasedControllerInstantiator implements ControllerInstantiator {

	@Override
	public Object instantiateController(ViewInfo viewInfo, RequestData request, Object viewInstance) throws ViewInstantiationException {
		Object controller = null;
		ControllerInfo controllerInfo = viewInfo.getControllerInfo();
		boolean hasEventHandlers = false;
		
		try{
			controller = controllerInfo.getControllerClass().newInstance();
			for(Method method : controllerInfo.getControllerClass().getMethods()){
				if(method.getParameterTypes() != null && method.getParameterTypes().length > 0){
					if(MessageBundleManager.class.isAssignableFrom(method.getParameterTypes()[0]))
						MethodUtils. getAccessibleMethod(method).invoke(controller, MVPUtils.createMessageBundleManager(viewInfo));
					if(RequestData.class.isAssignableFrom(method.getParameterTypes()[0]))
						MethodUtils.getAccessibleMethod(method).invoke(controller, request);
					if(EventBus.class.isAssignableFrom(method.getParameterTypes()[0]))
						MethodUtils.getAccessibleMethod(method).invoke(controller, request.getEventBus());
					if(request.getEventBus() != null && method.isAnnotationPresent(UIEventHandler.class))
						hasEventHandlers = true;
				}
			}
			if(hasEventHandlers)
				request.getEventBus().addHandler(controller);
			//Se hace binding de los campos que coincida con los del view, antes de llamar al metodo
			//Inicializador del Controller
			MVPUtils.copyHomonymFieldsFromViewToController(viewInfo, viewInstance, controller);
			MVPUtils.injectIOCProviderDependencies(controller, controllerInfo.getControllerClass());
			
			if(controller instanceof Initializable)
				((Initializable)controller).init();
			else if(StringUtils.isNoneEmpty(controllerInfo.getInitMethod()))
				MethodUtils.getAccessibleMethod(controllerInfo.getControllerClass().getMethod(controllerInfo.getInitMethod())).invoke(controller);
		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | FMWException | InstantiationException ex){
			Throwable cause = FMWExceptionUtils.prettyMessageException(ex);
			throw new ViewInstantiationException(cause.getMessage(), cause);
		}
		
		return controller;
	}

}
