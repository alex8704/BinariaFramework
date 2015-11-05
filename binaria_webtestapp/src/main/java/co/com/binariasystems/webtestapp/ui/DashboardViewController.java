package co.com.binariasystems.webtestapp.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import co.com.binariasystems.fmw.annotation.Dependency;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.controller.AbstractViewController;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.ClickHandler;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.LinkClickEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager2;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;
import co.com.binariasystems.webtestapp.business.AuthenticationBusiness;
import co.com.binariasystems.webtestapp.dto.Gateway;
import co.com.binariasystems.webtestapp.dto.Medidor;
import co.com.binariasystems.webtestapp.dto.MenuModuleDTO;
import co.com.binariasystems.webtestapp.dto.MenuOptionDTO;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;


@ViewController
public class DashboardViewController extends AbstractViewController {
	@ViewField private TreeMenu menuContainer;
	@ViewField private HorizontalSplitPanel splitPanel;
	@ViewField private Label welcomeLabel;
	@Dependency
	private AuthenticationBusiness authBusiness;
	@ViewField private Pager2<Medidor, Medidor> pager;
	@ViewField private LinkLabel linkLabel;
	private List<Medidor> items = new ArrayList<Medidor>();
	@ViewField private Grid grid;
	
	@Init
	public void inicializar(){
		System.out.println("Inicializando Dashboard Controller");
		linkLabel.setClickHandler(new ClickHandler() {
			@Override
			public void handleClick(LinkClickEvent event) {
				Notification.show("Haz clickado el link button", Notification.Type.HUMANIZED_MESSAGE);
			}
		});
		
		Random random = new Random();
		for(int i = 1; i <= 100; i++){
			Medidor medidor = new Medidor();
			medidor.setId(Long.valueOf(i));
			medidor.setSerial(String.valueOf(Math.abs(random.nextInt())));
			medidor.setFechaInstalacion(new Timestamp(new Date().getTime()));
			medidor.setGateway(new Gateway());
			medidor.getGateway().setId(i);
			medidor.getGateway().setDescripcion("Gateway "+i);
			medidor.getGateway().setIp("127.0.0."+i);
			medidor.getGateway().setFechaComunicacion(new Timestamp(new Date().getTime()));
			medidor.setSuscriptor(Long.valueOf(100 - i));
			medidor.setLecturaInicial(Double.valueOf(i+2));
			items.add(medidor);
			
		}
		menuContainer.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() instanceof MenuOptionDTO){
					MenuOptionDTO opcion = (MenuOptionDTO)event.getProperty().getValue();
					Page.getCurrent().setUriFragment(opcion.getPath());
				}
			}
		});
		
		pager.setPageDataTargetForGrid(grid);
		
		pager.setPageChangeHandler(new PageChangeHandler<Medidor, Medidor>() {
			@Override
			public ListPage<Medidor> loadPage(PageChangeEvent<Medidor> event) throws FMWUncheckedException {
				return new ListPage<Medidor>(items.subList(event.getInitialRow(), event.getFinalRow() > items.size() ? items.size() : event.getFinalRow()), items.size());
			}
		});
	}
	
	@OnLoad
	public void onControlerLoad(){
		List<MenuElement> opciones = new ArrayList<MenuElement>();
		MenuModuleDTO maestros = new MenuModuleDTO();
		maestros.setCaption("Maestros");
		maestros.setDescription("Modulo de Administraci\u00f3n de Maestros de toda la aplicaci\u00f3n");
		
		MenuOptionDTO suscriptor = new MenuOptionDTO();
		suscriptor.setCaption("Suscriptor");
		suscriptor.setDescription("Maestro de Suscriptores/Inmuebles");
		suscriptor.setPath("/maestros/_masterentity/"+suscriptor.getCaption());
		
		maestros.addChild(suscriptor);
		
		MenuModuleDTO dispositivos = new MenuModuleDTO();
		dispositivos.setCaption("Dispositivos");
		dispositivos.setDescription("Submodulo para Maestros esclusivamente de dispositivos");
		
		MenuOptionDTO medidor = new MenuOptionDTO();
		medidor.setCaption("Medidor");
		medidor.setPath("/maestros/dispositivos/_masterentity/"+medidor.getCaption());
		dispositivos.addChild(medidor);
		
		MenuOptionDTO gateway = new MenuOptionDTO();
		gateway.setCaption("Gateway");
		gateway.setDescription("Administracion de Gateways");
		gateway.setPath("/maestros/dispositivos/_masterentity/"+gateway.getCaption());
		dispositivos.addChild(gateway);
		
		maestros.addChild(dispositivos);
		
		
		MenuModuleDTO configuracion = new MenuModuleDTO();
		configuracion.setCaption("Configuracion");
		configuracion.setDescription("");
		
		MenuOptionDTO confisys = new MenuOptionDTO();
		confisys.setCaption("Gesti\u00f3n ConfigSys");
		confisys.setDescription("Administracion de Configsys");
		confisys.setPath("/asa/asas/adminConfisys");
		configuracion.addChild(confisys);
		
		opciones.add(maestros);
		opciones.add(configuracion);
		
		menuContainer.setItems(opciones);
	}
}
