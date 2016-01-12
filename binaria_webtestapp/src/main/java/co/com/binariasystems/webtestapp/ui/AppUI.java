
package co.com.binariasystems.webtestapp.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.security.mgt.SecurityManager;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.UI;
@Theme(value=UIConstants.BINARIA_THEME)
//@PreserveOnRefresh
public class AppUI extends UI {
	Logger log = LoggerFactory.getLogger(AppUI.class);
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		SecurityManager securityManager = IOCHelper.getBean(SecurityManager.class);
		getPage().setTitle("Web Test Application v5.6");
		UriFragmentChangedListener uriFragmentListener = IOCHelper.getBean(UriFragmentChangedListener.class);
		getPage().addUriFragmentChangedListener(uriFragmentListener);
		getPage().setUriFragment(securityManager.isAuthenticated(getAuthorizationRequest(vaadinRequest)) ? securityManager.getDashBoardViewUrl() : "/");
    }
	
	private AuthorizationRequest getAuthorizationRequest(VaadinRequest vaadinRequest){
		VaadinServletRequest request =  (VaadinServletRequest) VaadinService.getCurrentRequest();
		return new AuthorizationRequest(null, request.getHttpServletRequest(), request.getSession());
	}
	
}
