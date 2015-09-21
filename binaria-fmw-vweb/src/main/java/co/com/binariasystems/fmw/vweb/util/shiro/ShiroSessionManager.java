package co.com.binariasystems.fmw.vweb.util.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.HttpServletSession;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;

public class ShiroSessionManager implements WebSessionManager {
	public Session start(SessionContext context) throws AuthorizationException {
        return createSession(context);
    }

    public Session getSession(SessionKey key) throws SessionException {
    	// Retrieve the VaadinSession for the current user.
	    VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (!WebUtils.isHttp(key) && vaadinSession == null) {
            String msg = "SessionKey must be an HTTP compatible implementation.";
            throw new IllegalArgumentException(msg);
        }

        HttpServletRequest request = WebUtils.getHttpRequest(key);

        Session session = null;
        HttpSession httpSession = null;
        
      //Caso posible de request == null es cuando se hace una peticion en un hilo diferente al del httprquestactual, por tanto debe
        //Obtenerse la session desde Vaadin. Esto debido a la capacidad de vaadin por medio del PUSH de realizar comunicacion asyncrona
        if(request != null)
        	httpSession = request.getSession(false);
        else if(vaadinSession != null && vaadinSession.getSession() instanceof WrappedHttpSession)
        	httpSession = ((WrappedHttpSession)vaadinSession.getSession()).getHttpSession();
        
        if (httpSession != null) {
            session = createSession(httpSession, request.getRemoteHost());
        }

        return session;
    }

    private String getHost(SessionContext context) {
        String host = context.getHost();
        if (host == null) {
            ServletRequest request = WebUtils.getRequest(context);
            if (request != null) {
                host = request.getRemoteHost();
            }
        }
        return host;

    }

    /**
     * @since 1.0
     */
    protected Session createSession(SessionContext sessionContext) throws AuthorizationException {
    	VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (!WebUtils.isHttp(sessionContext) && vaadinSession == null) {
            String msg = "SessionContext must be an HTTP compatible implementation.";
            throw new IllegalArgumentException(msg);
        }

        HttpServletRequest request = WebUtils.getHttpRequest(sessionContext);
        
        
        HttpSession httpSession = null;
        
        //Caso posible de request == null es cuando se hace una peticion en un hilo diferente al del httprquestactual, por tanto debe
        //Obtenerse la session desde Vaadin. Esto debido a la capacidad de vaadin por medio del PUSH de realizar comunicacion asyncrona
        
        if(request != null)
        	httpSession = request.getSession();
        else if(vaadinSession != null && vaadinSession.getSession() instanceof WrappedHttpSession)
        	httpSession = ((WrappedHttpSession)vaadinSession.getSession()).getHttpSession();
        
        if(httpSession == null)
        	throw new AuthorizationException("Unable to locate current http session");
        
        //SHIRO-240: DO NOT use the 'globalSessionTimeout' value here on the acquired session.
        //see: https://issues.apache.org/jira/browse/SHIRO-240

        String host = getHost(sessionContext);
        Session shiroSession = createSession(httpSession, host);
        
        return shiroSession;
    }

    protected Session createSession(HttpSession httpSession, String host) {
        return new HttpServletSession(httpSession, host);
    }

    /**
     * This implementation always delegates to the servlet container for sessions, so this method returns
     * {@code true} always.
     *
     * @return {@code true} always
     * @since 1.2
     */
	public boolean isServletContainerSessions() {
		return true;
	}
}
