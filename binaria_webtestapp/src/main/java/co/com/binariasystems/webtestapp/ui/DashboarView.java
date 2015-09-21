package co.com.binariasystems.webtestapp.ui;

import co.com.binariasystems.fmw.vweb.mvp.annotation.DashBoard;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.views.AbstractView;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.UIForm;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;

@DashBoard
@Root(contentSetterMethod="setContentPane")
@View(url="/", controller=DashboardViewController.class)
public class DashboarView extends AbstractView{
	private HorizontalSplitPanel splitPanel;
	private TreeMenu menuContainer;
	private Label welcomeLabel;
	
	@ViewBuild
	public Component init(){
		splitPanel = new HorizontalSplitPanel();
		welcomeLabel = new Label("Welcome My People");
		
		menuContainer = new TreeMenu();
		menuContainer.setWidth(200, Unit.PIXELS);
		menuContainer.setShowSearcher();
		
		splitPanel.setFirstComponent(menuContainer);
		splitPanel.setSecondComponent(welcomeLabel);
		splitPanel.setSplitPosition(200, Unit.PIXELS);
		splitPanel.setSizeFull();
		return splitPanel;
	}
	
	public void setContentPane(Component component){
		splitPanel.setSecondComponent(component);
		
	}
	
	
}
