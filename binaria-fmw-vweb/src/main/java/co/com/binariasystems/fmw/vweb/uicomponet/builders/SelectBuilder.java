package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.Dimension;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;
import co.com.binariasystems.fmw.vweb.util.ValidationUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.ui.NativeSelect;

public class SelectBuilder extends NativeSelect {
	private boolean useCaptionAsInputPrompt;

	public SelectBuilder() {
		super();
		setDefaults();
	}

	public SelectBuilder(String caption, Collection<?> options) {
		super(caption, options);
		setDefaults();
	}

	public SelectBuilder(String caption, Container dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public SelectBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		setInvalidCommitted(true);
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	public SelectBuilder withDataSource(Container dataSource){
		setContainerDataSource(dataSource);
		return this;
	}
	
	public SelectBuilder withOptions(Collection<?> options){
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
	
	public SelectBuilder usePropertyAsCaption(Object propertyId){
		setItemCaptionPropertyId(propertyId);
		return this;
	}
	
	public SelectBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public SelectBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public SelectBuilder readOnly(){
		setReadOnly(true);
		return this;
	}
	
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if(StringUtils.isEmpty(getDescription()))
			setDescription(caption);
		setRequiredError(ValidationUtils.requiredErrorFor(caption));
	}
	
	public SelectBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public SelectBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public SelectBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public SelectBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public SelectBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public SelectBuilder withValue(Object value){
		setValue(value);
		return this;
	}
	
	public SelectBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public SelectBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public SelectBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public SelectBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public SelectBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public SelectBuilder addListableItem(Listable listableItem){
		if(listableItem != null){
			addItem(listableItem);
			setItemCaption(listableItem, listableItem.getDescription());
		}
		return this;
	}
	
	public SelectBuilder addItem(Object itemId, String caption){
		addItem(itemId, caption, null);
		return this;
	}
	
	public SelectBuilder addItem(Object itemId, String caption, Resource icon){
		addItem(itemId, caption, icon, false);
		return this;
	}
	
	public SelectBuilder addItem(Object itemId, String caption, Resource icon, boolean selected){
		addItem(itemId);
		setItemCaption(itemId, caption);
		if(icon != null)
			setItemIcon(itemId, icon);
		if(selected)
			select(itemId);
		return this;
	}
	
	public SelectBuilder withIconAndCaptionProperties(Object captionProperty, Object iconProperty){
		setItemCaptionPropertyId(captionProperty);
		if(iconProperty != null)
				setItemIconPropertyId(iconProperty);
		return this;
	}
}
