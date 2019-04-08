package org.daisy.dotify;

/**
 * Provides common property keys used in the system.
 * @author Joel Håkansson
 */
public enum SystemKeys {
	/**
	 * Defines a key for the system name 
	 */
	SYSTEM_NAME("systemName"),
	/**
	 * Defines a key for the system build
	 */
	SYSTEM_BUILD("systemBuild"),
	/**
	 * Defines a key for the system release 
	 */
	SYSTEM_RELEASE("systemRelease"),
	/**
	 * Defines a key for the input file path 
	 */
	INPUT("input"),
	
	INPUT_FORMAT("inputFormat"),
	/**
	 * Defines a key for the input uri 
	 */
	INPUT_URI("input-uri"),
	/**
	 * Defines a key for the output format 
	 */
	OUTPUT_FORMAT("outputFormat"),
	
	TEMPLATE("template"),
	

	
	PEF_FORMAT("pef"),
	FORMATTED_TEXT_FORMAT("formatted-text"),
	OBFL_FORMAT("obfl"),
	
	/**
	 * Defines a key for the configuration
	 */
	CONFIGURATION("configuration"),
	/**
	 * Defines a key for the temp files.
	 * Corresponding value should be the string "true" or "false" 
	 */
	WRITE_TEMP_FILES("writeTempFiles"),
	/**
	 * Defines a key for keeping temp files on success
	 * Corresponding value should be the string "true" or "false"
	 */
	KEEP_TEMP_FILES_ON_SUCCESS("keepTempFilesOnSuccess"),
	/**
	 * Defines a key for the temp files directory.
	 * Corresponding value should be a string containing a file path
	 */
	TEMP_FILES_DIRECTORY("tempFilesDirectory"),
	
	/**
	 * Defines a key for listing the conversion options.
	 * 
	 */
	LIST_OPTIONS("listOptions");
	private final String key;
	SystemKeys(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
