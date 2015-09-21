package co.com.binariasystems.fmw.vweb.uicomponet.builders;

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
	}

	public LabelBuilder(String content, ContentMode contentMode) {
		super(content, contentMode);
	}

	public LabelBuilder(String content) {
		super(content);
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
	
	public LabelBuilder withPixelsWidth(float width){
		setWidth(width, Unit.PIXELS);
		return this;
	}
	
	public LabelBuilder withPointsWidth(float width){
		setWidth(width, Unit.POINTS);
		return this;
	}
	
	public LabelBuilder withEmWidth(float width){
		setWidth(width, Unit.EM);
		return this;
	}
	
	public LabelBuilder withRemWidth(float width){
		setWidth(width, Unit.REM);
		return this;
	}
	
	public LabelBuilder withMilimetersWidth(float width){
		setWidth(width, Unit.MM);
		return this;
	}
	
	public LabelBuilder withCentimetersWidth(float width){
		setWidth(width, Unit.CM);
		return this;
	}
	
	public LabelBuilder withInchsWidth(float width){
		setWidth(width, Unit.INCH);
		return this;
	}
	
	public LabelBuilder withPixelsHeight(float height){
		setHeight(height, Unit.PIXELS);
		return this;
	}
	
	public LabelBuilder withPointsHeight(float height){
		setHeight(height, Unit.POINTS);
		return this;
	}
	
	public LabelBuilder withEmHeight(float height){
		setHeight(height, Unit.EM);
		return this;
	}
	
	public LabelBuilder withRemHeight(float height){
		setHeight(height, Unit.REM);
		return this;
	}
	
	public LabelBuilder withMilimetersHeight(float height){
		setHeight(height, Unit.MM);
		return this;
	}
	
	public LabelBuilder withCentimetersHeight(float height){
		setHeight(height, Unit.CM);
		return this;
	}
	
	public LabelBuilder withInchsHeight(float height){
		setHeight(height, Unit.INCH);
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
