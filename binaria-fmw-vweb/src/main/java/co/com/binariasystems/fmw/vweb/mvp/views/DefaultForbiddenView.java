package co.com.binariasystems.fmw.vweb.mvp.views;

import co.com.binariasystems.fmw.vweb.mvp.annotation.ResourceNotFound;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;
import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

@ResourceNotFound
@View(
	url="/",
	messages="co.com.binariasystems.fmw.vweb.resources.messages.default_views_strings"
)
public class DefaultForbiddenView extends AbstractView{
	public static final String VIEW_BUILD_METHOD = "buildView";
	private FormPanel form;
	private Label messageLbl;
	
	@ViewBuild
	public Component buildView(){
		form = new FormPanel(getText(getClass().getSimpleName()+".form.title"));
		form.setWidth(Dimension.percent(90));
		messageLbl = new Label();
		messageLbl.addStyleName(ValoTheme.LABEL_H1);
		messageLbl.setValue(getText(getClass().getSimpleName()+".messageLbl.value"));
		form.add(messageLbl, Dimension.fullPercent());
		
		return form;
	}

}
