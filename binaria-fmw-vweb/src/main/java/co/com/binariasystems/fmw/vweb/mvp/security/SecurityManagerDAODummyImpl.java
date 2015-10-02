package co.com.binariasystems.fmw.vweb.mvp.security;


public class SecurityManagerDAODummyImpl implements SecurityManagerDAO{

	@Override
	public boolean isAuthorized(String resourceUrl) {
		return true;
	}

}
