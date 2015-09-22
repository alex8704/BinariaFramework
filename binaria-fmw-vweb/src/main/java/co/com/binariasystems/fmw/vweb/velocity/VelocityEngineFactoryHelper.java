package co.com.binariasystems.fmw.vweb.velocity;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.web.context.ServletContextAware;

import co.com.binariasystems.fmw.util.velocity.VelocityEngineFactory;

public class VelocityEngineFactoryHelper extends VelocityEngineFactory implements ServletContextAware {
	private ServletContext servletContext;
	private String velocityConfigLocation;
	private Class resourceLoadeerClass;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private VelocityEngine velocityEngine;

	@PostConstruct
	protected void postConstruct() throws IOException, VelocityException {
		setOverrideLogging(true);
		if(StringUtils.isNotEmpty(velocityConfigLocation))
			setConfigLocation((resourceLoadeerClass != null ? resourceLoadeerClass :getClass()).getResource(velocityConfigLocation));
		this.velocityEngine = createVelocityEngine();
	}

	@Override
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine) throws IOException, VelocityException {
		super.postProcessVelocityEngine(velocityEngine);
		velocityEngine.setApplicationAttribute(ServletContext.class.getName(), servletContext);
	}

	public VelocityEngine getEngine() {
		return this.velocityEngine;
	}

	public String getVelocityConfigLocation() {
		return velocityConfigLocation;
	}

	public void setVelocityConfigLocation(String velocityConfigLocation) {
		this.velocityConfigLocation = velocityConfigLocation;
	}

	public Class getResourceLoadeerClass() {
		return resourceLoadeerClass;
	}

	public void setResourceLoadeerClass(Class resourceLoadeerClass) {
		this.resourceLoadeerClass = resourceLoadeerClass;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

}
