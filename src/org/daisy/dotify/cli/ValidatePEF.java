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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.braille.utils.api.validator.ValidatorFactory;
import org.daisy.braille.utils.pef.PEFValidator;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;

class ValidatePEF implements CommandDetails {
	/**
	 * Prefix used for required arguments in the arguments map
	 */
	public static final String ARG_PREFIX = "required-";
	public enum Mode {FULL, LIGHT};
	private final CommandParser parser;
	
	public ValidatePEF() {
		this.parser = CommandParser.create(this);
	}

	public static void main(String[] args) throws IOException {
		ValidatePEF ui = new ValidatePEF();
		if (args.length<1) {
			System.out.println("Expected one more argument: input [options ...]");
			System.out.println();
			ui.parser.displayHelp(System.out);
			ExitCode.MISSING_ARGUMENT.exitSystem();
		}
		File in = new File(args[0]);
		if (!in.exists()) {
			ExitCode.MISSING_RESOURCE.exitSystem("File does not exist: " + in);
		}
		Mode m = Mode.values()[0];
		if (args.length>1) {
			Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);
			String mode = p.remove("mode");
			if (mode!=null) {
				try {
					m = Mode.valueOf(mode.toUpperCase());
				} catch (Exception e) {
					System.out.println("Could not set mode to '" + mode + "'");
				}
			}
		}  
		ValidatorFactory factory = ValidatorFactory.newInstance();
		org.daisy.braille.utils.api.validator.Validator pv = factory.newValidator(PEFValidator.class.getCanonicalName());
		if (pv == null) {
			ExitCode.INTERNAL_ERROR.exitSystem("Could not find validator.");
		}
		pv.setFeature(PEFValidator.FEATURE_MODE, m.equals(Mode.LIGHT) ? PEFValidator.Mode.LIGHT_MODE : PEFValidator.Mode.FULL_MODE);
		System.out.println("Validating " + in + " using \"" + pv.getDisplayName() + "\" (" + pv.getDescription() + ") in " + pv.getFeature(PEFValidator.FEATURE_MODE));
		boolean ok = pv.validate(in.toURI().toURL());
		System.out.println("Validation was " + (ok ? "succcessful" : "unsuccessful"));
		if (!ok) {
			System.out.println("Messages returned by the validator:");
                    try (InputStreamReader report = new InputStreamReader(pv.getReportStream())) {
                        int c;
                        while ((c = report.read()) != -1) {
                            System.out.print((char)c);
                        }
                    }
		}
	}

	@Override
	public String getName() {
		return DotifyCLI.VALIDATE;
	}
	
	@Override
	public String getDescription() {
		return "Validates a PEF-file.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		ArrayList<Argument> ret = new ArrayList<Argument>();
		ret.add(new Argument("input_file", "Path to the input PEF-file"));
		return ret;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		ArrayList<OptionalArgument> ret = new ArrayList<OptionalArgument>();
		ArrayList<Definition> values = new ArrayList<Definition>();
		values.add(new Definition(Mode.FULL.toString().toLowerCase(), "Validate using full mode"));
		values.add(new Definition(Mode.LIGHT.toString().toLowerCase(), "Validate using light mode"));
		ret.add(new OptionalArgument("mode", "Validation mode", values, Mode.FULL.toString().toLowerCase()));
		return ret;
	}

}
