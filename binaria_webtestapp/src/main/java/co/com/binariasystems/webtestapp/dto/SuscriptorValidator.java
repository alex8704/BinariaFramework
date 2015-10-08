package co.com.binariasystems.webtestapp.dto;

import co.com.binariasystems.fmw.entity.exception.EntityCRUDValidationException;
import co.com.binariasystems.fmw.entity.validator.EntityValidator;

public class SuscriptorValidator implements EntityValidator<Suscriptor> {

	@Override
	public void beforeInsert(Suscriptor masterBean) throws EntityCRUDValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(Suscriptor masterBean) throws EntityCRUDValidationException {
		
	}

	@Override
	public void beforeDelete(Suscriptor masterBean) throws EntityCRUDValidationException {
		// TODO Auto-generated method stub

	}

}
