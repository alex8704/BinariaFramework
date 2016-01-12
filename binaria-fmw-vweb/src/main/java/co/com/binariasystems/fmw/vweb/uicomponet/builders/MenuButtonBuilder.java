package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;

public class MenuButtonBuilder extends MenuBar {
	private MenuItem defaultItem;
	
	private MenuButtonBuilder(){super();}
	
	public MenuButtonBuilder(String caption) {
		this(caption, null);
	}
	
	public MenuButtonBuilder(Resource icon) {
		this(null, icon);
	}
	
	public MenuButtonBuilder(String caption, Resource icon) {
		super();
		defaultItem = addItem(StringUtils.defaultIfEmpty(caption, "-"), icon, null);
		addItem("", null);
	}
	
	public MenuButtonBuilder withDefaultCommand(Command command){
		defaultItem.setCommand(command);
		return this;
	}
	
	public MenuButtonBuilder withItem(String caption, Command command){
		addItem(caption, command);
		return this;
	}
	
	public MenuButtonBuilder withItem(String caption, Command command, String description){
		MenuItem item = addItem(caption, command);
		item.setDescription(description);
		return this;
	}
	
	
	public MenuButtonBuilder withItem(String caption, Command command, Resource icon){
		addItem(caption, icon, command);
		return this;
	}
	
	public MenuButtonBuilder withItem(String caption, Command command, Resource icon, String description){
		MenuItem item = addItem(caption, icon, command);
		item.setDescription(description);
		return this;
	}
	
	public MenuButtonBuilder withItem(Command command, Resource icon, String description){
		MenuItem item = addItem("", icon, command);
		item.setDescription(description);
		return this;
	}
	
	public MenuButtonBuilder withTooltip(String tooltip){
		defaultItem.setDescription(tooltip);
		return this;
	}
	
	public MenuButtonBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	
	public MenuButtonBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public MenuButtonBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public MenuButtonBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public MenuButtonBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
}
