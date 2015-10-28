package co.com.binariasystems.webtestapp.ui;

import co.com.binariasystems.fmw.vweb.mvp.annotation.DashBoard;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.views.AbstractView;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager2;
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
import com.vaadin.ui.VerticalLayout;

@DashBoard
@Root(contentSetterMethod="setContentPane")
@View(url="/", controller=DashboardViewController.class)
public class DashboarView extends AbstractView{
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout rightPanel;
	private TreeMenu menuContainer;
	private Label welcomeLabel;
	private LinkLabel linkLabel;
	private Pager2<Object, Object> pager;
	
	
	@ViewBuild
	public Component init(){
		splitPanel = new HorizontalSplitPanel();
		rightPanel = new VerticalLayout();
		welcomeLabel = new Label("Welcome My People");
		linkLabel = new LinkLabel("Este es mi LinkLabel");
		
		pager = new Pager2<Object, Object>();
		
		menuContainer = new TreeMenu();
		menuContainer.setWidth(200, Unit.PIXELS);
		menuContainer.setShowSearcher();
		
		rightPanel.addComponent(welcomeLabel);
		rightPanel.addComponent(linkLabel);
		rightPanel.addComponent(pager);
		
		splitPanel.setFirstComponent(menuContainer);
		splitPanel.setSecondComponent(rightPanel);
		splitPanel.setSplitPosition(200, Unit.PIXELS);
		splitPanel.setSizeFull();
		return splitPanel;
	}
	
	public void setContentPane(Component component){
		splitPanel.setSecondComponent(component);
		
	}
	
	
}
