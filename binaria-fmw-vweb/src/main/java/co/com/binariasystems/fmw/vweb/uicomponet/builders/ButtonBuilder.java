package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public class ButtonBuilder extends Button{

	public ButtonBuilder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ButtonBuilder(Resource icon) {
		super(icon);
		// TODO Auto-generated constructor stub
	}

	public ButtonBuilder(String caption, ClickListener listener) {
		super(caption, listener);
	}

	public ButtonBuilder(String caption, Resource icon) {
		super(caption, icon);
	}

	public ButtonBuilder(String caption) {
		super(caption);
	}
	
	
	public ButtonBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public ButtonBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public ButtonBuilder withLinkStyle(){
		addStyleName(ValoTheme.BUTTON_LINK);
		return this;
	}
	
	public ButtonBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public ButtonBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	
	public ButtonBuilder withPixelsWidth(float width){
		setWidth(width, Unit.PIXELS);
		return this;
	}
	
	public ButtonBuilder withPointsWidth(float width){
		setWidth(width, Unit.POINTS);
		return this;
	}
	
	public ButtonBuilder withEmWidth(float width){
		setWidth(width, Unit.EM);
		return this;
	}
	
	public ButtonBuilder withRemWidth(float width){
		setWidth(width, Unit.REM);
		return this;
	}
	
	public ButtonBuilder withMilimetersWidth(float width){
		setWidth(width, Unit.MM);
		return this;
	}
	
	public ButtonBuilder withCentimetersWidth(float width){
		setWidth(width, Unit.CM);
		return this;
	}
	
	public ButtonBuilder withInchsWidth(float width){
		setWidth(width, Unit.INCH);
		return this;
	}
	
	public ButtonBuilder withPixelsHeight(float height){
		setHeight(height, Unit.PIXELS);
		return this;
	}
	
	public ButtonBuilder withPointsHeight(float height){
		setHeight(height, Unit.POINTS);
		return this;
	}
	
	public ButtonBuilder withEmHeight(float height){
		setHeight(height, Unit.EM);
		return this;
	}
	
	public ButtonBuilder withRemHeight(float height){
		setHeight(height, Unit.REM);
		return this;
	}
	
	public ButtonBuilder withMilimetersHeight(float height){
		setHeight(height, Unit.MM);
		return this;
	}
	
	public ButtonBuilder withCentimetersHeight(float height){
		setHeight(height, Unit.CM);
		return this;
	}
	
	public ButtonBuilder withInchsHeight(float height){
		setHeight(height, Unit.INCH);
		return this;
	}
	
	public ButtonBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public ButtonBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
}
