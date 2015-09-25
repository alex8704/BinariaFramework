package co.com.binariasystems.fmw.util.velocity;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;

public class VelocityEngineFactoryWebHelper extends VelocityEngineFactory implements ServletContextAware,VelocityEngineFactoryHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(VelocityEngineFactory.class);
	private ServletContext servletContext;
	private String velocityConfigLocation;
	private Class resourceLoaderClass;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private VelocityEngine velocityEngine;

	@PostConstruct
	protected void postConstruct() {
		LOGGER.info("servletContext: {"+ servletContext +"}");
		try{
			setOverrideLogging(true);
			if(StringUtils.isNotEmpty(velocityConfigLocation))
				setConfigLocation((resourceLoaderClass != null ? resourceLoaderClass :getClass()).getResource(velocityConfigLocation));
			this.velocityEngine = createVelocityEngine();
		}catch(IOException | VelocityException ex){
			LOGGER.error(ex.toString());
			throw new FMWUncheckedException(ex);
		}
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

	public Class getResourceLoaderClass() {
		return resourceLoaderClass;
	}

	public void setResourceLoaderClass(Class resourceLoaderClass) {
		this.resourceLoaderClass = resourceLoaderClass;
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
