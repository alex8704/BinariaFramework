package co.com.binariasystems.fmw.util.messagebundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;

public abstract class MessageBundleManager {
	private String resourcePath;
    private ResourceBundle initialBundle;
    private Locale initialLocale;
    private boolean returnKeyWhenNotFound;
    private Class loaderClass;
    private final ConcurrentMap<Locale, ResourceBundle> bundlesContext = new ConcurrentHashMap<Locale, ResourceBundle>();
    private final ConcurrentMap<Locale, Locale> localeMapping = new ConcurrentHashMap<Locale, Locale>();
    private static final ConcurrentMap<String, MessageBundleManager> managersContext = new ConcurrentHashMap<String, MessageBundleManager>();
    
    private MessageBundleManager(){}

    public static MessageBundleManager forPath(String resourcePath) {
        return forPath(resourcePath, false);
    }

    public static MessageBundleManager forPath(String resourcePath, Class loaderClass) {
        return forPath(resourcePath, false, loaderClass);
    }

    public static MessageBundleManager forPath(String resourcePath, boolean returnKeyWhenNotFound) {
        return forPath(resourcePath, returnKeyWhenNotFound, null);
    }

    public static MessageBundleManager forPath(String resourcePath, boolean returnKeyWhenNotFound, Class loaderClass) {
        return forPath(resourcePath, returnKeyWhenNotFound, loaderClass, Locale.getDefault());
    }

    public static MessageBundleManager forPath(String resourcePath, boolean returnKeyWhenNotFound, Class loaderClass, Locale locale) {
        if (StringUtils.isEmpty(resourcePath)) {
            return null;
        }
        MessageBundleManager messageManager = managersContext.get(resourcePath);
        if (messageManager == null) {
        	synchronized (MessageBundleManager.class) {
        		messageManager = new MessageBundleManager() {};
                messageManager.initialLocale = (locale != null) ? locale : Locale.getDefault();
                messageManager.resourcePath = resourcePath;
                messageManager.returnKeyWhenNotFound = returnKeyWhenNotFound;
                messageManager.init(loaderClass != null ? loaderClass : MessageBundleManager.class);
                managersContext.putIfAbsent(resourcePath, messageManager);
			}
        }
        return messageManager;
    }

    public String getString(String key) {
        return getString(key, initialLocale);
    }

    public String getString(String key, Locale locale) {
        if (key == null) {
            return "";
        }

        ResourceBundle bundle = obtainBundle(locale != null ? locale : initialLocale);
        String resp = null;
        if (bundle != null) {
            try {
                resp = bundle.getString(key);
            } catch (Exception ex) {
            }
        }
        return StringUtils.isNotEmpty(resp) ? resp : (returnKeyWhenNotFound ? key : "");
    }

    private void init(Class loaderClass) {
        this.loaderClass = loaderClass;
        try {
            initialBundle = ResourceBundle.getBundle(resourcePath, initialLocale, loaderClass.getClassLoader());
            localeMapping.putIfAbsent(initialLocale, initialBundle.getLocale());
            this.initialLocale = initialBundle.getLocale();
            bundlesContext.putIfAbsent(initialLocale, initialBundle);
        } catch (NullPointerException | MissingResourceException | IllegalArgumentException ex) {
        	Throwable cause = FMWExceptionUtils.prettyMessageException(ex);
            throw new FMWUncheckedException("Cannot create message manager: "+cause.getMessage(), cause);
        }
    }

    private ResourceBundle obtainBundle(Locale locale) {
        ResourceBundle targetResource = initialBundle;
        Locale targetLocale = localeMapping.get(locale);
        if (targetLocale == null) {
            targetLocale = locale;
            try {
                targetResource = ResourceBundle.getBundle(resourcePath, targetLocale, loaderClass.getClassLoader());
                bundlesContext.putIfAbsent(targetResource.getLocale(), targetResource);
            } catch (Exception ex) {
            }
            localeMapping.putIfAbsent(locale, targetResource.getLocale());
        } else {
            targetResource = bundlesContext.get(targetLocale);
        }
        return targetResource;
    }
    public Set<String> getKeys(){
		return initialBundle == null ? null : initialBundle.keySet();
	}
}
