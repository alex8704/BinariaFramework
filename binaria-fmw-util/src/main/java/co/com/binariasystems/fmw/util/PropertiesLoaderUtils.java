package co.com.binariasystems.fmw.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesLoaderUtils {
	private static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * Load properties from the given resource (in ISO-8859-1 encoding).
	 * @param resource the resource to load from
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 * @see #fillProperties(java.util.Properties, URL)
	 */
	public static Properties loadProperties(URL resource) throws IOException {
		Properties props = new Properties();
		fillProperties(props, resource);
		return props;
	}

	/**
	 * Fill the given properties from the given resource (in ISO-8859-1 encoding).
	 * @param props the Properties instance to fill
	 * @param resource the resource to load from
	 * @throws IOException if loading failed
	 */
	public static void fillProperties(Properties props, URL resource) throws IOException {
		InputStream is = resource.openStream();
		try {
			String filename = resource.getFile();
			if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
				props.loadFromXML(is);
			}
			else {
				props.load(is);
			}
		}
		finally {
			is.close();
		}
	}

	/**
	 * Load all properties from the specified class path resource
	 * (in ISO-8859-1 encoding), using the default class loader.
	 * <p>Merges properties if more than one resource of the same name
	 * found in the class path.
	 * @param resourceName the name of the class path resource
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 */
	public static Properties loadAllProperties(String resourceName) throws IOException {
		return loadAllProperties(resourceName, null);
	}

	/**
	 * Load all properties from the specified class path resource
	 * (in ISO-8859-1 encoding), using the given class loader.
	 * <p>Merges properties if more than one resource of the same name
	 * found in the class path.
	 * @param resourceName the name of the class path resource
	 * @param classLoader the ClassLoader to use for loading
	 * (or {@code null} to use the default class loader)
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 */
	public static Properties loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException {
		Assert.notNull(resourceName, "Resource name must not be null");
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = ClassUtils.getDefaultClassLoader();
		}
		Enumeration<URL> urls = (classLoaderToUse != null ? classLoaderToUse.getResources(resourceName) :
				ClassLoader.getSystemResources(resourceName));
		Properties props = new Properties();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			try {
				if (resourceName != null && resourceName.endsWith(XML_FILE_EXTENSION)) {
					props.loadFromXML(is);
				}
				else {
					props.load(is);
				}
			}
			finally {
				is.close();
			}
		}
		return props;
	}
}
