/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.dotify.cli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.daisy.braille.utils.api.factory.FactoryProperties;

/**
 * Provides a command line input helper for setting arguments interactively and 
 * storing them in the users preferences.
 * @author Joel Håkansson
 */
public class InputHelper {
	private Preferences pr;
	private LineNumberReader ln;
	
	/**
	 * Creates a new InputHelper for the specified class. The package name
	 * for the specified class will be used to locate an appropriate storage
	 * location in the user preferences.
	 * @param c the class to create a InputHelper for
	 */
	public InputHelper(@SuppressWarnings("rawtypes") Class c) {
		pr = Preferences.userNodeForPackage(c);
		ln =  new LineNumberReader(new InputStreamReader(System.in));
	}
	
	/**
	 * Creates a new InputHelper with the default storage location (determined by
	 * the calling class's package name)
	 */
	public InputHelper() {
		// Determining the calling class.
		// ca[0] is the anonymous security manager
		// ca[1] is this class
		// ca[2] is the calling class
		this((new SecurityManager() { @SuppressWarnings("rawtypes")
                @Override
			   public Class[] getClassContext() { return super.getClassContext(); }}.getClassContext())[2]
		);
	}

	/**
	 * Selects the value for a key.
	 * @param key the key to select a value for
	 * @param select the list of available values
	 * @param name a display name for the key
	 * @param verify if true, and no value is found, lets user select a value
	 * @return returns the value for the key.
	 */
	public String select(String key, String[] select, String name, boolean verify) {
		String value = getKey(key);
		if (value!=null) {
			// check value
			boolean ok = false;
			for (String s : select) {
				if (value.equals(s)) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				// reset value
				value = null;
			}
		}
		if (value==null || verify) {
			// ask user
			System.out.println("Choose " + name + ": ");
			int i = 1;
			for (String s : select) {
				System.out.print(i + ". " + s);
				if (value!=null && s.equals(value)) {
					System.out.print(" (current value, hit enter to keep this value)");
				}
				System.out.println();
				i++;
			}
			int sel;
			try {
				sel = getInput(value!=null)-1;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (sel==-1 && value!=null) {
				return value;
			} else if (sel<0) {
				throw new RuntimeException("Exception");
			}
			value = select[sel];
			pr.put(key, value);
		}
		return value;
	}
	
	/**
	 * Selects the value for a key.
	 * @param key the key to get a value for
	 * @param select the list of available values
	 * @param name the display name for the key
	 * @param verify if true, and no value is found, lets user select a value
	 * @return returns the value for the key.
	 */
	public String select(String key, List<FactoryProperties> select, String name, boolean verify) {
		String value = getKey(key);
		if (value!=null) {
			// check value
			boolean ok = false;
			for (FactoryProperties s : select) {
				if (value.equals(s.getIdentifier())) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				// reset value
				value = null;
			}
		}
		if (value==null || verify) {
			// ask user
			System.out.println("Choose " + name + ": ");
			int i = 1;
			for (FactoryProperties s : select) {
				System.out.print(i + ". " + s.getDisplayName());
				if (value!=null && s.getIdentifier().equals(value)) {
					System.out.print(" (current value, hit enter to keep this value)");
				}
				System.out.println();
				i++;
			}
			int sel;
			try {
				sel = getInput(value!=null)-1;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (sel==-1 && value!=null) {
				return value;
			} else if (sel<0) {
				throw new RuntimeException("Exception");
			}
			value = select.get(sel).getIdentifier();
			pr.put(key, value);
		}
		return value;
	}
	
	/**
	 * Gets the value for a key.
	 * @param key the key to get a value for
	 * @return returns the value for the key
	 */
	public String getKey(String key) {
		return pr.get(key, null);
	}
	
	/**
	 * Gets a integer from user. The assumption is that this value should be a non-zero
	 * positive integer, that is the user selects a value from a list starting with one.
	 * If the input is an empty string, 0 is returned. If an IO error occurs, -1 is returned.
	 * @param allowEnter set to true to return 0 if input is empty, false to retry if input is empty
	 * @return returns the integer value suppled by the user on the command line.
	 * @throws IOException if IO fails.
	 */
	public int getInput(boolean allowEnter) throws IOException {
		while (true) {
			System.out.print("Input: ");
			String line = ln.readLine();
			try {
				if (line.equals("") && allowEnter) {
					return 0;
				} else {
					return Integer.parseInt(line);
				}
			} catch (NumberFormatException e) {
				System.out.println("Not a number: '" + line + "'");
			}
		}
	}
	
	public double getDouble(String msg, String key, boolean verify) throws IOException {
		Double ret = null;
		if (getKey(key)!=null) {
			// check value
			try {
				ret = Double.valueOf(getKey(key));
			} catch (NumberFormatException e) { }
		}
		if (ret==null || verify) {
			// ask user
			while (true) {
				System.out.print(msg+ (ret!=null?" (current value "+ ret + ")":"") + " : ");
				String line = ln.readLine();
				if (ret!=null && "".equals(line)) {
					pr.put(key, ret+"");
					break;
				} else {
					try {
						ret = Double.parseDouble(line);
						pr.put(key, ret+"");
						break;
					} catch (NumberFormatException e) {
						System.out.println("Not a number: '" + line + "'");
					}
				}
			}
		}
		return ret;
	}
	
	public boolean getBoolean(String msg, String key, boolean verify) throws IOException {
		Boolean ret = null;
		if (getKey(key)!=null) {
			ret = Boolean.valueOf(getKey(key));
		}
		if (ret==null || verify) {
			// ask user
			while (true) {
				System.out.print(msg+" (y/n): ");
				String line = ln.readLine();
				if (ret!=null && "".equals(line)) {
					pr.put(key, ret+"");
					break;
				} else {
					if ("y".equalsIgnoreCase(line)) {
						ret = true;
						pr.put(key, ret+"");
						break;
					} else if ("n".equalsIgnoreCase(line)) {
						ret = false;
						pr.put(key, ret+"");
						break;
					}
					System.out.println("Not a valid input: '" + line + "'");
				}
			}
		}
		return ret;
	}
	
	/**
	 * Clears the settings associated with this object from storage.
	 * @throws BackingStoreException if this operation cannot be completed 
	 * due to a failure in the backing store, or inability to communicate with it.
     * @throws IllegalStateException if the associated node (or an ancestor) has 
     * 		been removed with the removeNode() method.
	 */
	public void clearSettings() throws BackingStoreException {
		pr.clear();
		pr.flush();
	}

}
