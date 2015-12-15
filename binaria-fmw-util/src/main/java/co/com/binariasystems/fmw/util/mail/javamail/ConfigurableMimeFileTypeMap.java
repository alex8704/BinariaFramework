package co.com.binariasystems.fmw.util.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import co.com.binariasystems.fmw.util.resources.resources;

public class ConfigurableMimeFileTypeMap extends FileTypeMap {

	/**
	 * The {@code Resource} to load the mapping file from.
	 * 
	 */
	private URL mappingLocation = resources.class.getResource(resources.propertiesPath()+"mime.types");

	/**
	 * Used to configure additional mappings.
	 */
	private String[] mappings;

	/**
	 * The delegate FileTypeMap, compiled from the mappings in the mapping file
	 * and the entries in the {@code mappings} property.
	 */
	private FileTypeMap fileTypeMap;


	/**
	 * Specify the {@code Resource} from which mappings are loaded.
	 * <p>Needs to follow the {@code mime.types} file format, as specified
	 * by the Java Activation Framework, containing lines such as:<br>
	 * {@code text/html  html htm HTML HTM}
	 */
	public void setMappingLocation(URL mappingLocation) {
		this.mappingLocation = mappingLocation;
	}

	/**
	 * Specify additional MIME type mappings as lines that follow the
	 * {@code mime.types} file format, as specified by the
	 * Java Activation Framework, for example:<br>
	 * {@code text/html  html htm HTML HTM}
	 */
	public void setMappings(String[] mappings) {
		this.mappings = mappings;
	}

	public void afterPropertiesSet() {
		getFileTypeMap();
	}

	/**
	 * Return the delegate FileTypeMap, compiled from the mappings in the mapping file
	 * and the entries in the {@code mappings} property.
	 * @see #setMappingLocation
	 * @see #setMappings
	 * @see #createFileTypeMap
	 */
	protected final FileTypeMap getFileTypeMap() {
		if (this.fileTypeMap == null) {
			try {
				this.fileTypeMap = createFileTypeMap(this.mappingLocation, this.mappings);
			}
			catch (IOException ex) {
				throw new IllegalStateException(
						"Could not load specified MIME type mapping file: " + this.mappingLocation, ex);
			}
		}
		return this.fileTypeMap;
	}

	/**
	 * Compile a {@link FileTypeMap} from the mappings in the given mapping file
	 * and the given mapping entries.
	 * <p>The default implementation creates an Activation Framework {@link MimetypesFileTypeMap},
	 * passing in an InputStream from the mapping resource (if any) and registering
	 * the mapping lines programmatically.
	 * @param mappingLocation a {@code mime.types} mapping resource (can be {@code null})
	 * @param mappings MIME type mapping lines (can be {@code null})
	 * @return the compiled FileTypeMap
	 * @throws IOException if resource access failed
	 * @see javax.activation.MimetypesFileTypeMap#MimetypesFileTypeMap(java.io.InputStream)
	 * @see javax.activation.MimetypesFileTypeMap#addMimeTypes(String)
	 */
	protected FileTypeMap createFileTypeMap(URL mappingLocation, String[] mappings) throws IOException {
		MimetypesFileTypeMap fileTypeMap = null;
		if (mappingLocation != null) {
			InputStream is = mappingLocation.openStream();
			try {
				fileTypeMap = new MimetypesFileTypeMap(is);
			}
			finally {
				is.close();
			}
		}
		else {
			fileTypeMap = new MimetypesFileTypeMap();
		}
		if (mappings != null) {
			for (String mapping : mappings) {
				fileTypeMap.addMimeTypes(mapping);
			}
		}
		return fileTypeMap;
	}


	/**
	 * Delegates to the underlying FileTypeMap.
	 * @see #getFileTypeMap()
	 */
	@Override
	public String getContentType(File file) {
		return getFileTypeMap().getContentType(file);
	}

	/**
	 * Delegates to the underlying FileTypeMap.
	 * @see #getFileTypeMap()
	 */
	@Override
	public String getContentType(String fileName) {
		return getFileTypeMap().getContentType(fileName);
	}

}
