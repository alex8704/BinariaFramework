package co.com.binariasystems.fmw.security.realm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.authc.SecurityPrincipalConverter;

@SuppressWarnings("rawtypes")
public class ShiroBasedJDBCRealm extends JdbcRealm{
	private static final Logger log = LoggerFactory.getLogger(ShiroBasedJDBCRealm.class);
	
	private String saltStyleName;
	private SecurityPrincipalConverter principalConverter;
	
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

	public SecurityPrincipalConverter getPrincipalConverter() {
		return principalConverter;
	}

	public void setPrincipalConverter(
			SecurityPrincipalConverter principalConverter) {
		this.principalConverter = principalConverter;
	}
	
	@SuppressWarnings("unchecked")
	private Object createPrincipalForUser(String username){
		Object principal = null;
		try{
			principal = (principalConverter != null) ? principalConverter.toPrincipalModel(username) : username;
		}catch(FMWSecurityException ex){
			throw new AuthenticationException(ex.getMessage());
		}
		return principal;
	}
	
	@SuppressWarnings("unchecked")
	private String getUsernameForPrincipal(Object principal){
		String username = null;
		try{
			username = (principalConverter != null) ? principalConverter.toPrincipalRepresentation(principal).toString() : (String)principal;
		}catch(FMWSecurityException ex){
			throw new AuthenticationException(ex.getMessage());
		}
		return username;
	}
	
	private String[] getPasswordForUser(Connection conn, String username) throws SQLException {

        String[] result;
        boolean returningSeparatedSalt = false;
        switch (saltStyle) {
        case NO_SALT:
        case CRYPT:
        case EXTERNAL:
            result = new String[1];
            break;
        default:
            result = new String[2];
            returningSeparatedSalt = true;
        }
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(authenticationQuery);
            ps.setString(1, username);

            // Execute query
            rs = ps.executeQuery();

            // Loop over results - although we are only expecting one result, since usernames should be unique
            boolean foundResult = false;
            while (rs.next()) {

                // Check to ensure only one row is processed
                if (foundResult) {
                    throw new AuthenticationException("More than one user row found for user [" + username + "]. Usernames must be unique.");
                }

                result[0] = rs.getString(1);
                if (returningSeparatedSalt) {
                    result[1] = rs.getString(2);
                }

                foundResult = true;
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
        }

        return result;
    }
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        Connection conn = null;
        SimpleAuthenticationInfo info = null;
        try {
            conn = dataSource.getConnection();

            String password = null;
            String salt = null;
            switch (saltStyle) {
            case NO_SALT:
                password = getPasswordForUser(conn, username)[0];
                break;
            case CRYPT:
                // TODO: separate password and hash from getPasswordForUser[0]
                throw new ConfigurationException("Not implemented yet");
                //break;
            case COLUMN:
                String[] queryResults = getPasswordForUser(conn, username);
                password = queryResults[0];
                salt = queryResults[1];
                break;
            case EXTERNAL:
                password = getPasswordForUser(conn, username)[0];
                salt = getSaltForUser(username);
            }

            if (password == null) {
                throw new UnknownAccountException("No account found for user [" + username + "]");
            }
            
            info = new SimpleAuthenticationInfo(createPrincipalForUser(username), password.toCharArray(), getName());
            
            if (salt != null) {
                info.setCredentialsSalt(ByteSource.Util.bytes(salt));
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authenticating user [" + username + "]";
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }

            // Rethrow any SQL errors as an authentication exception
            throw new AuthenticationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        return info;
    }
	
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        
        String username = getUsernameForPrincipal(getAvailablePrincipal(principals));

        Connection conn = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            conn = dataSource.getConnection();

            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(conn, username);
            if (permissionsLookupEnabled) {
                permissions = getPermissions(conn, username, roleNames);
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authorizing user [" + username + "]";
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }

            // Rethrow any SQL errors as an authorization exception
            throw new AuthorizationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;

    }
	
}
