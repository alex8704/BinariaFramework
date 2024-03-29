package co.com.binariasystems.webtestapp.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.com.binariasystems.fmw.annotation.Dependency;
import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.mgt.SecurityManager;
import co.com.binariasystems.fmw.security.model.AuthenticationRequest;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnUnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.controller.AbstractViewController;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.FormValidationException;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.webtestapp.business.AuthenticationBusiness;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;


@ViewController
public class AuthenticationViewController extends AbstractViewController{
	private static final Logger log = LoggerFactory.getLogger(AuthenticationViewController.class);
	@ViewField private TextField usernameField;
	@ViewField private PasswordField passwordField;
	@ViewField private Button logInBtn;
	@ViewField private PropertysetItem item;
	@ViewField(isViewReference = true) private FormPanel form;
	@Autowired
	private AuthenticationBusiness authBusiness;
	@Dependency
	private SecurityManager securityManager;
	
	@Init
	public void initController(){
		
		
		logInBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					form.validate();
					
					AuthenticationRequest authRequest = new AuthenticationRequest();
					authRequest.setUsername((String)item.getItemProperty("usernameField").getValue());
					authRequest.setPassword((String)item.getItemProperty("passwordField").getValue());
					authRequest.setHttpRequest(getVaadinRequest().getHttpServletRequest());
					
					
					securityManager.authenticate(authRequest);
					System.out.println(authBusiness.dato());
					authBusiness.sendAuthenticationMail();
					new MessageDialog("Bienvenido", "La validaci\u00f3n de las credenciales de autenticaci\u00f3n ha sida satisfactoria", Type.INFORMATION)
					.addYesClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							UI.getCurrent().getPage().setUriFragment(securityManager.getDashBoardViewUrl());
						}
					}).show();
					
					log.debug("La validaci\u00f3n de las credenciales de autenticaci\u00f3n ha sida satisfactoria");
				}catch ( FMWSecurityException ex) {
					log.error(ex.toString());
					MessageDialog.showExceptions(ex);
				} catch (FormValidationException ex) {
					log.warn("Debe digitar sus datos de usuario para continuar.");
					MessageDialog.showValidationErrors(null, ex.getMessage());
				}
				
			}
		});
	}
	
	private VaadinServletRequest getVaadinRequest(){
		return (VaadinServletRequest) VaadinService.getCurrentRequest();
	}
	
	@OnLoad
	public void onLoadController(){
		log.info("On load "+getClass().getSimpleName());
	}
	
	@OnUnLoad
	public void onUnloadController(){
		log.info("On Unload "+getClass().getSimpleName());
		for(Object propertyId : item.getItemPropertyIds())
			item.getItemProperty(propertyId).setValue(null);
	}
	
}
