package co.com.binariasystems.fmw.vweb.mvp.security;

import co.com.binariasystems.fmw.business.FMWBusiness;

public interface SecurityManager extends FMWBusiness{
	public boolean isPublicView(String url);
	public String getForbiddenViewUrl();
	public String getDashBoardViewUrl();
	
	public boolean isAuthorized(String resourceUrl) throws Exception;
}
