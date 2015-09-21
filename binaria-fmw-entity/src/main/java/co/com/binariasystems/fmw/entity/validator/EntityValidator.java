package co.com.binariasystems.fmw.entity.validator;

import co.com.binariasystems.fmw.entity.exception.EntityCRUDValidationException;


/**
 * 
 * @author Alexander Castro O.
 */

public interface EntityValidator {
	public void beforeInsert(Object masterBean) throws EntityCRUDValidationException;
	public void beforeUpdate(Object masterBean) throws EntityCRUDValidationException;
	public void beforeDelete(Object masterBean) throws EntityCRUDValidationException;
}
