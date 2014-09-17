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
package org.daisy.braille.ui;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.daisy.braille.tools.FileTools;
import org.daisy.cli.AbstractUI;

/**
 * Provides a basic command line UI for core functionality in
 * org.daisy.braille.
 * @author Joel Håkansson
 */
public class BasicUI extends AbstractUI {
	public final static String emboss = "emboss";
	public final static String text2pef = "text2pef";
	public final static String pef2text = "pef2text";
	public final static String validate = "validate";
	public final static String split = "split";
	public final static String merge = "merge";
	public final static String generate = "generate";
	public final static String list = "list";
	enum Mode {EMBOSS, TEXT2PEF, PEF2TEXT, VALIDATE, SPLIT, MERGE, GENERATE, LIST};

	private final String[] args;
	private final Mode m;
	private final Logger logger;
	
	/**
	 * Creates a new Basic UI
	 * @param args the application arguments
	 */
	public BasicUI(String[] args) {
		logger = Logger.getLogger(BasicUI.class.getCanonicalName());
		logger.fine(System.getProperties().toString());
		this.args = args;
		if (args.length<1) {
			System.out.println("Expected at least one argument.");
			System.out.println();
			displayHelp(System.out);
			System.exit(-ExitCode.MISSING_ARGUMENT.ordinal());
		}
		Mode m2;
		try {
			m2 = Mode.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			m2 = null;
			System.out.println("Unknown argument '" + args[0] + "'");
			displayHelp(System.out);
			System.exit(-ExitCode.UNKNOWN_ARGUMENT.ordinal());
		}
		m = m2;
	}
	
	/**
	 * Sets the context class loader to an URLClassLoader containing the jars found in
	 * the specified path. 
	 * @param dir the directory to search for jar-files.
	 */
	public void setPluginsDir(File dir) {
		// list jars and convert to URL's
		URL[] jars = FileTools.toURL(FileTools.listFiles(dir, ".jar"));
		for (URL u : jars) {
			logger.info("Found jar " + u);
		}
		// set context class loader
		if (jars.length>0) {
			Thread.currentThread().setContextClassLoader(new URLClassLoader(jars));
		}
	}

	/**
	 * Runs the application.
	 * @throws Exception if something bad happens
	 */
	public void run() throws Exception {
		setPluginsDir(new File("plugins"));
		switch (m) {
			case EMBOSS:
				logger.fine("Starting embossing application...");
				EmbossPEF.main(getArgsSubList(1));
				break;
			case VALIDATE:
				logger.fine("Starting validating application...");
				ValidatePEF.main(getArgsSubList(1));
				break;
			case PEF2TEXT:
				logger.fine("Starting pef to text application...");
				PEFParser.main(getArgsSubList(1));
				break;
			case TEXT2PEF:
				logger.fine("Starting text to pef application...");
				TextParser.main(getArgsSubList(1));
				break;
			case SPLIT:
				logger.fine("Starting file splitter application...");
				SplitPEF.main(getArgsSubList(1));
				break;
			case MERGE:
				logger.fine("Starting file merger application...");
				MergePEF.main(getArgsSubList(1));
				break;
			case GENERATE:
				logger.fine("Starting generator application...");
				GeneratePEF.main(getArgsSubList(1));
				break;
			case LIST:
				logger.fine("Starting list application...");
				ListStuff.main(getArgsSubList(1));
				break;
			default:
				throw new RuntimeException("Coding error");
		}
	}
	
	/**
	 * Command line entry point
	 * @param args the application arguments
	 */
	public static void main(String[] args) throws Exception {
		BasicUI ui = new BasicUI(args);
		ui.run();
	}

	private String[] getArgsSubList(int offset) {
		int len = args.length-offset;
		if (len==0) {
			// no args left
			return new String[]{};
		} else if (len<0) {
			// too few args
			throw new IllegalArgumentException("New array has a negative size");
		}
		String[] args2 = new String[len];
		System.arraycopy(args, offset, args2, 0, len);
		return args2;
	}

	@Override
	public String getName() {
		return "BasicUI";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		ArrayList<Argument> ret = new ArrayList<Argument>();
		ArrayList<Definition> values = new ArrayList<Definition>();
		values.add(new Definition(emboss, "emboss a PEF-file"));
		values.add(new Definition(text2pef, "convert text to pef"));
		values.add(new Definition(pef2text, "convert pef to text"));
		values.add(new Definition(validate, "validate a PEF-file"));
		values.add(new Definition(split, "split a PEF-file into several single volume files"));
		values.add(new Definition(merge, "merge several single volume PEF-files into one"));
		values.add(new Definition(generate, "generate a random PEF-file for testing"));
		values.add(new Definition(list, "lists stuff"));
		ret.add(new Argument("app_to_run", "the application to run", values));
		return ret;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return null;
	}

}
