package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.io.Serializable;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;

//FrontController que se provee de ViewProvider
public interface ViewDispatcher extends Serializable{
	public static final String POPUP_REQUEST_PREFIX  = "/behavior/popup";
	public void dispatch(ViewDispatchRequest dispatchRequest) throws FMWException;
}
