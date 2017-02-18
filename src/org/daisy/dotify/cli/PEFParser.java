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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.daisy.braille.api.embosser.StandardLineBreaks;
import org.daisy.braille.api.factory.Factory;
import org.daisy.braille.api.factory.FactoryCatalog;
import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.consumer.embosser.EmbosserCatalog;
import org.daisy.braille.consumer.table.TableCatalog;
import org.daisy.braille.consumer.validator.ValidatorFactory;
import org.daisy.braille.pef.PEFConverterFacade;
import org.daisy.braille.pef.PEFValidatorFacade;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.Definition;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.ShortFormResolver;

/**
 * Reads a PEF-file and outputs a text file.
 * 
 * @author  Joel Håkansson
 * @version 2 jul 2008
 */
class PEFParser extends AbstractUI {
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	//private final ShortFormResolver embosserSF;
	private final ShortFormResolver tableSF;
	
	public PEFParser() {
		reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("input", "path to the input file"));
		reqArgs.add(new Argument("output", "path to the output file"));
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_RANGE, "output a range of pages", "1-"));
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_TABLE, "braille code table", getDefinitionList(tableCatalog, tableSF), ""));
		/*
		EmbosserCatalog embosserCatalog = EmbosserCatalog.newInstance();
		embosserSF = new ShortFormResolver(embosserCatalog.list());
		System.out.println(embosserSF.getShortForm(org_daisy.GenericEmbosserProvider.class.getCanonicalName()+".EmbosserType.NONE"));
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_EMBOSSER, "target embosser", getDefinitionList(embosserCatalog, embosserSF), embosserSF.getShortForm(org_daisy.GenericEmbosserProvider.class.getCanonicalName()+".EmbosserType.NONE")));
		*/
		List<Definition> lbDefs = new ArrayList<Definition>();
		lbDefs.add(new Definition(StandardLineBreaks.Type.DEFAULT.toString(), "System default line breaks"));
		lbDefs.add(new Definition(StandardLineBreaks.Type.DOS.toString(), "DOS/Windows line breaks"));
		lbDefs.add(new Definition(StandardLineBreaks.Type.MAC.toString(), "Mac line breaks"));
		lbDefs.add(new Definition(StandardLineBreaks.Type.UNIX.toString(), "Unix/Linux line breaks"));
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_BREAKS, "line break style", lbDefs, ""));
		List<Definition> fallbackDefs = new ArrayList<Definition>();
		fallbackDefs.add(new Definition("mask", "Mask the 8-dot pattern as a 6-dot pattern by ignoring dots 7 and 8"));
		fallbackDefs.add(new Definition("replace", "Replace the 8-dot pattern with a fixed 6-dot character"));
		fallbackDefs.add(new Definition("remove", "Remove the 8-dot pattern (shortens row)"));
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_FALLBACK, "8-dot fallback method", fallbackDefs, ""));
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_REPLACEMENT, "replacement character, expressed as a hexadecimal number representing the unicode code point of the replacement character (in the range 2800-283F)", "2800"));
	}
	
	/**
	 * Command line entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		PEFParser ui = new PEFParser();
		if (args.length<2) {
			ui.displayHelp(System.out);
		} else {
			try {
				Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);
				// remove required argument
				File input = new File(""+p.remove(ARG_PREFIX+0));
				File output = new File(""+p.remove(ARG_PREFIX+1));
				
				// validate input
				boolean ok = new PEFValidatorFacade(ValidatorFactory.newInstance()).validate(input, System.out);
				if (!ok) {
					System.out.println("Validation failed, exiting...");
					System.exit(-1);
				}
				
				// expand short forms, if any
				//ui.expandShortForm(p, PEFConverterFacade.KEY_EMBOSSER, ui.embosserSF);
				ui.expandShortForm(p, PEFConverterFacade.KEY_TABLE, ui.tableSF);
				
                            try ( // run
                                    FileOutputStream os = new FileOutputStream(output)) {
                                new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(input, os, null, p);
                            }
				System.out.println("Done!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return DotifyCLI.PEF2TEXT;
	}
	
	@Override
	public String getDescription() {
		return "Converts a PEF-file document into a text braille file.";
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
