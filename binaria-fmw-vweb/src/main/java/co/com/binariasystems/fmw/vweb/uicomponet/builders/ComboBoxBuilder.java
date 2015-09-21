package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.ui.ComboBox;

public class ComboBoxBuilder extends ComboBox{
	private boolean useCaptionAsInputPrompt;

	public ComboBoxBuilder() {
		super();
		setDefaults();
	}

	public ComboBoxBuilder(String caption, Collection<?> options) {
		super(caption, options);
		setDefaults();
	}

	public ComboBoxBuilder(String caption, Container dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public ComboBoxBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	public ComboBoxBuilder withDataSource(Container dataSource){
		setContainerDataSource(dataSource);
		return this;
	}
	
	public ComboBoxBuilder withOptions(Collection<?> options){
		final Container c = getContainerDataSource() != null ? getContainerDataSource() : new IndexedContainer();
		c.removeAllItems();
        if (options != null) {
            for (final Iterator<?> i = options.iterator(); i.hasNext();) {
                c.addItem(i.next());
            }
        }
		
		if(getContainerDataSource() == null)
			setContainerDataSource(c);
		return this;
	}
	
	public ComboBoxBuilder usePropertyAsCaption(Object propertyId){
		setItemCaptionPropertyId(propertyId);
		return this;
	}
	
	public ComboBoxBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public ComboBoxBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public ComboBoxBuilder readOnly(){
		setReadOnly(true);
		return this;
	}
	
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if(StringUtils.isEmpty(getDescription()))
			setDescription(caption);
		if(useCaptionAsInputPrompt && StringUtils.isNoneEmpty(caption))
			setInputPrompt(caption);
		setRequiredError(ValidationUtils.requiredErrorFor(caption));
	}
	
	public ComboBoxBuilder useCaptioAsPrompt(){
		useCaptionAsInputPrompt = true;
		if(StringUtils.isNoneEmpty(getCaption()))
			setInputPrompt(getCaption());
		return this;
	}
	
	public ComboBoxBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public ComboBoxBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public ComboBoxBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public ComboBoxBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public ComboBoxBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public ComboBoxBuilder withValue(Object value){
		setValue(value);
		return this;
	}
	
	public ComboBoxBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public ComboBoxBuilder withPixelsWidth(float width){
		setWidth(width, Unit.PIXELS);
		return this;
	}
	
	public ComboBoxBuilder withPointsWidth(float width){
		setWidth(width, Unit.POINTS);
		return this;
	}
	
	public ComboBoxBuilder withEmWidth(float width){
		setWidth(width, Unit.EM);
		return this;
	}
	
	public ComboBoxBuilder withRemWidth(float width){
		setWidth(width, Unit.REM);
		return this;
	}
	
	public ComboBoxBuilder withMilimetersWidth(float width){
		setWidth(width, Unit.MM);
		return this;
	}
	
	public ComboBoxBuilder withCentimetersWidth(float width){
		setWidth(width, Unit.CM);
		return this;
	}
	
	public ComboBoxBuilder withInchsWidth(float width){
		setWidth(width, Unit.INCH);
		return this;
	}
	
	public ComboBoxBuilder withPixelsHeight(float height){
		setHeight(height, Unit.PIXELS);
		return this;
	}
	
	public ComboBoxBuilder withPointsHeight(float height){
		setHeight(height, Unit.POINTS);
		return this;
	}
	
	public ComboBoxBuilder withEmHeight(float height){
		setHeight(height, Unit.EM);
		return this;
	}
	
	public ComboBoxBuilder withRemHeight(float height){
		setHeight(height, Unit.REM);
		return this;
	}
	
	public ComboBoxBuilder withMilimetersHeight(float height){
		setHeight(height, Unit.MM);
		return this;
	}
	
	public ComboBoxBuilder withCentimetersHeight(float height){
		setHeight(height, Unit.CM);
		return this;
	}
	
	public ComboBoxBuilder withInchsHeight(float height){
		setHeight(height, Unit.INCH);
		return this;
	}
	
	public ComboBoxBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public ComboBoxBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public ComboBoxBuilder addListableItem(Listable listableItem){
		if(listableItem != null){
			addItem(listableItem);
			setItemCaption(listableItem, listableItem.getDescription());
		}
		return this;
	}
	
	public ComboBoxBuilder addItem(Object itemId, String caption){
		addItem(itemId, caption, null);
		return this;
	}
	
	public ComboBoxBuilder addItem(Object itemId, String caption, Resource icon){
		addItem(itemId, caption, icon, false);
		return this;
	}
	
	public ComboBoxBuilder addItem(Object itemId, String caption, Resource icon, boolean selected){
		addItem(itemId);
		setItemCaption(itemId, caption);
		if(icon != null)
			setItemIcon(itemId, icon);
		if(selected)
			select(itemId);
		return this;
	}
	
	public ComboBoxBuilder withIconAndCaptionProperties(Object captionProperty, Object iconProperty){
		setItemCaptionPropertyId(captionProperty);
		if(iconProperty != null)
				setItemIconPropertyId(iconProperty);
		return this;
	}
	
	
}
