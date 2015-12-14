package co.com.binariasystems.webtestapp.ui;

import java.text.MessageFormat;

import co.com.binariasystems.fmw.vweb.mvp.annotation.DashBoard;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.mvp.views.AbstractView;
import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;
import co.com.binariasystems.fmw.vweb.uicomponet.LinkLabel;
import co.com.binariasystems.fmw.vweb.uicomponet.Pager;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.util.AddressFieldValidator;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;
import co.com.binariasystems.webtestapp.dto.DireccionDTO;
import co.com.binariasystems.webtestapp.dto.Medidor;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonArray;

@DashBoard
@Root(contentSetterMethod="setContentPane")
@View(url="/", controller=DashboardViewController.class)
public class DashboarView extends AbstractView{
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout rightPanel;
	private TreeMenu menuContainer;
	private Label welcomeLabel;
	private LinkLabel linkLabel;
	private AddressEditorField<DireccionDTO> addressField;
	private Pager<Medidor, Medidor> pager;
	private Grid grid;
	private BeanItemContainer<Medidor> gridDs;
	private GeneratedPropertyContainer container;
	private MessageFormat doChooseFmt = new MessageFormat("javascript:doChoose({0})");
	private Button botonPruebas;
	private Button boton2;
	private ObjectProperty<DireccionDTO> addressFieldProperty;
	
	@ViewBuild
	public Component init(){
		DireccionDTO direccion = new DireccionDTO();
		direccion.setMainViaType("AUT");
		direccion.setMainViaNum(12);
		direccion.setMainViaLetter("ABC");
		addressFieldProperty = new ObjectProperty<DireccionDTO>(null, DireccionDTO.class);
		splitPanel = new HorizontalSplitPanel();
		rightPanel = new VerticalLayout();
		welcomeLabel = new Label("Welcome My People");
		linkLabel = new LinkLabel("Este es mi LinkLabel");
		addressField = new AddressEditorField<DireccionDTO>("Direcci\u00f3n de Residencia", DireccionDTO.class);
		addressField.addValidator(ValidationUtils.addressValidator("Direcci\u00f3n de Residencia"));
		addressField.setImmediate(true);
		botonPruebas = new Button("Probar");
		boton2 = new Button("Asignar Direccion");

		gridDs = new BeanItemContainer<Medidor>(Medidor.class);
		container = new GeneratedPropertyContainer(gridDs);
		grid = new Grid("Medidores", container);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.getColumn("id").setHeaderCaption("Identificador");//Long
		grid.getColumn("serial").setHeaderCaption("Serial");//String
		grid.getColumn("gateway").setHeaderCaption("Gateway");//Gateway
		grid.getColumn("suscriptor").setHeaderCaption("Id. Suscriptor");//Long
		grid.getColumn("fechaInstalacion").setHeaderCaption("Fecha Instalaci\u00f3n");//Timestamp
		grid.getColumn("lecturaInicial").setHeaderCaption("Lectura Inicial");//Double
		container.addGeneratedProperty("gateway", new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				if(propertyId == null) return null;
				BeanItem<Medidor> dto = (BeanItem<Medidor>) item;
				return new StringBuilder()
				.append("<a href=\"javascript:void(0)\" ")
				.append("onclick=\"").append(doChooseFmt.format(new Object[]{dto.getBean().getId()})).append("\"")
				.append(">")
				.append(dto.getBean().getGateway().getIp())
				.append("|").append(dto.getBean().getGateway().getDescripcion())
				.append("</a>").toString();
			}

			@Override
			public Class<String> getType() {
				return String.class;
			}
			
		});
		grid.getColumn("gateway").setRenderer(new HtmlRenderer());
		
		
		pager = new Pager<Medidor, Medidor>();
		pager.setMaxCachedPages(5);
		
		menuContainer = new TreeMenu();
		menuContainer.setWidth(200, Unit.PIXELS);
		menuContainer.setShowSearcher();
		
		rightPanel.addComponent(welcomeLabel);
		rightPanel.addComponent(linkLabel);
		rightPanel.addComponent(grid);
		rightPanel.addComponent(pager);
		rightPanel.addComponent(addressField);
		rightPanel.addComponent(botonPruebas);
		rightPanel.addComponent(boton2);
		
		splitPanel.setFirstComponent(menuContainer);
		splitPanel.setSecondComponent(rightPanel);
		splitPanel.setSplitPosition(200, Unit.PIXELS);
		splitPanel.setSizeFull();
		
		
		addressField.setPropertyDataSource(addressFieldProperty);
		addressField.setValue(direccion);
		
		JavaScript.getCurrent().addFunction("doChoose", new JavaScriptFunction() {
			@Override
			public void call(JsonArray arguments) {
				Long idMedidor = Double.valueOf(arguments.getNumber(0)).longValue();
				Notification.show("Clickado", "Haz clickado sobre el medidor "+idMedidor, Type.HUMANIZED_MESSAGE);
			}
		});
		
		return splitPanel;
	}
	
	public void setContentPane(Component component){
		splitPanel.setSecondComponent(component);
		
	}
	
	
}
