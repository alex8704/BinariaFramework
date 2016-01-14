package co.com.binariasystems.fmw.util.messagebundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;

public abstract class PropertiesManager {
	private String resourcePath;
	private boolean returnKeyWhenNotFound;
	private Properties props;
	private static final ConcurrentMap<String, PropertiesManager> managersContext = new ConcurrentHashMap<String, PropertiesManager>();

	private PropertiesManager(){}	
	
	public static PropertiesManager forPath(String resourcePath) {
		return forPath(resourcePath, true);
	}

	public static PropertiesManager forPath(String resourcePath, Class loaderClass) {
		return forPath(resourcePath, true, loaderClass);
	}

	public static PropertiesManager forPath(String resourcePath, boolean returnKeyWhenNotFound) {
		return forPath(resourcePath, returnKeyWhenNotFound, null);
	}

	public static PropertiesManager forPath(String resourcePath, boolean returnKeyWhenNotFound, Class loaderClass) {
		if (StringUtils.isEmpty(resourcePath))
			return null;
		String resourceFormatPath = toResourcePathFormat(resourcePath);
		PropertiesManager resp = managersContext.get(resourceFormatPath);
		if (resp == null) {
			synchronized (PropertiesManager.class) {
				resp = new PropertiesManager() {};
				resp.resourcePath = resourceFormatPath;
				resp.returnKeyWhenNotFound = returnKeyWhenNotFound;
				resp.init(loaderClass != null ? loaderClass : PropertiesManager.class);
				managersContext.putIfAbsent(resourceFormatPath, resp);
			}
		}
		return resp;
	}
	
	private static String toResourcePathFormat(String resource){
		String extension = resource.endsWith(".xml") ? ".xml" : ".properties";
		int extensionOffset = (resource.lastIndexOf(extension) >= 0) ? resource.lastIndexOf(extension) : resource.length();
		return (resource.startsWith("/") ? "" : "/")+resource.substring(0, extensionOffset).replace(".", "/")+extension;
	}

	private void init(Class loaderClass) {
		InputStream is = null;
		try {
			is = loaderClass.getResourceAsStream(resourcePath);
			props = new Properties();
			if (resourcePath.endsWith(".xml"))
				props.loadFromXML(is);
			else
				props.load(is);
		} catch (IOException | NullPointerException ex) {
			throw new FMWUncheckedException("Cannot create properties manager for path '"+resourcePath+"' using Loader Class "+loaderClass.getName(), ex);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
	}

	public String getString(String key) {
		if (key == null)
			return "";
		String resp = null;
		if (props != null)
			resp = props.getProperty(key);
		return (returnKeyWhenNotFound && StringUtils.isEmpty(resp)) ? key : StringUtils.defaultString(resp);
	}
	
	public Set<Object> getKeys(){
		return props == null ? null : props.keySet();
	}
}
