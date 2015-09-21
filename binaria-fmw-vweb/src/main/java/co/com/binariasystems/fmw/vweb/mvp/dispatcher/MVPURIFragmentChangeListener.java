package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.vweb.mvp.event.RequestDispatchEvent;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;

public class MVPURIFragmentChangeListener implements UriFragmentChangedListener{
	private ViewDispatcher viewDispatcher;
	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		RequestDispatchEvent dispatchEvent = new RequestDispatchEvent();
		dispatchEvent.set(RequestDispatchEvent.URL_PROPERTY, event.getUriFragment());
		try {
			viewDispatcher.dispatch(dispatchEvent);
		} catch (FMWException e) {
			throw new FMWUncheckedException(e);
		}
	}
	public ViewDispatcher getViewDispatcher() {
		return viewDispatcher;
	}
	public void setViewDispatcher(ViewDispatcher viewDispatcher) {
		this.viewDispatcher = viewDispatcher;
	}
	
}
