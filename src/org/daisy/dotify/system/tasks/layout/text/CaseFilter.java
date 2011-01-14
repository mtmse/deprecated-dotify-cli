package org.daisy.dotify.system.tasks.layout.text;


/**
 * 
 * Implements StringFilter to return upper or lower case characters only.
 * 
 * @author  Joel Hakansson, TPB
 * @version 4 maj 2009
 * @since 1.0
 */
public class CaseFilter implements StringFilter {

	/**
	 * Filter modes
	 */
	public static enum Mode {
		/**
		 * Lower case mode
		 */
			LOWER_CASE,
		/**
		 * Upper case mode
		 */
			UPPER_CASE};
	private Mode mode;
	
	/**
	 * Create a new CaseFilter
	 * @param mode filter mode
	 */
	public CaseFilter(Mode mode) {
		this.mode = mode;
	}

	public String filter(String expr) {
		switch (mode) {
			case UPPER_CASE:
				return expr.toUpperCase();
			case LOWER_CASE:
				return expr.toLowerCase();
			default:
				return null;
		}
	}

}
