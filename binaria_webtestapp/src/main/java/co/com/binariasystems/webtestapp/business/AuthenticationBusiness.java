package co.com.binariasystems.webtestapp.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.com.binariasystems.fmw.business.FMWBusiness;

public interface AuthenticationBusiness extends FMWBusiness {
	
	public String dato();
	
	
	@Service
	@Transactional(
			propagation=Propagation.SUPPORTS, readOnly=true
			)
	public static class AuthenticationBusinessImpl implements AuthenticationBusiness{
		@Override
		public String dato() {
			return "{Este es el DATO gilipollete}";
		}
	}
}
