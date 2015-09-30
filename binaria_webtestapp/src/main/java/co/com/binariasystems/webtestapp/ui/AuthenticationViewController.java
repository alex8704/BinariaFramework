package co.com.binariasystems.webtestapp.ui;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import co.com.binariasystems.fmw.ioc.IOCHelper;
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
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;
import co.com.binariasystems.webtestapp.business.AuthenticationBusiness;


@ViewController
public class AuthenticationViewController extends AbstractViewController{
	private static final Logger log = LoggerFactory.getLogger(AuthenticationViewController.class);
	@ViewField private TextField usernameField;
	@ViewField private PasswordField passwordField;
	@ViewField private Button logInBtn;
	@ViewField private PropertysetItem item;
	@ViewField private UIForm form;
	private PropertiesManager mailProperties;
	@Autowired
	private AuthenticationBusiness authBusiness;
	
	@Init
	public void initController(){
		
		mailProperties = PropertiesManager.forPath("javamail.properties", AuthenticationViewController.class);
		
		logInBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(!form.validate()){
					log.warn("Debe digitar sus datos de usuario para continuar.");
					return;
				}
				Subject subject = SecurityUtils.getSubject();
				UsernamePasswordToken authToken = new UsernamePasswordToken((String)item.getItemProperty("usernameField").getValue(), (String)item.getItemProperty("passwordField").getValue());
				
				try{
					subject.login(authToken);
					System.out.println(authBusiness.dato());
					//sendAuthenticationMail();
					new MessageDialog("Bienvenido", "La validaci\u00f3n de las credenciales de autenticaci\u00f3n ha sida satisfactoria", Type.INFORMATION)
					.addYesClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							SecurityManager secMgr = IOCHelper.getBean(SecurityManager.class);
							UI.getCurrent().getPage().setUriFragment(secMgr.getDashBoardViewUrl());
						}
					}).show();
					
					log.debug("La validaci\u00f3n de las credenciales de autenticaci\u00f3n ha sida satisfactoria");
				}catch ( UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ex) {
					MessageDialog.showExceptions(ex);
					log.error(ex.toString());
				}  catch (AuthenticationException  ae ) {
					MessageDialog.showExceptions(ae);
					log.error(ae.toString());
				}
				
			}
		});
	}
	
	
	private void sendAuthenticationMail(){
		try{
			VelocityMailSender mailSender = IOCHelper.getBean(VelocityMailSender.class);
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
