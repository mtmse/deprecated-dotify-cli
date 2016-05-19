package org.daisy.dotify;

import org.daisy.braille.ui.ManifestRetriever;

/**
 * Provides common property values of the system
 * @author Joel Håkansson
 *
 */
public final class SystemProperties {
	private static final ManifestRetriever retriever = new ManifestRetriever(SystemProperties.class);
	/**
	 * Defines the system name
	 */
	public static final String SYSTEM_NAME = getWithDefault(retriever.getManifest().getMainAttributes().getValue("Implementation-Title"), "Dotify");
	/**
	 * Defines the system build
	 */
	public static final String SYSTEM_BUILD = retriever.getManifest().getMainAttributes().getValue("Repository-Revision");
	/**
	 * Defines the system release
	 */
	public static final String SYSTEM_RELEASE = retriever.getManifest().getMainAttributes().getValue("Implementation-Version");

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	private final static String getWithDefault(String val, String def) {
		return (val!=null?val:def);
	}

}
