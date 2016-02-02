package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;

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
	
	
	public ButtonBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public ButtonBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
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
	
	public ButtonBuilder disable(){
		setEnabled(false);
		return this;
	}
	
	public ButtonBuilder enable(){
		setEnabled(true);
		return this;
	}
	
	public ButtonBuilder withData(Object data){
		setData(data);
		return this;
	}
	
}
