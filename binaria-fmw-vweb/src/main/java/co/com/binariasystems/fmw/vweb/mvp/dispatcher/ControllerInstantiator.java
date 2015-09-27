package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;

public interface ControllerInstantiator {
	public Object instantiateController(ViewInfo viewInfo, RequestData request, Object viewInstance) throws ViewInstantiationException;
}
