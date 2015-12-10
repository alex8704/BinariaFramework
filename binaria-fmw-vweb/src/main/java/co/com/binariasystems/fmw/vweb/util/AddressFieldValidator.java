package co.com.binariasystems.fmw.vweb.util;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField.Address;

import com.vaadin.data.validator.AbstractValidator;

public class AddressFieldValidator extends AbstractValidator<Address> {

	public AddressFieldValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(Address value) {
		return value != null && ((StringUtils.isNotEmpty(value.getMainViaType()) && value.getMainViaNum() != null)
				|| (StringUtils.isNotEmpty(value.getMainViaComplement()) && StringUtils.isNoneBlank(value.getMainViaComplementDetail()))
				|| (StringUtils.isNotEmpty(value.getComplementaryViaComplement()) && StringUtils.isNoneBlank(value.getComplementaryViaComplementDetail())));
	}

	@Override
	public Class<Address> getType() {
		return Address.class;
	}

}
