package co.com.binariasystems.fmw.security.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.realm.jdbc.JdbcRealm;

public class ShiroBasedJDBCRealm extends JdbcRealm{
	
	private String saltStyleName;
	
	public ShiroBasedJDBCRealm() {
		super();
	}

	public String getSaltStyleName() {
		return saltStyleName;
	}

	public void setSaltStyleName(String saltStyleName) {
		this.saltStyleName = saltStyleName;
		if(StringUtils.isNotEmpty(saltStyleName))
			setSaltStyle(SaltStyle.valueOf(saltStyleName.toUpperCase()));
	}
	
	
}
