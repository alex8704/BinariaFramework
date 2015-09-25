package co.com.binariasystems.fmw.util.velocity;

import org.apache.velocity.app.VelocityEngine;

public interface VelocityEngineFactoryHelper {
	public void setVelocityConfigLocation(String velocityConfigLocation);
	public void setResourceLoaderClass(Class resourceLoaderClass);
	public VelocityEngine getEngine();
}
