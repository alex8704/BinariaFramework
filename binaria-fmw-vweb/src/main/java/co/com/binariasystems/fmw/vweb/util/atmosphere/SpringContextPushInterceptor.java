package co.com.binariasystems.fmw.vweb.util.atmosphere;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringContextPushInterceptor extends AtmosphereInterceptorAdapter{
	private static final String REQUEST_ATTRIBUTES_ATTRIBUTE =
			RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";
	
	@Override
	public Action inspect(AtmosphereResource r) {
		HttpServletRequest request = r.getRequest();
		ServletRequestAttributes attributes = new ServletRequestAttributes(request);
		request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
		LocaleContextHolder.setLocale(request.getLocale());
		RequestContextHolder.setRequestAttributes(attributes);
		
		return Action.CONTINUE;
	}
	
	@Override
	public void postInspect(AtmosphereResource r) {
		ServletRequestAttributes attributes = null;
		Object reqAttr =  r.getRequest().getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
		if (reqAttr instanceof ServletRequestAttributes) {
			attributes = (ServletRequestAttributes) reqAttr;
		}
		RequestAttributes threadAttributes = RequestContextHolder.getRequestAttributes();
		if (threadAttributes != null) {
			// We're assumably within the original request thread...
			LocaleContextHolder.resetLocaleContext();
			RequestContextHolder.resetRequestAttributes();
			if (attributes == null && threadAttributes instanceof ServletRequestAttributes) {
				attributes = (ServletRequestAttributes) threadAttributes;
			}
		}
		if (attributes != null) {
			attributes.requestCompleted();
		}
	}
}
