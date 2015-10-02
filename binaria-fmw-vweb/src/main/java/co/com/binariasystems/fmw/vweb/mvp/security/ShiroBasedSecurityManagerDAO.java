package co.com.binariasystems.fmw.vweb.mvp.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class ShiroBasedSecurityManagerDAO implements SecurityManagerDAO {

	@Override
	public boolean isAuthorized(String resourceUrl) {
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.isAuthenticated() && currentUser.isPermitted(resourceUrl);
	}

}
