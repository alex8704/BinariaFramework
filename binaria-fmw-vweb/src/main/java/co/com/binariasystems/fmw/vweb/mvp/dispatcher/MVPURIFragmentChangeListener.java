package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

public class MVPURIFragmentChangeListener implements UriFragmentChangedListener{
	private ViewDispatcher viewDispatcher;
	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
		ViewDispatchRequest dispatchRequest = new ViewDispatchRequest();
		dispatchRequest.setViewURL(event.getUriFragment());
		dispatchRequest.setHttpRequest(vaadinRequest.getHttpServletRequest());
		dispatchRequest.setHttpSession(vaadinRequest.getSession());
		
		try {
			viewDispatcher.dispatch(dispatchRequest);
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
