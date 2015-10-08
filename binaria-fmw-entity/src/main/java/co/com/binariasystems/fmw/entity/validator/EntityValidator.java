package co.com.binariasystems.fmw.entity.validator;

import co.com.binariasystems.fmw.entity.exception.EntityCRUDValidationException;


/**
 * 
 * @author Alexander Castro O.
 */

public interface EntityValidator<T> {
	public void beforeInsert(T masterBean) throws EntityCRUDValidationException;
	public void beforeUpdate(T masterBean) throws EntityCRUDValidationException;
	public void beforeDelete(T masterBean) throws EntityCRUDValidationException;
}
