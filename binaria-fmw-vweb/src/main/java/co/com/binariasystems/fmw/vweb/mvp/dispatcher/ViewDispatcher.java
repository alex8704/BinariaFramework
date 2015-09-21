package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.io.Serializable;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.mvp.event.RequestDispatchEvent;

//FrontController que se provee de ViewProvider
public interface ViewDispatcher extends Serializable{
	public void dispatch(RequestDispatchEvent dispatchEvent) throws FMWException;
}
