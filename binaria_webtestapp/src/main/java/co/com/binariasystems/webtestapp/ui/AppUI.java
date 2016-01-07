
package co.com.binariasystems.webtestapp.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.vweb.constants.UIConstants;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
@Theme(value=UIConstants.BINARIA_THEME)
//@PreserveOnRefresh
public class AppUI extends UI {
	Logger log = LoggerFactory.getLogger(AppUI.class);
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		
		getPage().setTitle("Web Test Application v5.6");
		UriFragmentChangedListener uriFragmentListener = IOCHelper.getBean(UriFragmentChangedListener.class);
		getPage().addUriFragmentChangedListener(uriFragmentListener);
		getPage().setUriFragment("/");
    }
}
