package co.com.binariasystems.fmw.vweb.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.AddressFieldsToDTOMappingInfo;

import com.vaadin.data.validator.AbstractValidator;


public class AddressFieldValidator extends AbstractValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AddressFieldValidator.class);
	private AddressFieldsToDTOMappingInfo fieldsToPropertyMapping;
	public AddressFieldValidator(String errorMessage) {
		this(errorMessage, null);
	}
	
	public AddressFieldValidator(String errorMessage, AddressFieldsToDTOMappingInfo fieldsToPropertyMapping) {
		super(errorMessage);
		setFieldsToPropertyMapping(fieldsToPropertyMapping);
	}
	
	@Override
	protected boolean isValidValue(Object value) {
		try {
			return value != null && ((StringUtils.isNotEmpty(getMainViaType(value)) && getMainViaNum(value) != null)
					|| (StringUtils.isNotEmpty(getMainViaComplement(value)) && StringUtils.isNoneBlank(getMainViaComplementDetail(value)))
					|| (StringUtils.isNotEmpty(getComplementaryViaComplement(value)) && StringUtils.isNoneBlank(getComplementaryViaComplementDetail(value))));
		} catch (ReflectiveOperationException e) {
			LOGGER.error("AddressFieldValidator error :"+e.getMessage(), e);
			return false;
		}
	}
	
	private String getMainViaType(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getMainViaTypeProperty());
	}
	private String getMainViaNum(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getMainViaNumProperty());
	}
	private String getMainViaComplement(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getMainViaComplementProperty());
	}
	private String getMainViaComplementDetail(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getMainViaComplementDetailProperty());
	}
	private String getComplementaryViaComplement(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getComplementaryViaComplementProperty());
	}
	private String getComplementaryViaComplementDetail(Object addressObj) throws ReflectiveOperationException{
		return getFieldPropertyValue(addressObj, fieldsToPropertyMapping.getComplementaryViaComplementDetailProperty());
	}
	
	private String getFieldPropertyValue(Object addressObj, String propertyName) throws ReflectiveOperationException{
		if(addressObj == null) return null;
		Object value = PropertyUtils.getProperty(addressObj, propertyName);
		return value == null ? null : value.toString();
	}

	@Override
	public Class getType() {
		return Object.class;
	}

	/**
	 * @return the fieldsToPropertyMapping
	 */
	public AddressFieldsToDTOMappingInfo getFieldsToPropertyMapping() {
		return fieldsToPropertyMapping;
	}

	/**
	 * @param fieldsToPropertyMapping the fieldsToPropertyMapping to set
	 */
	public void setFieldsToPropertyMapping(AddressFieldsToDTOMappingInfo fieldsToPropertyMapping) {
		this.fieldsToPropertyMapping = fieldsToPropertyMapping != null ? fieldsToPropertyMapping : new AddressFieldsToDTOMappingInfo();
	}

	

}
