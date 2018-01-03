package org.daisy.dotify.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.daisy.streamline.api.identity.IdentityProvider;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.validity.ValidationReport;
import org.daisy.streamline.api.validity.Validator;
import org.daisy.streamline.api.validity.ValidatorFactoryMaker;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;

class ValidateCLI implements CommandDetails {
	public static final String MEDIA_TYPE = "mediaType";
	private final CommandParser parser;
	
	public ValidateCLI() {
		this.parser = CommandParser.create(this);
	}

	public static void main(String[] args) throws IOException {
		ValidateCLI ui = new ValidateCLI();
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
		CommandParserResult result = ui.parser.parse(args);
		String mediaType = result.getOptional().get(MEDIA_TYPE);
		if (mediaType == null) {
			AnnotatedFile an = IdentityProvider.newInstance().identify(in);
			mediaType = an.getMediaType();
		}
		if (mediaType == null) {
			ExitCode.INTERNAL_ERROR.exitSystem(String.format("Could not determine media type for %s", in.getName()));
		}
		ValidatorFactoryMaker factoryMaker = ValidatorFactoryMaker.newInstance();
		Validator pv = factoryMaker.newValidator(mediaType);
		if (pv == null) {
			ExitCode.INTERNAL_ERROR.exitSystem(String.format("Could not find validator for '%s'", mediaType));
		}
		System.out.println("Validating " + in + " using \"" + pv.getClass().getName() + "\"" 
				+ (result.getOptional().isEmpty()?"":" with options " + result.getOptional())
				);
		Map<String, Object> options = new HashMap<>();
		options.putAll(result.getOptional());
		ValidationReport report = pv.validate(in.toURI().toURL(), options);
		System.out.println("Validation was " + (report.isValid() ? "succcessful" : "unsuccessful"));
		if (!report.isValid()) {
			System.out.println("Messages returned by the validator:");
			report.getMessages().stream().forEach(System.out::println); 
		}
	}

	@Override
	public String getName() {
		return DotifyCLI.VALIDATE;
	}
	
	@Override
	public String getDescription() {
		return "Validates a file.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		ArrayList<Argument> ret = new ArrayList<Argument>();
		ret.add(new Argument("input_file", "Path to the input file"));
		return ret;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		ValidatorFactoryMaker factoryMaker = ValidatorFactoryMaker.newInstance();
		ArrayList<OptionalArgument> ret = new ArrayList<OptionalArgument>();
		ret.add(new OptionalArgument(MEDIA_TYPE, "The media type for the file.", 
				factoryMaker.listIdentifiers().stream()
					.map(v->new Definition(v, ""))
					.collect(Collectors.toList()),
				"[detect]"));
		for (String identifier : factoryMaker.listIdentifiers()) {
			Validator v = factoryMaker.newValidator(identifier);
			for (UserOption u : v.listOptions()) {
				if (u.hasValues()) {
					List<Definition> values = u.getValues().stream()
						.map(uov->new Definition(uov.getName(), uov.getDescription()))
						.collect(Collectors.toList());
					ret.add(new OptionalArgument(u.getKey(), String.format("%s (applies to media type '%s')", u.getDescription(), identifier), values, u.getDefaultValue()));
				} else {
					ret.add(new OptionalArgument(u.getKey(), String.format("%s (applies to media type '%s')", u.getDescription(), identifier), u.getDefaultValue()));
				}
			}
		}
		return ret;
	}

}
