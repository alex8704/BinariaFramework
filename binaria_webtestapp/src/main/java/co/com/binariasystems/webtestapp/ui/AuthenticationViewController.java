package co.com.binariasystems.webtestapp.ui;

import static co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants.SECURITY_SUBJECT_ATTRIBUTE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.com.binariasystems.fmw.annotation.Dependency;
import co.com.binariasystems.fmw.util.mail.SimpleMailMessage;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;
import co.com.binariasystems.fmw.util.velocity.VelocityMailSender;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnUnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.controller.AbstractViewController;
import co.com.binariasystems.fmw.vweb.mvp.security.SecurityManager;
import co.com.binariasystems.fmw.vweb.mvp.security.model.AuthorizationAndAuthenticationInfo;
import co.com.binariasystems.fmw.vweb.mvp.security.model.FMWSecurityException;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.FormValidationException;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.webtestapp.business.AuthenticationBusiness;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.VaadinService;
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
	private PropertiesManager mailProperties;
	@Autowired
	private AuthenticationBusiness authBusiness;
	@Dependency
	private SecurityManager securityManager;
	@Dependency
	private VelocityMailSender mailSender;
	
	@Init
	public void initController(){
		
		mailProperties = PropertiesManager.forPath("javamail.properties", AuthenticationViewController.class);
		
		logInBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					form.validate();
					
					AuthorizationAndAuthenticationInfo authInfo = new AuthorizationAndAuthenticationInfo()
							.set(AuthorizationAndAuthenticationInfo.USERNAME_ARG, (String)item.getItemProperty("usernameField").getValue())
							.set(AuthorizationAndAuthenticationInfo.USERPASSWORD_ARG, (String)item.getItemProperty("passwordField").getValue())
							.set(AuthorizationAndAuthenticationInfo.SECURITY_SUBJECT_ARG, VaadinService.getCurrentRequest().getAttribute(SECURITY_SUBJECT_ATTRIBUTE));
					
					securityManager.authenticate(authInfo);
					System.out.println(authBusiness.dato());
					//sendAuthenticationMail();
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
	
	
	private void sendAuthenticationMail(){
		try{
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(mailProperties.getString("mail.smtp.user"));
			msg.setTo("alexander_8704@hotmail.com");
			msg.setSubject("[Mensaje Simple]");
			mailSender.send(msg, "/simple-tmpl.vm", null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@OnLoad
	public void onLoadController(){
		log.info("On load "+getClass().getSimpleName());
	}
	
	@OnUnLoad
	public void onUnloadController(){
		log.info("On Unload "+getClass().getSimpleName());
	}
	
}
