package co.com.binariasystems.webtestapp.ui;

import static co.com.binariasystems.fmw.vweb.uicomponet.builders.Builders.button;
import static co.com.binariasystems.fmw.vweb.uicomponet.builders.Builders.passwordField;
import static co.com.binariasystems.fmw.vweb.uicomponet.builders.Builders.textField;
import co.com.binariasystems.fmw.vweb.mvp.annotation.AuthenticationForm;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.NullValidator;
import co.com.binariasystems.fmw.vweb.mvp.annotation.validation.StringLengthValidator;
import co.com.binariasystems.fmw.vweb.mvp.views.AbstractFormView;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@AuthenticationForm
@Root
@View(url="/", messages="co.com.binariasystems.webtestapp.ui.webtestviews", controller=AuthenticationViewController.class, viewStringsByConventions= true)
public class AuthenticationView extends AbstractFormView{
	@NullValidator
	@StringLengthValidator(min=4)
	protected TextField usernameField;
	@NullValidator
	@StringLengthValidator(min=4)
	private PasswordField passwordField;
	private Button logInBtn;
	private PropertysetItem item;
	private UIForm form;
	
	@Init
	public void init(){
		form = this;
		item = new PropertysetItem();
		item.addItemProperty("usernameField", new ObjectProperty<String>(null, String.class));
		item.addItemProperty("passwordField", new ObjectProperty<String>(null, String.class));
		
		usernameField = textField().withProperty(item.getItemProperty("usernameField"));
		
		passwordField = passwordField().withProperty(item.getItemProperty("passwordField"));
		
		logInBtn =button();
		
		add(usernameField, FIRST, 100).add(passwordField, 0, 100).add(logInBtn, LAST, 100, Alignment.BOTTOM_LEFT);
	}
	
}
