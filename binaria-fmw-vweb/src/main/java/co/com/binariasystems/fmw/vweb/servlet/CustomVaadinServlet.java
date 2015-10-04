package co.com.binariasystems.fmw.vweb.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.LocaleUtils;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;

public class CustomVaadinServlet extends VaadinServlet {
	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		
		
		getService().addSessionInitListener(new SessionInitListener() {
			
			@Override
			public void sessionInit(SessionInitEvent event) throws ServiceException {
				Cookie langCookie = null;
				for(Cookie cookie : event.getRequest().getCookies())
					if(cookie.getName().equals(VWebCommonConstants.USER_LANGUAGE_APPCOOKIE)){
						langCookie = cookie;
						break;
					}
				if(langCookie == null)
					langCookie = createLanguageCookie(event.getRequest());
				event.getSession().setLocale(LocaleUtils.toLocale(langCookie.getValue()));
				VaadinService.getCurrentResponse().addCookie(langCookie);
			}
		});
	}
	
	
	private Cookie createLanguageCookie(VaadinRequest request){
		Cookie langCookie = new Cookie(VWebCommonConstants.USER_LANGUAGE_APPCOOKIE, request.getLocale().toString());
		langCookie.setPath(request.getContextPath());
		return langCookie;
	}
}
