package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.vweb.mvp.event.ViewDispatchRequest;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

public class MVPURIFragmentChangeListener implements UriFragmentChangedListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(MVPURIFragmentChangeListener.class);
	private ViewDispatcher viewDispatcher;
	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		
		VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
		ViewDispatchRequest dispatchRequest = new ViewDispatchRequest();
		dispatchRequest.setPopup(StringUtils.defaultString(event.getUriFragment()).startsWith(ViewDispatcher.POPUP_REQUEST_PREFIX));
		dispatchRequest.setViewURL(dispatchRequest.isPopup() ? event.getUriFragment().substring(ViewDispatcher.POPUP_REQUEST_PREFIX.length()) :event.getUriFragment());
		dispatchRequest.setHttpRequest(vaadinRequest.getHttpServletRequest());
		dispatchRequest.setHttpSession(vaadinRequest.getSession());
		
		try {
			viewDispatcher.dispatch(dispatchRequest);
		} catch (FMWException ex) {
			MessageDialog.showExceptions(ex, LOGGER);
		}
	}
	public ViewDispatcher getViewDispatcher() {
		return viewDispatcher;
	}
	public void setViewDispatcher(ViewDispatcher viewDispatcher) {
		this.viewDispatcher = viewDispatcher;
	}
	
}
