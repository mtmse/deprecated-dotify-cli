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

import org.daisy.braille.utils.pef.PEFBook;
import org.daisy.braille.utils.pef.PEFBookLoader;
import org.daisy.braille.utils.pef.PEFLibrary;
import org.daisy.braille.utils.pef.PEFSearchIndex;
import org.daisy.dotify.cli.pefinfo.Detail;
import org.daisy.dotify.cli.pefinfo.DetailSet;
import org.daisy.dotify.cli.pefinfo.PEFBookInfo;
import org.daisy.dotify.cli.pefinfo.URIDetail;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.SwitchArgument;
import org.daisy.streamline.cli.SwitchMap;
import org.xml.sax.SAXException;

/**
 * Provides a UI for finding PEF-files. Not for public use.
 * This class is a package class. Use DotifyCLI
 * @author Joel Håkansson
 */
class FindPEF implements CommandDetails {
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final SwitchMap switches;
	private final CommandParser parser;
	
	private static final String FOLDER_KEY = "folder";
	private static final String RECURSIVE_KEY = "recursive";
	private static final String INTERACTIVE_KEY = "interactive";

	public FindPEF() {
		reqArgs = new ArrayList<Argument>();
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(FOLDER_KEY, "Folder path", (new File("")).getAbsolutePath()));
		this.switches = new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('r', RECURSIVE_KEY, "true", "Include subfolders in the scan."))
				.addSwitch(new SwitchArgument('i', INTERACTIVE_KEY, "true", "Starts an interactive shell for repeated queries."))
				.build();
		this.parser = CommandParser.create(this);
	}

	public static void main(String[] args) throws FileNotFoundException {
		FindPEF ui = new FindPEF();
		if (args.length<1) {
			System.out.println("Expected at least one more argument.");
			System.out.println();
			ui.parser.displayHelp(System.out);
			ExitCode.MISSING_ARGUMENT.exitSystem();
		}
		CommandParserResult pr =  ui.parser.parse(args);
		//Map<String, String> p =pr.toMap(ARG_PREFIX);
		String dirStr = pr.getOptional().get(FOLDER_KEY);
		File dir;
		if (dirStr!=null) {
			dir = new File(dirStr);
		} else {
			dir = new File("").getAbsoluteFile();
		}

		boolean recursive = Boolean.parseBoolean(pr.getOptional().get(RECURSIVE_KEY));
		System.out.println("Scanning books. Wait a while...");
		PEFSearchIndex search = new PEFSearchIndex();
		PEFBookLoader loader = new PEFBookLoader();
		for (File f : PEFLibrary.listFiles(dir, recursive)) {
			try {
				PEFBook p = loader.load(f);
				if (p!=null) {
					search.add(p);
				}
			} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
				//e.printStackTrace();
			}
    		System.out.print(".");
		}
		System.out.println();
		Collection<Detail> c = new ArrayList<>();
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
		return DotifyCLI.FIND;
	}
	
	@Override
	public String getDescription() {
		return "Finds PEF-files.";
	}

	@Override
	public SwitchMap getSwitches() {
		return switches;
	}

}
