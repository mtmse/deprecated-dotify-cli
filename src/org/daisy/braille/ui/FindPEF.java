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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.braille.pef.PEFBook;
import org.daisy.braille.pef.PEFBookLoader;
import org.daisy.braille.pef.PEFLibrary;
import org.daisy.braille.pef.PEFSearchIndex;
import org.daisy.braille.ui.pefinfo.Detail;
import org.daisy.braille.ui.pefinfo.DetailSet;
import org.daisy.braille.ui.pefinfo.PEFBookInfo;
import org.daisy.braille.ui.pefinfo.URIDetail;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.CommandParserResult;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.SwitchArgument;
import org.xml.sax.SAXException;

/**
 * Provides a UI for finding PEF-files. Not for public use.
 * This class is a package class. Use BasicUI
 * @author Joel Håkansson
 */
class FindPEF extends AbstractUI {
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	
	private final static String FOLDER_KEY = "folder";
	private final static String RECURSIVE_KEY = "recursive";
	private final static String INTERACTIVE_KEY = "interactive";

	public FindPEF() {
		reqArgs = new ArrayList<Argument>();
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(FOLDER_KEY, "Folder path", (new File("")).getAbsolutePath()));
		parser.addSwitch(new SwitchArgument('r', RECURSIVE_KEY, "true", "Include subfolders in the scan."));
		parser.addSwitch(new SwitchArgument('i', INTERACTIVE_KEY, "true", "Starts an interactive for repeated queries."));
	}

	public static void main(String[] args) throws FileNotFoundException {
		FindPEF ui = new FindPEF();
		if (args.length<1) {
			System.out.println("Expected at least one more argument.");
			System.out.println();
			ui.displayHelp(System.out);
			System.exit(-ExitCode.MISSING_ARGUMENT.ordinal());
		}
		CommandParserResult pr =  ui.parser.parse(args);
		//Map<String, String> p =pr.toMap(ARG_PREFIX);
		String dirStr = pr.getOptional().get(FOLDER_KEY);
		File dir;
		if (dirStr!=null) {
			dir = new File(dirStr);
		} else {
			dir = new File("");
		}

		boolean recursive = Boolean.parseBoolean(pr.getOptional().get(RECURSIVE_KEY));
		System.out.println("Scanning books. Wait a while...");
		PEFSearchIndex search = new PEFSearchIndex();
		PEFBookLoader loader = new PEFBookLoader();
		for (File f : PEFLibrary.listFiles(dir, recursive)) {
			try {
				search.add(loader.load(f));
			} catch (XPathExpressionException e) {
				//e.printStackTrace();
			} catch (ParserConfigurationException e) {
				//e.printStackTrace();
			} catch (SAXException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		System.out.print(".");
		}
		System.out.println();
		Collection<Detail> c = new ArrayList<Detail>();
		c.add(new URIDetail());
		c.addAll(DetailSet.DEFAULT.newDetailSet());
		PEFBookInfo pbi = new PEFBookInfo(c);
		if (!pr.getRequired().isEmpty()) {
			printResult(pbi, search.containsAll(pr.getRequired()));
		}
		if (pr.getRequired().isEmpty() || Boolean.parseBoolean(pr.getOptional().get(INTERACTIVE_KEY))) {
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
			String line;
			try {
				System.out.println("> ");
				while ((line = lnr.readLine())!=null) {
					printResult(pbi, search.containsAll(line.split("\\s+")));
					System.out.println("> ");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printResult(PEFBookInfo pbi, Set<PEFBook> result) {
		for (PEFBook p : result) {
			pbi.print(p, System.out);
		}
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

	@Override
	public String getName() {
		return "find";//BasicUI.find;
	}
	
	@Override
	public String getDescription() {
		return "Finds PEF-files.";
	}

}
