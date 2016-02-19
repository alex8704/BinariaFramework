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
import com.vaadin.ui.ListSelect;

public class ListBuilder extends ListSelect {

	public ListBuilder() {
		super();
		setDefaults();
	}

	public ListBuilder(String caption, Collection<?> options) {
		super(caption, options);
		setDefaults();
	}

	public ListBuilder(String caption, Container dataSource) {
		super(caption, dataSource);
		setDefaults();
	}

	public ListBuilder(String caption) {
		super(caption);
		setDefaults();
	}
	
	private void setDefaults(){
		setConversionError(VWebUtils.getCommonString(VWebCommonConstants.FIELD_CONVERSION_ERROR_DEFAULT_MSG));
		setInvalidCommitted(true);
		//setInvalidAllowed(invalidAllowed);
		//setValidationVisible(validateAutomatically);
	}
	
	public ListBuilder multipleSelection(){
		setMultiSelect(true);
		return this;
	}
	
	public ListBuilder withDataSource(Container dataSource){
		setContainerDataSource(dataSource);
		return this;
	}
	
	public ListBuilder withOptions(Collection<?> options){
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
	
	public ListBuilder usePropertyAsCaption(Object propertyId){
		setItemCaptionPropertyId(propertyId);
		return this;
	}
	
	public ListBuilder immediate(){
		setImmediate(true);
		return this;
	}
	
	public ListBuilder required(){
		if(!isRequired()){
			addValidator(ValidationUtils.nullValidator(getCaption()));
			setRequired(true);
		}
		return this;
	}
	
	public ListBuilder readOnly(){
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
	
	public ListBuilder withCaption(String caption) {
		setCaption(caption);
		return this;
	}
	
	public ListBuilder withValidators(Validator... validators){
		if(validators != null)
			for(Validator validator : validators)
				addValidator(validator);
		return this;
	}
	
	public ListBuilder withIcon(Resource icon){
		setIcon(icon);
		return this;
	}
	
	public ListBuilder withTooltip(String tooltip){
		setDescription(tooltip);
		return this;
	}
	
	public ListBuilder withStyleNames(String... styleNames){
		if(styleNames != null)
			for(String style : styleNames)
				addStyleName(style);
		return this;
	}
	
	public ListBuilder withValue(Object value){
		setValue(value);
		return this;
	}
	
	public ListBuilder withProperty(Property contentSource){
		setPropertyDataSource(contentSource);
		return this;
	}
	
	public ListBuilder withWidth(Dimension width){
		setWidth(width.value, width.unit);
		return this;
	}
	
	public ListBuilder withHeight(Dimension height){
		setWidth(height.value, height.unit);
		return this;
	}
	
	public ListBuilder withFullWidth(){
		setWidth(100, Unit.PERCENTAGE);
		return this;
	}
	
	public ListBuilder withFullHeight(){
		setHeight(100, Unit.PERCENTAGE);
		return this;
	}
	
	public ListBuilder addListableItem(Listable listableItem){
		if(listableItem != null){
			addItem(listableItem);
			setItemCaption(listableItem, listableItem.getDescription());
		}
		return this;
	}
	
	public ListBuilder addItem(Object itemId, String caption){
		addItem(itemId, caption, null);
		return this;
	}
	
	public ListBuilder addItem(Object itemId, String caption, Resource icon){
		addItem(itemId, caption, icon, false);
		return this;
	}
	
	public ListBuilder addItem(Object itemId, String caption, Resource icon, boolean selected){
		addItem(itemId);
		setItemCaption(itemId, caption);
		if(icon != null)
			setItemIcon(itemId, icon);
		if(selected)
			select(itemId);
		return this;
	}
	
	public ListBuilder withIconAndCaptionProperties(Object captionProperty, Object iconProperty){
		setItemCaptionPropertyId(captionProperty);
		if(iconProperty != null)
				setItemIconPropertyId(iconProperty);
		return this;
	}
}
