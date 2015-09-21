package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import org.apache.commons.lang3.StringUtils;

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
	
	
	public MenuButtonBuilder withPixelsWidth(float width){
		setWidth(width, Unit.PIXELS);
		return this;
	}
	
	public MenuButtonBuilder withPointsWidth(float width){
		setWidth(width, Unit.POINTS);
		return this;
	}
	
	public MenuButtonBuilder withEmWidth(float width){
		setWidth(width, Unit.EM);
		return this;
	}
	
	public MenuButtonBuilder withRemWidth(float width){
		setWidth(width, Unit.REM);
		return this;
	}
	
	public MenuButtonBuilder withMilimetersWidth(float width){
		setWidth(width, Unit.MM);
		return this;
	}
	
	public MenuButtonBuilder withCentimetersWidth(float width){
		setWidth(width, Unit.CM);
		return this;
	}
	
	public MenuButtonBuilder withInchsWidth(float width){
		setWidth(width, Unit.INCH);
		return this;
	}
	
	public MenuButtonBuilder withPixelsHeight(float height){
		setHeight(height, Unit.PIXELS);
		return this;
	}
	
	public MenuButtonBuilder withPointsHeight(float height){
		setHeight(height, Unit.POINTS);
		return this;
	}
	
	public MenuButtonBuilder withEmHeight(float height){
		setHeight(height, Unit.EM);
		return this;
	}
	
	public MenuButtonBuilder withRemHeight(float height){
		setHeight(height, Unit.REM);
		return this;
	}
	
	public MenuButtonBuilder withMilimetersHeight(float height){
		setHeight(height, Unit.MM);
		return this;
	}
	
	public MenuButtonBuilder withCentimetersHeight(float height){
		setHeight(height, Unit.CM);
		return this;
	}
	
	public MenuButtonBuilder withInchsHeight(float height){
		setHeight(height, Unit.INCH);
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
