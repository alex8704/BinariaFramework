package co.com.binariasystems.webtestapp.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import co.com.binariasystems.fmw.annotation.Dependency;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.security.mgt.SecurityManager;
import co.com.binariasystems.fmw.security.model.AuthorizationRequest;
import co.com.binariasystems.fmw.util.pagination.ListPage;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewField;
import co.com.binariasystems.fmw.vweb.mvp.controller.AbstractViewController;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.ClickHandler;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel.LinkClickEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog;
import co.com.binariasystems.fmw.vweb.uicomponet.MessageDialog.Type;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeEvent;
import co.com.binariasystems.fmw.vweb.uicomponet.pager.PageChangeHandler;
import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.webtestapp.business.AuthenticationBusiness;
import co.com.binariasystems.webtestapp.dto.DireccionDTO;
import co.com.binariasystems.webtestapp.dto.Gateway;
import co.com.binariasystems.webtestapp.dto.Medidor;
import co.com.binariasystems.webtestapp.dto.MenuActionDTO;
import co.com.binariasystems.webtestapp.dto.MenuModuleDTO;
import co.com.binariasystems.webtestapp.dto.MenuOptionDTO;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;


@ViewController
public class DashboardViewController extends AbstractViewController {
	@ViewField private TreeMenu menuContainer;
	@ViewField private HorizontalSplitPanel splitPanel;
	@ViewField private Label welcomeLabel;
	@Dependency
	private AuthenticationBusiness authBusiness;
	@Dependency
	private SecurityManager securityManager;
	@ViewField private Pager<Medidor, Medidor> pager;
	@ViewField private LinkLabel linkLabel;
	private List<Medidor> items = new ArrayList<Medidor>();
	@ViewField private Grid grid;
	@ViewField private Button botonPruebas;
	@ViewField private Button boton2;
	@ViewField private AddressEditorField<DireccionDTO> addressField;
	@ViewField private ObjectProperty<DireccionDTO> addressFieldProperty;
	private MessageDialog logoutConfirmDialog;
	
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
				}if(event.getProperty().getValue() instanceof MenuActionDTO)
					handleMenuAction((MenuActionDTO)event.getProperty().getValue());
			}
		});
		
		pager.setPageDataTargetForGrid(grid);
		
		pager.setPageChangeHandler(new PageChangeHandler<Medidor, Medidor>() {
			@Override
			public ListPage<Medidor> loadPage(PageChangeEvent<Medidor> event) throws FMWUncheckedException {
				return new ListPage<Medidor>(items.subList(event.getInitialRow(), event.getFinalRow() > items.size() ? items.size() : event.getFinalRow()), items.size());
			}
		});
		
		botonPruebas.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(addressFieldProperty.getValue().toString());
			}
		});
		boton2.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				DireccionDTO direccion = new DireccionDTO();
				direccion.setMainViaType("AUT");
				direccion.setMainViaNum(12);
				direccion.setMainViaLetter("ABC");
				addressFieldProperty.setValue(direccion);
				System.out.println(addressFieldProperty.getValue().toString());
			}
		});
	}
	
	@OnLoad
	public void onControlerLoad(){
		List<MenuElement> opciones = new ArrayList<MenuElement>();
		MenuElement maestros = new MenuModuleDTO("Maestros", "Modulo de Administraci\u00f3n de Maestros de toda la aplicaci\u00f3n")
		.addChild(new MenuOptionDTO("Maestro Suscriptores", "Maestro de Suscriptores/Inmuebles", "/maestros/_masterentity/Suscriptor"));
		
		MenuElement dispositivos = new MenuModuleDTO("Dispositivos", "Submodulo para Maestros esclusivamente de dispositivos").
				addChilds(new MenuOptionDTO("Maestro Medidores", "Medidor", "/maestros/dispositivos/_masterentity/Medidor"),
						new MenuOptionDTO("Maestro Gateways", "Administracion de Gateways", "/maestros/dispositivos/_masterentity/Gateway"));
		
		maestros.addChild(dispositivos);
		
		
		MenuElement configuracion = new MenuModuleDTO("Configuracion", null)
		.addChild(new MenuOptionDTO("Gesti\u00f3n ConfigSys", "Administracion de Configsys", "/asa/asas/adminConfisys"));
		
		opciones.add(maestros);
		opciones.add(configuracion);
		opciones.add(new MenuActionDTO("Cerrar Sesi\u00f3n", null, "logout"));
		
		menuContainer.setItems(opciones);
		pager.setFilterDto(null);
	}
	
	private void handleMenuAction(MenuActionDTO menuAction){
		if("logout".equals(menuAction.getActionId())){
			if(logoutConfirmDialog == null){
				logoutConfirmDialog = new MessageDialog("Salir", "Esta seguro de salir de la aplicaci\u00f3n", Type.QUESTION);
				logoutConfirmDialog.addYesClickListener(new ClickListener() {
					@Override public void buttonClick(ClickEvent event) {
						VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
						securityManager.logout(new AuthorizationRequest(null, vaadinRequest.getHttpServletRequest(), vaadinRequest.getSession()));
						UI.getCurrent().getSession().close();
						Page.getCurrent().setLocation(VWebUtils.getContextPath());
					}
				});
			}
			logoutConfirmDialog.show();
		}
			
	}
}
