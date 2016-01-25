package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;

import com.vaadin.data.Property;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class LabelBuilder extends Label {
	private boolean boldStyle;

	public LabelBuilder() {
		super();
		setContentMode(ContentMode.HTML);
	}

	public LabelBuilder(Property contentSource, ContentMode contentMode) {
		super(contentSource, contentMode);
	}

	public LabelBuilder(Property contentSource) {
		super(contentSource);
		setContentMode(ContentMode.HTML);
	}

	public LabelBuilder(String content, ContentMode contentMode) {
		super(content, contentMode);
	}

	public LabelBuilder(String content) {
		super(content);
		setContentMode(ContentMode.HTML);
	}
	
	public LabelBuilder withContentMode(ContentMode contentMode){
		setContentMode(contentMode);
		return this;
	}
	
	public LabelBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public LabelBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public LabelBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public LabelBuilder withValue(String value){
		setValue(value);
		return this;
	}
	
	public LabelBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public LabelBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public LabelBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public LabelBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public LabelBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public LabelBuilder boldStyle(){
		if(!boldStyle){
			addStyleName(ValoTheme.LABEL_BOLD);
			boldStyle = !boldStyle;
		}
		return this;
	}
	
}
