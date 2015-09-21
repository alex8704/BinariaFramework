package co.com.binariasystems.fmw.vweb.mvp.views;

import co.com.binariasystems.fmw.vweb.mvp.annotation.ResourceNotFound;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

@ResourceNotFound
@View(
	url="/",
	messages="co.com.binariasystems.fmw.vweb.mvp.views.default_views_strings"
)
public class DefaultForbiddenView extends AbstractView{
	public static final String VIEW_BUILD_METHOD = "buildView";
	private UIForm form;
	private Label messageLbl;
	
	@ViewBuild
	public Component buildView(){
		form = new UIForm(getText(getClass().getSimpleName()+".form.title"), 90, Unit.PERCENTAGE);
		messageLbl = new Label();
		messageLbl.addStyleName(ValoTheme.LABEL_H1);
		messageLbl.setValue(getText(getClass().getSimpleName()+".messageLbl.value"));
		form.add(messageLbl);
		
		return form;
	}

}
