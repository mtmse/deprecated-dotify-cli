package org.daisy.dotify;

/**
 * Provides common property keys used in the system.
 * @author Joel Håkansson
 */
public interface SystemKeys {
	/**
	 * Defines a key for the system name 
	 */
	public static final String SYSTEM_NAME = "systemName";
	/**
	 * Defines a key for the system build
	 */
	public static final String SYSTEM_BUILD = "systemBuild";
	/**
	 * Defines a key for the system release 
	 */
	public static final String SYSTEM_RELEASE = "systemRelease";
	/**
	 * Defines a key for the input file path 
	 */
	public static final String INPUT = "input";
	
	public static final String INPUT_FORMAT = "inputFormat";
	/**
	 * Defines a key for the input uri 
	 */
	public static final String INPUT_URI = "input-uri";
	/**
	 * Defines a key for the output format 
	 */
	public static final String OUTPUT_FORMAT = "outputFormat";
	
	public static final String TEMPLATE = "template";
	
	public static final String DATE_FORMAT = "dateFormat";
	public static final String DATE = "date";
	public static final String IDENTIFIER = "identifier";
	
	public static final String PEF_FORMAT = "pef";
	public static final String TEXT_FORMAT = "text";
	public static final String OBFL_FORMAT = "obfl";
	
	/**
	 * Defines a key for the configuration
	 */
	public static final String CONFIGURATION = "configuration";
	/**
	 * Defines a key for the temp files.
	 * Corresponding value should be the string "true" or "false" 
	 */
	public static final String WRITE_TEMP_FILES = "writeTempFiles";
	/**
	 * Defines a key for keeping temp files on success
	 * Corresponding value should be the string "true" or "false"
	 */
	public static final String KEEP_TEMP_FILES_ON_SUCCESS = "keepTempFilesOnSuccess";
	/**
	 * Defines a key for the temp files directory.
	 * Corresponding value should be a string containing a file path
	 */
	public static final String TEMP_FILES_DIRECTORY = "tempFilesDirectory";
	
	/**
	 * Defines a key for listing the conversion options.
	 * 
	 */
	public static final String LIST_OPTIONS = "listOptions";
}
