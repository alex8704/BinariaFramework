package co.com.binariasystems.fmw.vweb.uicomponet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.vweb.resources.resources;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/*
 * Clase Util para crear cuadros de dialogo de manera sencilla, tanto modales como no modales
 * 
 * @author Alexander Castro O.
 */

public class MessageDialog extends Window implements ClickListener{
	private VerticalLayout contentPane;
	private Label titleLbl;
	private HorizontalLayout centralPanel;
	private Label informationLbl;
	private HorizontalLayout btnsPanel;
	private Resource iconResource;
	private Type type;
	private Button okBtn;
	private Button noBtn;
	
	private String title;
	private String message;
	
	private MessageDialog(){
	}
	
	public MessageDialog(String title, String message){
		this(title, message, Type.INFORMATION);
	}
	
	public MessageDialog(String title, String message, Type type){
		this.title = title;
		this.message = message;
		this.type = type;
		setWidth(450.0f, Unit.PIXELS);
		if(type == Type.QUESTION)
			setModal(true);
		ensureDefaults();
		buildUI();
		center();
		setResizable(false);
	}
	
	public enum Type {
		ERROR("msgd-error", "Error.gif"), INFORMATION("msgd-information", "Inform.gif"), PLANE("msgd-plane", null), WARNING("msgd-warning", "Warn.gif"), QUESTION("msgd-question", "Question.gif");
		String style;
		String iconPath;
		Type(String style, String iconPath){
			this.style = style;
			this.iconPath = iconPath;
		}
		
		public String getStyle(){
			return style;
		}
		
		public String getIconPath(){
			return iconPath;
		}
		
		
	};
	
	private void ensureDefaults(){
		type = (type != null) ? type : Type.INFORMATION;
	}
	
	private void buildUI(){
		Image iconImg = null;
		contentPane = new VerticalLayout();
		centralPanel = new HorizontalLayout();
		titleLbl = new Label("", ContentMode.HTML);
		informationLbl = new Label("", ContentMode.HTML);
		btnsPanel = new HorizontalLayout();
		okBtn = new Button();
		
		titleLbl.setWidth(100, Unit.PERCENTAGE);
		titleLbl.setHeight(30, Unit.PIXELS);
		titleLbl.setValue(title);
		
		centralPanel.setWidth(100, Unit.PERCENTAGE);
		centralPanel.setSpacing(true);
		centralPanel.setMargin(new MarginInfo(true, true, true, true));
		
		if(type != Type.PLANE){
			
			iconResource = new ClassResource(resources.imagesPath() + type.getIconPath());
			iconImg = new Image(null, iconResource);
			iconImg.setWidth(50, Unit.PIXELS);
			iconImg.setHeight(50, Unit.PIXELS);
		}
		informationLbl.setWidth(100, Unit.PERCENTAGE);
		informationLbl.setValue(message);
		
		if(type != Type.PLANE){
			centralPanel.addComponent(iconImg);
			centralPanel.setComponentAlignment(iconImg, Alignment.MIDDLE_CENTER);
			iconImg.addStyleName("msgd");
		}
		
		centralPanel.addComponent(informationLbl);
		centralPanel.setExpandRatio(informationLbl, 1.0f);
		
		String okBtnText = "Aceptar";
		if(type == Type.QUESTION){
			okBtnText = "Si";
			noBtn = new Button("No");
			noBtn.setWidth(100, Unit.PIXELS);
		}
		
		okBtn.setCaption(okBtnText);
		okBtn.setWidth(100, Unit.PIXELS);
		
		
		btnsPanel.setSpacing(true);
		btnsPanel.setMargin(new MarginInfo(false, false, true, false));
		btnsPanel.addComponent(okBtn);
		if(type == Type.QUESTION){
			btnsPanel.addComponent(noBtn);
		}
		
		titleLbl.addStyleName("title-"+type.getStyle());
		informationLbl.addStyleName("msg-"+type.getStyle());
		btnsPanel.addStyleName("btns-msgd");
		
		
		contentPane.addComponent(titleLbl);
		contentPane.addComponent(centralPanel);
		contentPane.addComponent(btnsPanel);
		contentPane.setExpandRatio(centralPanel, 1.0f);
		contentPane.setComponentAlignment(btnsPanel, Alignment.MIDDLE_CENTER);
		
		this.setContent(contentPane);
		setCloseShortcut(KeyCode.ESCAPE);
		addYesClickListener(this);
		addNoClickListener(this);
		okBtn.focus();
	}
	
	public MessageDialog addYesClickListener(ClickListener listener){
		okBtn.addClickListener(listener);
		return this;
	}
	
	public MessageDialog addNoClickListener(ClickListener listener){
		if(noBtn != null)
			noBtn.addClickListener(listener);
		return this;
	}
	
	public void show(){
		UI.getCurrent().addWindow(this);
	}

	public void buttonClick(ClickEvent event) {
		close();
	}
	
	
	public static void showValidationErrors(String title, String message){
		new MessageDialog(StringUtils.defaultIfEmpty(title, VWebUtils.getCommonString(ValidationUtils.VALIDATION_ERROR_WINDOW_TITLE)), message, Type.WARNING).show();
	}
	
    public static void showErrorMessage(String title, String message){
		new MessageDialog(StringUtils.defaultIfEmpty(title, VWebUtils.getCommonString(ValidationUtils.VALIDATION_ERROR_WINDOW_TITLE)), message, Type.ERROR).show();
	}
    
    public static void showExceptions(Throwable ex, Logger logger){
		Throwable pretty = FMWExceptionUtils.prettyMessageException(ex);
		if(logger != null)
			logger.error(pretty.toString(), pretty);
		new MessageDialog(VWebUtils.getCommonString(ValidationUtils.VALIDATION_ERROR_WINDOW_TITLE), pretty.getMessage(), Type.ERROR).show();
	}
	
    public static void showExceptions(Throwable ex){
		Throwable pretty = FMWExceptionUtils.prettyMessageException(ex);
		new MessageDialog(VWebUtils.getCommonString(ValidationUtils.VALIDATION_ERROR_WINDOW_TITLE), pretty.getMessage(), Type.ERROR).show();
	}
    
    public Button yesButton(){
    	return okBtn;
    }
    
    public Button noButton(){
    	return noBtn;
    }
}