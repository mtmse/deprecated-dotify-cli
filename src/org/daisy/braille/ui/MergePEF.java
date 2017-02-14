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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.braille.consumer.validator.ValidatorFactory;
import org.daisy.braille.pef.PEFFileMerger;
import org.daisy.braille.pef.PEFFileMerger.SortType;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.Definition;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;

/**
 * Provides a UI for merging PEF-files. Not for public use. This class is a package class. Use BasicUI
 * @author Joel Håkansson
 */
class MergePEF extends AbstractUI {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		MergePEF ui = new MergePEF();
		if (args.length<3) {
			System.out.println("Expected three arguments.");
			System.out.println();
			ui.displayHelp(System.out);
			System.exit(-ExitCode.MISSING_ARGUMENT.ordinal());
		}
		PEFFileMerger merger = new PEFFileMerger(ValidatorFactory.newInstance());
		File input = new File(args[0]);
		File output = new File(args[1]);
		SortType sort = SortType.STANDARD;
		
		if (args.length>3) {
			Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);
			String sortString = p.remove("sort");
			if (sortString.equalsIgnoreCase("alpha")) {
				sort = SortType.STANDARD;
			} else if (sortString.equalsIgnoreCase("number")) {
				sort = SortType.NUMERAL_GROUPING;
			} else {
				System.out.println("Illegal value for argument sort: " + sortString);
				System.exit(-ExitCode.ILLEGAL_ARGUMENT_VALUE.ordinal());
			}
		}
		merger.merge(input, new FileOutputStream(output), args[2], sort);
	}

	@Override
	public String getName() {
		return BasicUI.merge;
	}
	
	@Override
	public String getDescription() {
		return "Merges several PEF files into one. The purpose is to facilitating the " +
				"use of PEF-files with braille editors that do not support multi volume files.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		ArrayList<Argument> ret = new ArrayList<Argument>();
		ret.add(new Argument("input_directory", "Path to input directory containing only PEF-files to merge"));
		ret.add(new Argument("ouput_file", "Path to output file"));
		ret.add(new Argument("identifier", "Publication identifier"));
		return ret;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		ArrayList<OptionalArgument> ret = new ArrayList<OptionalArgument>();
		ArrayList<Definition> values = new ArrayList<Definition>();
		values.add(new Definition("alpha", "Sort in alphabetical order (character by character from left to right)"));
		values.add(new Definition("number", "Sort groups of digits as numbers (from smaller to larger)"));
		ret.add(new OptionalArgument("sort", "Sorting method to use when determining file order based on file name", values, "alpha"));
		return ret;
	}

}
