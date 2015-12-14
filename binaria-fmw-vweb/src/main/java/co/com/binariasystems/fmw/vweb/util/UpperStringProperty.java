package co.com.binariasystems.fmw.vweb.util;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

public class UpperStringProperty<T extends CharSequence> extends ObjectProperty<T> {

	public UpperStringProperty(T value, Class<T> type, boolean readOnly) {
		super(value, type, readOnly);
	}

	public UpperStringProperty(T value, Class<T> type) {
		super(value, type);
	}

	public UpperStringProperty(T value) {
		super(value);
	}
	
	 @Override
    public void setValue(T newValue) throws Property.ReadOnlyException {
		 super.setValue((T) (StringUtils.isNotBlank(newValue) ? StringUtils.upperCase(newValue.toString()) : newValue));
    }
	
}
