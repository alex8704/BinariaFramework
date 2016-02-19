package co.com.binariasystems.fmw.vweb.util;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.validator.AbstractStringValidator;

public class CustomStringLengthValidator extends AbstractStringValidator {

	private Integer minLength = null;

    private Integer maxLength = null;

    public CustomStringLengthValidator(String errorMessage) {
        super(errorMessage);
    }
    
    public CustomStringLengthValidator(String errorMessage, Integer minLength,
            Integer maxLength) {
        this(errorMessage);
        setMinLength(minLength);
        setMaxLength(maxLength);
    }

    /**
     * Checks if the given value is valid.
     * 
     * @param value
     *            the value to validate.
     * @return <code>true</code> for valid value, otherwise <code>false</code>.
     */
    @Override
    protected boolean isValidValue(String value) {
    	if(StringUtils.isEmpty(value)) return true;
    	
        final int len = value.length();
        return !((minLength != null && minLength > -1 && len < minLength) 
        		|| (maxLength != null && maxLength > -1 && len > maxLength));
    }

    /**
     * Gets the maximum permissible length of the string.
     * 
     * @return the maximum length of the string or null if there is no limit
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the minimum permissible length of the string.
     * 
     * @return the minimum length of the string or null if there is no limit
     */
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * Sets the maximum permissible length of the string.
     * 
     * @param maxLength
     *            the maximum length to accept or null for no limit
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sets the minimum permissible length.
     * 
     * @param minLength
     *            the minimum length to accept or null for no limit
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }
	
}
