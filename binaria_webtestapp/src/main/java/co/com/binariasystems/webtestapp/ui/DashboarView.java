package co.com.binariasystems.webtestapp.ui;

import co.com.binariasystems.fmw.vweb.mvp.annotation.DashBoard;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.mvp.views.AbstractView;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager2;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.webtestapp.dto.Medidor;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
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
	private Pager2<Medidor, Medidor> pager;
	private Grid grid;
	private BeanItemContainer<Medidor> container;
	
	@ViewBuild
	public Component init(){
		splitPanel = new HorizontalSplitPanel();
		rightPanel = new VerticalLayout();
		welcomeLabel = new Label("Welcome My People");
		linkLabel = new LinkLabel("Este es mi LinkLabel");

		container = new BeanItemContainer<Medidor>(Medidor.class);
		grid = new Grid("Medidores", container);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.getColumn("id").setHeaderCaption("Identificador");//Long
		grid.getColumn("serial").setHeaderCaption("Serial");//String
		grid.getColumn("gateway").setHeaderCaption("Gateway");//Gateway
		grid.getColumn("suscriptor").setHeaderCaption("Id. Suscriptor");//Long
		grid.getColumn("fechaInstalacion").setHeaderCaption("Fecha Instalaci\u00f3n");//Timestamp
		grid.getColumn("lecturaInicial").setHeaderCaption("Lectura Inicial");//Double
		
		
		pager = new Pager2<Medidor, Medidor>();
		pager.setMaxCachedPages(5);
		
		menuContainer = new TreeMenu();
		menuContainer.setWidth(200, Unit.PIXELS);
		menuContainer.setShowSearcher();
		
		rightPanel.addComponent(welcomeLabel);
		rightPanel.addComponent(linkLabel);
		rightPanel.addComponent(grid);
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
