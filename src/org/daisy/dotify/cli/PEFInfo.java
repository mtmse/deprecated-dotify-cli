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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.braille.utils.pef.PEFBook;
import org.daisy.dotify.cli.pefinfo.DetailSet;
import org.daisy.dotify.cli.pefinfo.PEFBookInfo;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.SwitchArgument;
import org.daisy.streamline.cli.SwitchMap;
import org.xml.sax.SAXException;

/**
 * Provides a UI for generating PEF-files. Not for public use.
 * This class is a package class. Use DotifyCLI
 * @author Joel Håkansson
 */
class PEFInfo implements CommandDetails {
	/**
	 * Prefix used for required arguments in the arguments map
	 */
	public static final String ARG_PREFIX = "required-";
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final SwitchMap switches;
	private final CommandParser parser;

	public PEFInfo() {
		reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("path_to_file", "Path to the pef file"));
		optionalArgs = new ArrayList<OptionalArgument>();
		this.switches = new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('m', "meta", "metadata", "full", "Prints all metadata."))
				.build();
		this.parser = CommandParser.create(this);
	}
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		PEFInfo ui = new PEFInfo();
		if (args.length<1) {
			System.out.println("Expected at least one more argument.");
			System.out.println();
			ui.parser.displayHelp(System.out);
			ExitCode.MISSING_ARGUMENT.exitSystem();
		}
		Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);
		// remove required argument
		File input = new File(""+p.remove(ARG_PREFIX+0));
		
		System.out.println("Reading " + input);
		PEFBook book = PEFBook.load(input.toURI());
		PrintStream ps = System.out;
		ps.println();
		
		String m = p.get("metadata");
		boolean meta = m!=null && m.equals("full");
		
		PEFBookInfo pbi;
		if (meta) {
			pbi = new PEFBookInfo(DetailSet.FULL);
		} else {
			pbi = new PEFBookInfo();
		}
		
		pbi.print(book, ps);
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
		return DotifyCLI.INSPECT;
	}
	
	@Override
	public String getDescription() {
		return "Displays meta data and summary details about a PEF-file.";
	}

	@Override
	public SwitchMap getSwitches() {
		return switches;
	}

}
