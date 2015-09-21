package co.com.binariasystems.fmw.util.velocity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.apache.velocity.runtime.log.NullLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.util.ClassUtils;
import co.com.binariasystems.fmw.util.PropertiesLoaderUtils;
import co.com.binariasystems.fmw.util.StringUtils;

public class VelocityEngineFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(VelocityEngineFactory.class);
	private URL configLocation;

	private final Map<String, Object> velocityProperties = new HashMap<String, Object>();

	private String resourceLoaderPath;


	private boolean overrideLogging = true;


	/**
	 * Set the location of the Velocity config file.
	 * Alternatively, you can specify all properties locally.
	 * @see #setVelocityProperties
	 * @see #setResourceLoaderPath
	 */
	public void setConfigLocation(URL configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set Velocity properties, like "file.resource.loader.path".
	 * Can be used to override values in a Velocity config file,
	 * or to specify all necessary properties locally.
	 * <p>Note that the Velocity resource loader path also be set to any
	 * Spring resource location via the "resourceLoaderPath" property.
	 * Setting it here is just necessary when using a non-file-based
	 * resource loader.
	 * @see #setVelocityPropertiesMap
	 * @see #setConfigLocation
	 * @see #setResourceLoaderPath
	 */
	public void setVelocityProperties(Properties velocityProperties) {
		mergePropertiesIntoMap(velocityProperties, this.velocityProperties);
	}

	/**
	 * Set Velocity properties as Map, to allow for non-String values
	 * like "ds.resource.loader.instance".
	 * @see #setVelocityProperties
	 */
	public void setVelocityPropertiesMap(Map<String, Object> velocityPropertiesMap) {
		if (velocityPropertiesMap != null) {
			this.velocityProperties.putAll(velocityPropertiesMap);
		}
	}

	/**
	 * Set the Velocity resource loader path via a Spring resource location.
	 * Accepts multiple locations in Velocity's comma-separated path style.
	 * <p>When populated via a String, standard URLs like "file:" and "classpath:"
	 * pseudo URLs are supported, as understood by ResourceLoader. Allows for
	 * relative paths when running in an ApplicationContext.
	 * <p>Will define a path for the default Velocity resource loader with the name
	 * "file". If the specified resource cannot be resolved to a {@code java.io.File},
	 * a generic SpringResourceLoader will be used under the name "spring", without
	 * modification detection.
	 * <p>Note that resource caching will be enabled in any case. With the file
	 * resource loader, the last-modified timestamp will be checked on access to
	 * detect changes. With SpringResourceLoader, the resource will be cached
	 * forever (for example for class path resources).
	 * <p>To specify a modification check interval for files, use Velocity's
	 * standard "file.resource.loader.modificationCheckInterval" property. By default,
	 * the file timestamp is checked on every access (which is surprisingly fast).
	 * Of course, this just applies when loading resources from the file system.
	 * <p>To enforce the use of SpringResourceLoader, i.e. to not resolve a path
	 * as file system resource in any case, turn off the "preferFileSystemAccess"
	 * flag. See the latter's javadoc for details.
	 * @see #setResourceLoader
	 * @see #setVelocityProperties
	 * @see #setPreferFileSystemAccess
	 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
	 */
	public void setResourceLoaderPath(String resourceLoaderPath) {
		this.resourceLoaderPath = resourceLoaderPath;
	}


	/**
	 * Set whether Velocity should log via Commons Logging, i.e. whether Velocity's
	 * log system should be set to {@link CommonsLogLogChute}. Default is "true".
	 */
	public void setOverrideLogging(boolean overrideLogging) {
		this.overrideLogging = overrideLogging;
	}


	/**
	 * Prepare the VelocityEngine instance and return it.
	 * @return the VelocityEngine instance
	 * @throws IOException if the config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 */
	public VelocityEngine createVelocityEngine() throws IOException, VelocityException {
		VelocityEngine velocityEngine = newVelocityEngine();
		Map<String, Object> props = new HashMap<String, Object>();

		// Load config file if set.
		if (this.configLocation != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Loading Velocity config from [" + this.configLocation + "]");
			}
			mergePropertiesIntoMap(PropertiesLoaderUtils.loadProperties(this.configLocation), props);
		}

		// Merge local properties if set.
		if (!this.velocityProperties.isEmpty()) {
			props.putAll(this.velocityProperties);
		}

		// Set a resource loader path, if required.
		if (this.resourceLoaderPath != null) {
			initVelocityResourceLoader(velocityEngine, this.resourceLoaderPath);
		}

		// Log via Commons Logging?
		if (this.overrideLogging) {
			velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
		}

		// Apply properties to VelocityEngine.
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			velocityEngine.setProperty(entry.getKey(), entry.getValue());
		}

		postProcessVelocityEngine(velocityEngine);

		// Perform actual initialization.
		velocityEngine.init();

		return velocityEngine;
	}

	/**
	 * Return a new VelocityEngine. Subclasses can override this for
	 * custom initialization, or for using a mock object for testing.
	 * <p>Called by {@code createVelocityEngine()}.
	 * @return the VelocityEngine instance
	 * @throws IOException if a config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 * @see #createVelocityEngine()
	 */
	protected VelocityEngine newVelocityEngine() throws IOException, VelocityException {
		return new VelocityEngine();
	}

	/**
	 * Initialize a Velocity resource loader for the given VelocityEngine:
	 * either a standard Velocity FileResourceLoader or a SpringResourceLoader.
	 * <p>Called by {@code createVelocityEngine()}.
	 * @param velocityEngine the VelocityEngine to configure
	 * @param resourceLoaderPath the path to load Velocity resources from
	 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
	 * @see #initSpringResourceLoader
	 * @see #createVelocityEngine()
	 */
	protected void initVelocityResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) throws IOException{
		try {
			StringBuilder resolvedPath = new StringBuilder();
			String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
			for (int i = 0; i < paths.length; i++) {
				String path = paths[i];
				URL resource = ClassUtils.getDefaultClassLoader().getResource(path);//getClass().getResource(path);
				File file = new File(resource.toURI());  // will fail if not resolvable in the file system
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Resource loader path [" + path + "] resolved to file [" + file.getAbsolutePath() + "]");
				}
				resolvedPath.append(file.getAbsolutePath());
				if (i < paths.length - 1) {
					resolvedPath.append(',');
				}
			}
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
			velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, resolvedPath.toString());
		}
		catch (URISyntaxException ex) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Cannot resolve resource loader path [" + resourceLoaderPath +
						"] to [java.io.File]: using SpringResourceLoader", ex);
			}
			throw new IOException(ex.getMessage(), ex);
		}
	}

	/**
	 * To be implemented by subclasses that want to to perform custom
	 * post-processing of the VelocityEngine after this FactoryBean
	 * performed its default configuration (but before VelocityEngine.init).
	 * <p>Called by {@code createVelocityEngine()}.
	 * @param velocityEngine the current VelocityEngine
	 * @throws IOException if a config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 * @see #createVelocityEngine()
	 * @see org.apache.velocity.app.VelocityEngine#init
	 */
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine)
			throws IOException, VelocityException {
	}
	
	/**
	 * Merge the given Properties instance into the given Map,
	 * copying all properties (key-value pairs) over.
	 * <p>Uses {@code Properties.propertyNames()} to even catch
	 * default properties linked into the original Properties instance.
	 * @param props the Properties instance to merge (may be {@code null})
	 * @param map the target Map to merge the properties into
	 */
	@SuppressWarnings("unchecked")
	private static <K, V> void mergePropertiesIntoMap(Properties props, Map<K, V> map) {
		if (map == null) {
			throw new IllegalArgumentException("Map must not be null");
		}
		if (props != null) {
			for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				Object value = props.getProperty(key);
				if (value == null) {
					// Potentially a non-String value...
					value = props.get(key);
				}
				map.put((K) key, (V) value);
			}
		}
	}
}
