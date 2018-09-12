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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.factory.Factory;
import org.daisy.dotify.api.factory.FactoryCatalog;
import org.daisy.dotify.api.factory.FactoryProperties;
import org.daisy.dotify.api.table.TableCatalog;
import org.daisy.braille.utils.pef.TextHandler;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.ShortFormResolver;
import org.daisy.streamline.cli.SwitchArgument;
import org.daisy.streamline.cli.SwitchMap;

/**
 * Reads an ASCII file and parses it into a basic PEF file.
 * 
 * In addition to the 64/256 defined code points defined in translation Mode, the
 * characters 0x0a, 0x0d (new row) and 0x0c (new page) may occur in the file. 
 * 
 * @author  Joel Håkansson
 */
class TextParser implements CommandDetails {
	/**
	 * Prefix used for required arguments in the arguments map
	 */
	public static final String ARG_PREFIX = "required-";
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final SwitchMap switches;
	private final ShortFormResolver tableSF;
	private final CommandParser parser;

	public TextParser() {
		reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("input", "path to the input file"));
		reqArgs.add(new Argument("output", "path to the output file"));
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_MODE, "input braille code", getDefinitionList(tableCatalog, tableSF), ""));
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_IDENTIFIER, "the publications unique identifier", "[generated]"));
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_DATE, "set the publication date using the form \"yyyy-MM-dd\"", "[today's date]"));
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_AUTHOR, "the author of the publication", "[undefined]"));
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_TITLE, "the title of the publication", "[undefined]"));
		optionalArgs.add(new OptionalArgument(TextHandler.KEY_LANGUAGE, "set the publications language (as defined by IETF RFC 3066)", "[undefined]"));
		//optionalArgs.add(new OptionalArgument(TextHandler.KEY_DUPLEX, "set the document's duplex property", "true"));
		this.switches = new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('s', "simplex", TextHandler.KEY_DUPLEX, "false", "create single sided PEF-files"))
				.build();
		this.parser = CommandParser.create(this);
	}

	/**
	 * Command line entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		TextParser ui = new TextParser();
		if (args.length<2) {
			ui.parser.displayHelp(System.out);
		} else {
			try {
				Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);

				// remove required argument
				File input = new File(""+p.remove(ARG_PREFIX+0));
				File output = new File(""+p.remove(ARG_PREFIX+1));
				// remap
				try {
					ui.tableSF.expandShortForm(p, TextHandler.KEY_MODE);
				} catch (IllegalArgumentException e) {
					ExitCode.ILLEGAL_ARGUMENT_VALUE.exitSystem(e.getMessage());
				}
				// run
				TextHandler.with(input, output, TableCatalog.newInstance())
					.options(p)
					.parse();
				System.out.println("Validating result...");
				boolean ok = new ValidatorFacade().validate(output, System.out);
				if (!ok) {
					System.out.println("Warning: Validation failed for " + output);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return DotifyCLI.TEXT2PEF;
	}
	
	@Override
	public String getDescription() {
		return "Converts a text braille file into a PEF-file.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}
	
	
	/**
	 * Creates a list of definitions based on the contents of the supplied FactoryCatalog.
	 * @param catalog the catalog to create definitions for
	 * @param resolver 
	 * @return returns a list of definitions
	 */
	List<Definition> getDefinitionList(FactoryCatalog<? extends Factory> catalog, ShortFormResolver resolver) {
		List<Definition> ret = new ArrayList<Definition>();
		for (String key : resolver.getShortForms()) {
			ret.add(new Definition(key, catalog.get(resolver.resolve(key)).getDescription()));
		}
		return ret;
	}

	@Override
	public SwitchMap getSwitches() {
		return switches;
	}

}
