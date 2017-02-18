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

import org.daisy.braille.api.factory.Factory;
import org.daisy.braille.api.factory.FactoryCatalog;
import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.consumer.table.TableCatalog;
import org.daisy.braille.consumer.validator.ValidatorFactory;
import org.daisy.braille.pef.PEFValidatorFacade;
import org.daisy.braille.pef.TextConverterFacade;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.Definition;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.ShortFormResolver;
import org.daisy.cli.SwitchArgument;

/**
 * Reads an ASCII file and parses it into a basic PEF file.
 * 
 * In addition to the 64/256 defined code points defined in translation Mode, the
 * characters 0x0a, 0x0d (new row) and 0x0c (new page) may occur in the file. 
 * 
 * @author  Joel Håkansson
 * @version 28 aug 2008
 */
class TextParser extends AbstractUI {
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final ShortFormResolver tableSF;

	public TextParser() {
		reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("input", "path to the input file"));
		reqArgs.add(new Argument("output", "path to the output file"));
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_MODE, "input braille code", getDefinitionList(tableCatalog, tableSF), ""));
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_IDENTIFIER, "the publications unique identifier", "[generated]"));
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_DATE, "set the publication date using the form \"yyyy-MM-dd\"", "[today's date]"));
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_AUTHOR, "the author of the publication", "[undefined]"));
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_TITLE, "the title of the publication", "[undefined]"));
		optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_LANGUAGE, "set the publications language (as defined by IETF RFC 3066)", "[undefined]"));
		//optionalArgs.add(new OptionalArgument(TextConverterFacade.KEY_DUPLEX, "set the document's duplex property", "true"));
		parser.addSwitch(new SwitchArgument('s', "simplex", TextConverterFacade.KEY_DUPLEX, "false", "create single sided PEF-files"));
	}

	/**
	 * Command line entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		TextParser ui = new TextParser();
		if (args.length<2) {
			ui.displayHelp(System.out);
		} else {
			try {
				Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);

				// remove required argument
				File input = new File(""+p.remove(ARG_PREFIX+0));
				File output = new File(""+p.remove(ARG_PREFIX+1));
				// remap
				ui.expandShortForm(p, TextConverterFacade.KEY_MODE, ui.tableSF);
				// run
				new TextConverterFacade(TableCatalog.newInstance()).parseTextFile(input, output, p);
				System.out.println("Validating result...");
				boolean ok = new PEFValidatorFacade(ValidatorFactory.newInstance()).validate(output, System.out);
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

}
