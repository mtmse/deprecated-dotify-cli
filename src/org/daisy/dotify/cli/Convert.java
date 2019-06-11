package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.utils.pef.PEFConverterFacade;
import org.daisy.braille.utils.pef.UnsupportedWidthException;
import org.daisy.dotify.api.embosser.EmbosserCatalog;
import org.daisy.dotify.api.embosser.EmbosserFactoryException;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.api.translator.TranslatorType;
import org.daisy.dotify.common.text.FilterLocale;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
import org.daisy.streamline.api.config.ConfigurationDetails;
import org.daisy.streamline.api.config.ConfigurationsCatalog;
import org.daisy.streamline.api.config.ConfigurationsProviderException;
import org.daisy.streamline.api.identity.IdentityProvider;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.FormatIdentifier;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.CompiledTaskSystem;
import org.daisy.streamline.api.tasks.InternalTaskException;
import org.daisy.streamline.api.tasks.TaskSystem;
import org.daisy.streamline.api.tasks.TaskSystemException;
import org.daisy.streamline.api.tasks.TaskSystemFactoryException;
import org.daisy.streamline.api.tasks.TaskSystemFactoryMaker;
import org.daisy.streamline.api.validity.Validator;
import org.daisy.streamline.api.validity.ValidatorFactoryMaker;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.SwitchArgument;
import org.daisy.streamline.cli.SwitchMap;
import org.daisy.streamline.engine.DefaultTempFileWriter;
import org.daisy.streamline.engine.RunnerResult;
import org.daisy.streamline.engine.TaskRunner;
import org.xml.sax.SAXException;

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Convert implements CommandDetails {
	private static final Logger logger = Logger.getLogger(Convert.class.getCanonicalName());
	private static final HashMap<String, String> extensionBindings;
	static {
		extensionBindings = new HashMap<String, String>();
		extensionBindings.put("txt", SystemKeys.FORMATTED_TEXT_FORMAT.getKey());
	}
	private static final String DEFAULT_LOCALE = Locale.getDefault().toString().replaceAll("_", "-");
	private static final String CONFIG_KEY = "configs";
	private static final String WATCH_KEY = "watch";
	private static final String META_KEY = "meta";
	
	private static final int DEFAULT_POLL_TIME = 5000;
	private static final int MIN_POLL_TIME = 250;

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final SwitchMap switches;
	private final BrailleUtilsInfo brailleInfo;
	private final CommandParser parser;

	public Convert() {
		this.brailleInfo = new BrailleUtilsInfo();
		//Use lazy loading of argument details
		this.reqArgs = new ArrayList<Argument>();
		this.optionalArgs = new ArrayList<OptionalArgument>();
		this.switches = new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('w', WATCH_KEY, WATCH_KEY, "" + DEFAULT_POLL_TIME, "Keeps the conversion in sync by watching the input file for changes and rerunning the conversion automatically when the input is modified."))
				.addSwitch(new SwitchArgument('o', SystemKeys.LIST_OPTIONS.getKey(), SystemKeys.LIST_OPTIONS.getKey(), "true", "Lists additional options as the conversion runs."))
				.addSwitch(new SwitchArgument('c', CONFIG_KEY, META_KEY, CONFIG_KEY, "Lists known configurations."))
				.build();
		this.parser = CommandParser.create(this);
	}

	/**
	 * Provides a entry point for Dotify from the command line.
	 * @param args command line arguments
	 * @throws IOException if there is an i/o exception
	 * @throws InternalTaskException if there is a problem with running the tasks
	 */
	public static void main(String[] args) throws InternalTaskException, IOException {
		Convert m = new Convert();
		CommandParserResult result = m.parser.parse(args);
		List<String> p = result.getRequired();
		if (args.length<2 || p.size()<2) {
			if (CONFIG_KEY.equals(result.getOptional().get(META_KEY))) {
				System.out.println("Known configurations (locale, braille mode):");
				BrailleTranslatorFactoryMaker.newInstance().listSpecifications().stream()
					.filter(ts->ts.getModeDetails().getType().map(v2->v2!=TranslatorType.BYPASS&&v2!=TranslatorType.PRE_TRANSLATED).orElse(true))
					.sorted()
					.map(ts->"" + ts.getLocale() + ", " + ts.getMode())
					.forEach(System.out::println);
				ExitCode.OK.exitSystem();
			} else {
				System.out.println("Expected at least two arguments");
				
				System.out.println();
				m.parser.displayHelp(System.out);
				ExitCode.MISSING_ARGUMENT.exitSystem();
			}
		} else if (p.size()>2) { 
			System.out.println("Unknown argument(s): " + p.subList(2, p.size()));
			System.out.println();
			m.parser.displayHelp(System.out);
			ExitCode.UNKNOWN_ARGUMENT.exitSystem();
		}
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			ExitCode.MISSING_RESOURCE.exitSystem("Cannot find input file: " + input);
		}
		
		final File output = new File(p.get(1)).getAbsoluteFile();

		final String context;
		{
			String s = result.getOptional().get("locale");
			if (s==null || s.equals("")) {
				s = DEFAULT_LOCALE;
			}
			context = s;
		}

		//File output = new File(args[1]);
		final HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY.getKey(), TEMP_DIR);

		props.putAll(result.getOptional());
		
		if (input.isDirectory() && output.isDirectory()) {
			if (result.getOptional().get(WATCH_KEY)!=null) {
				logger.warning("'" + WATCH_KEY + "' is not implemented for batch mode.");
			}
			if ("true".equals(props.get(SystemKeys.WRITE_TEMP_FILES.getKey()))) {
				ExitCode.ILLEGAL_ARGUMENT_VALUE.exitSystem("Cannot write debug files in batch mode.");
			}
			String format = props.get(SystemKeys.OUTPUT_FORMAT.getKey());
			if (format==null) {
				ExitCode.MISSING_ARGUMENT.exitSystem(SystemKeys.OUTPUT_FORMAT.getKey() + " must be specified in batch mode.");
			} else if (format.equals(SystemKeys.PEF_FORMAT.getKey())) {
				format = "pef";
			} else if (format.equals(SystemKeys.FORMATTED_TEXT_FORMAT.getKey())) {
				format = "txt";
			} else if (format.equals(SystemKeys.OBFL_FORMAT.getKey())) {
				format = "obfl";
			} else {
				ExitCode.ILLEGAL_ARGUMENT_VALUE.exitSystem("Unknown output format.");
			}
			//Experimental parallelization code in comment.
			//ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			final String ext = format;
			for (final File f : input.listFiles()) {
				//es.execute(new Runnable() {
					//public void run() {
						try {
					m.runDotify(f, new File(output, f.getName() + "." + ext), context, props);
						} catch (InternalTaskException e) {
							logger.log(Level.WARNING, "Failed to process " + f, e);
						} catch (IOException e) {
							logger.log(Level.WARNING, "Failed to read " + f, e);
						}
					//}});
			}
			//es.shutdown();
			//try {
			//	es.awaitTermination(600, TimeUnit.SECONDS);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//}
		} else if (input.isDirectory()) { 
			ExitCode.ILLEGAL_ARGUMENT_VALUE.exitSystem("If input is a directory, output must be an existing directory too.");
		} else {
			String pollWaitStr = result.getOptional().get(WATCH_KEY);
			if (pollWaitStr!=null) {
				int pollWait = DEFAULT_POLL_TIME;
				try {
					pollWait = Math.max(Integer.parseInt(pollWaitStr), MIN_POLL_TIME);
				} catch (NumberFormatException e) {
					logger.warning("Could not parse " + WATCH_KEY + " value '" + pollWaitStr + "' as an integer.");
				}
				logger.fine("Poll time is " + pollWait);
				long modified = 0;
				while (input.exists()) {
					if (modified<input.lastModified()) {
						modified = input.lastModified();
						try {
							//delete the output so that it is not there if something goes wrong
							output.delete();
							m.runDotify(input, output, context, props);
						} catch (Exception e) { 
							logger.log(Level.SEVERE, "A severe error occurred.", e);
						}
						logger.info("Waiting for changes in " + input);
					}
					try {
						Thread.sleep(pollWait);
					} catch (InterruptedException e) {
					}
				}
			} else {
				m.runDotify(input, output, context, props);
			}
		}
	}
	
	private void runDotify(File input, File output, String context, HashMap<String, String> props) throws InternalTaskException, IOException {
		if (!input.exists()) {
			ExitCode.MISSING_RESOURCE.exitSystem("Cannot find input file: " + input);
		}
		run(input, output, FilterLocale.parse(context), props);
		if (output.exists()) {
			AnnotatedFile ao = IdentityProvider.newInstance().identify(output);
			String mediaType = ao.getMediaType();
			ValidatorFactoryMaker validatorFactory = ValidatorFactoryMaker.newInstance();
			Validator validator;
			if (mediaType!=null && (validator = validatorFactory.newValidator(mediaType))!=null) {
				logger.info(String.format("Validating output using %s", validator.getClass().getName()));
				if (!validator.validate(output.toURI().toURL()).isValid()) {
					logger.warning("Validation failed: " + output);
				} else {
					logger.info("Output is valid.");
					if (mediaType.equals("application/x-pef+xml") && props.containsKey(PEFConverterFacade.KEY_TABLE)) {
						// create brl
						HashMap<String, String> p = new HashMap<String, String>();
						p.put(PEFConverterFacade.KEY_TABLE, props.get(PEFConverterFacade.KEY_TABLE));
						try {
							brailleInfo.getShortFormResolver().expandShortForm(p, PEFConverterFacade.KEY_TABLE);
						} catch (IllegalArgumentException e) {
							ExitCode.ILLEGAL_ARGUMENT_VALUE.exitSystem(e.getMessage());
						}
						File f = new File(output.getParentFile(), output.getName() + ".brl");
						logger.info("Writing brl to " + f.getAbsolutePath());
						try (FileOutputStream os = new FileOutputStream(f)) {
							new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(output, os, null, p);
						} catch (ParserConfigurationException e) {
							logger.log(Level.FINE, "Parse error when converting to brl", e);
						} catch (SAXException e) {
							logger.log(Level.FINE, "SAX error when converting to brl", e);
						} catch (UnsupportedWidthException e) {
							logger.log(Level.FINE, "Width error when converting to brl", e);
						} catch (NumberFormatException e) {
							logger.log(Level.FINE, "Number format error when converting to brl", e);
						} catch (EmbosserFactoryException e) {
							logger.log(Level.FINE, "Embosser error when converting to brl", e);
						}
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return DotifyCLI.CONVERT;
	}
	
	@Override
	public String getDescription() {
		return "Converts documents into braille.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		if (reqArgs.isEmpty()) {
			TaskSystemFactoryMaker fm = TaskSystemFactoryMaker.newInstance();
			//TODO: map identifiers to file formats
			Set<String> inputFormats = fm.listInputs().stream().map(v->v.getIdentifier()).collect(Collectors.toSet());
			Set<String> outputFormats = fm.listOutputs().stream().map(v->v.getIdentifier()).collect(Collectors.toSet());
			reqArgs.add(new Argument("path_to_input", "Path to the input file " + inputFormats));
			reqArgs.add(new Argument("path_to_output", "Path to the output file " + outputFormats));
		}
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		if (optionalArgs.isEmpty()) {
			{
				ArrayList<Definition> vals = new ArrayList<Definition>();
				ConfigurationsCatalog c = ConfigurationsCatalog.newInstance();
				List<ConfigurationDetails> detailsList = c.getConfigurationDetails().stream()
						.sorted((o1, o2) -> {
							return o1.getKey().compareTo(o2.getKey());
						})
						.collect(Collectors.toList());
				for (ConfigurationDetails details : detailsList) {
					vals.add(new Definition(details.getKey(), details.getDescription()));
				}
				vals.add(new Definition("[other]", "Path to setup file"));
				optionalArgs.add(new OptionalArgument("preset", "A preset to use", vals, null));
			}
			optionalArgs.add(new OptionalArgument("locale", "The target locale for the result", DEFAULT_LOCALE));
			
			{
				ArrayList<Definition> vals = new ArrayList<Definition>();
				vals.add(new Definition(SystemKeys.PEF_FORMAT.getKey(), "write result in PEF-format"));
				vals.add(new Definition(SystemKeys.FORMATTED_TEXT_FORMAT.getKey(), "write result as text"));
				//vals.add(new Definition(SystemKeys.OBFL_FORMAT.getKey(), "write result in OBFL-format (bypass formatter)"));
				optionalArgs.add(new OptionalArgument(SystemKeys.OUTPUT_FORMAT.getKey(), "Specifies output format", vals, "[detect]"));
			}
			{
				ArrayList<Definition> vals = new ArrayList<Definition>();
				vals.add(new Definition("true", "outputs temp files"));
				vals.add(new Definition("false", "does not output temp files"));
				optionalArgs.add(new OptionalArgument(SystemKeys.WRITE_TEMP_FILES.getKey(), "Writes temp files", vals, "false"));
			}
			optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY.getKey(), "Path to temp files directory", DefaultTempFileWriter.TEMP_DIR));
			optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_TABLE, "If specified, an ASCII-braille file (.brl) is generated in addition to the PEF-file using the specified braille code table", brailleInfo.getDefinitionList(), ""));
		}
		return optionalArgs;
	}

	@Override
	public SwitchMap getSwitches() {
		return switches;
	}
	
	/**
	 * Runs Dotify with the supplied parameters.
	 * @param input the input file
	 * @param output the output file
	 * @param locale the language/region context
	 * @param params additional parameters
	 * @throws IOException if there is an i/o error
	 * @throws InternalTaskException if there is a problem with running the task system
	 */
	public static void run(File input, File output, String locale, Map<String, String> params) throws IOException, InternalTaskException {
		run(input, output, FilterLocale.parse(locale), params);
	}

	/**
	 * Runs Dotify with the supplied parameters.
	 * @param inputFile the input file
	 * @param output the output file
	 * @param context the language/region context
	 * @param params additional parameters
	 * @throws IOException if there is an i/o error
	 * @throws InternalTaskException if there is a problem with running the task system
	 */
	public static void run(File inputFile, File output, FilterLocale context, Map<String, String> params) throws IOException, InternalTaskException {
		boolean writeTempFiles = "true".equals(params.get(SystemKeys.WRITE_TEMP_FILES.getKey()));
		boolean keepTempFilesOnSuccess = !("false".equals(params.get(SystemKeys.KEEP_TEMP_FILES_ON_SUCCESS.getKey())));
		
		String cols = params.get("cols");
		if (cols==null || "".equals(cols)) {
			params.remove("cols");
		}
		
		TaskSystemFactoryMaker specs = TaskSystemFactoryMaker.newInstance();

		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(params);

		AnnotatedFile ai = IdentityProvider.newInstance().identify(inputFile);
		map.put(SystemKeys.INPUT.getKey(), ai.getFile().getAbsolutePath());

		String inputFormat = getFormatString(ai);
		if (inputFormat!=null) {
			if (!supportsInputFormat(FormatIdentifier.with(inputFormat), specs)) {
				logger.warning("No input factory for " + inputFormat);
				logger.fine("Note, the following detection code has been deprected. In future versions, an exception will be thrown if this point is reached."
						+ " To avoid this, use the IdentifierFactory interface to implement a detector for the file type.");
				// attempt to detect a supported type
				try {
					if (XMLTools.isWellformedXML(ai.getFile())) {
						ai = DefaultAnnotatedFile.with(ai).extension("xml").build();
						inputFormat = ai.getExtension();
						logger.info("Input is well-formed xml.");
					}
				} catch (XMLToolsException e) {
					e.printStackTrace();
				}
			} else {
				logger.info("Found an input factory for " + inputFormat);
			}
		}
		
		map.put(SystemKeys.INPUT_FORMAT.getKey(), inputFormat);

		String outputformat = params.get(SystemKeys.OUTPUT_FORMAT.getKey());
		if (outputformat==null || "".equals(outputformat)) {
			int indx = output.getName().lastIndexOf('.');
			if (indx>-1) {
				String ext = output.getName().substring(indx+1).toLowerCase();
				if (supportsOutputFormat(FormatIdentifier.with(ext), specs)) {
					outputformat = ext;
				} else {
					outputformat = extensionBindings.get(ext);
				}
			}
			if (outputformat==null) {
				throw new IllegalArgumentException("Cannot detect file format for output file. Please specify output format.");
			}
		}
		map.put(SystemKeys.OUTPUT_FORMAT.getKey(), outputformat.toLowerCase());
		
		map.put(SystemKeys.SYSTEM_NAME.getKey(), SystemProperties.SYSTEM_NAME);
		map.put(SystemKeys.SYSTEM_BUILD.getKey(), SystemProperties.SYSTEM_BUILD);
		map.put(SystemKeys.SYSTEM_RELEASE.getKey(), SystemProperties.SYSTEM_RELEASE);
		map.put("conversionDate", new Date().toString());

		map.put(SystemKeys.INPUT_URI.getKey(), ai.getFile().toURI().toString());
		
		// Add default values for optional parameters

		final String tempFilesDirectory = params.get(SystemKeys.TEMP_FILES_DIRECTORY.getKey());

		// Load additional settings from file
		if (map.get("config")==null || "".equals(map.get("config"))) {
			map.remove("config");
		} else {
			File config = new File(map.get("config"));
			Properties p = new Properties();
			FileInputStream in = new FileInputStream(config);
			p.loadFromXML(in);
			for (Object key : p.keySet()) {
				map.put(key.toString(), p.get(key).toString());
			}
		}

		// Load setup
		String setup = map.remove("preset");
		Map<String, Object> rp = loadSetup(map, setup);

		boolean shouldPrintOptions = "true".equalsIgnoreCase(map.getOrDefault(SystemKeys.LIST_OPTIONS.getKey(), "false"));
		// Run tasks
		try {
			TaskSystem ts = TaskSystemFactoryMaker.newInstance().newTaskSystem(inputFormat, outputformat, context.toString());
			try {
				logger.info("About to run with parameters " + rp);
				CompiledTaskSystem tl = ts.compile(rp);
				TaskRunner.Builder builder = TaskRunner.withName(ts.getName())
						.writeTempFiles(writeTempFiles)
						.keepTempFiles(keepTempFilesOnSuccess)
						.tempFileWriter(
								new DefaultTempFileWriter.Builder()
								.prefix("Dotify")
								.tempFilesFolder(tempFilesDirectory)
								.build()
						);
				List<RunnerResult> res = builder.build().runTasks(ai, output, tl);
				if (shouldPrintOptions) {
					logOptions(tl, res);
				}
			} catch (TaskSystemException e) {
				throw new RuntimeException("Unable to run '" +ts.getName() + "' with parameters " + rp, e);
			}
		} catch (TaskSystemFactoryException e) {
			throw new RuntimeException("Unable to retrieve a TaskSystem", e);
		}
	}
	
	private static String getFormatString(AnnotatedFile f) {
		if (f.getFormatName()!=null) {
			return f.getFormatName();
		} else if (f.getExtension()!=null) {
			return f.getExtension();
		} else if (f.getMediaType()!=null) {
			return f.getMediaType();
		} else {
			return null;
		}
	}
	
	private static boolean supportsInputFormat(FormatIdentifier inputFormat, TaskSystemFactoryMaker specs) {
		return specs.listInputs().stream().filter(v->v.equals(inputFormat)).findAny().isPresent();
	}
	
	private static boolean supportsOutputFormat(FormatIdentifier outputFormat, TaskSystemFactoryMaker specs) {
		return specs.listOutputs().stream().filter(v->v.equals(outputFormat)).findAny().isPresent();
	}
	
	private static Map<String, Object> loadSetup(Map<String, String> guiParams, String setup) {
		Map<String, Object> ret;
		if (setup==null) {
			ret = new HashMap<>();
		} else {
			ret = new HashMap<>(loadConfiguration(setup));
		}

		// GUI parameters should take precedence
		ret.putAll(guiParams);

		return ret;
	}
	
	private static Map<String, Object> loadConfiguration(String setup) {
		try {
			ConfigurationsCatalog cm = ConfigurationsCatalog.newInstance();
			return cm.getConfiguration(setup);
		} catch (ConfigurationsProviderException e) {
			//try as file
			Properties p0 = new Properties();
			URL configURL;
			try {
				configURL = new URL(setup);
				try {
					p0.loadFromXML(configURL.openStream());
					Map<String, Object> ret = new HashMap<>();
					for (Object key : p0.keySet()) {
						ret.put(key.toString(), p0.get(key));
					}
					return ret;
				} catch (FileNotFoundException e2) {
					throw new RuntimeException("Configuration file not found: " + configURL, e2);
				} catch (InvalidPropertiesFormatException e2) {
					throw new RuntimeException("Configuration file could not be parsed: " + configURL, e2);
				} catch (IOException e2) {
					throw new RuntimeException("IOException while reading configuration file: " + configURL, e2);
				}
			} catch (MalformedURLException e1) {
				throw new RuntimeException("'"+ setup + "' is not a known configuration nor a valid URL.", e1);
			}
		}
	}
	
	private static void logOptions(CompiledTaskSystem ts, List<RunnerResult> res) {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			pw.println("Printing additional options.");
			printOptions("Options for " + ts.getName(), ts.getOptions(), pw);
			for (RunnerResult r : res) {
				printOptions("Options for " + r.getTask().getName(), r.getTask().getOptions(), pw);
			}
		}
		if (sw.toString().length()>0) {
			logger.info(sw.toString());
		}
	}
	
	private static void printOptions(String title, List<UserOption> options, PrintWriter out) {
		if (options==null || options.isEmpty()) {
			return;
		}
		out.println("=== "+title+" ===");
		for (UserOption to : options) {
			out.print("--"+to.getKey()+"=<value>");
			if (to.getValues()==null && to.getDefaultValue()!=null && !"".equals(to.getDefaultValue())) {
				out.print(" ("+to.getDefaultValue()+")");
			}
			out.println();
			out.println("\t" + to.getDescription());
			List<UserOptionValue> values = to.getValues();
			if (values!=null) {
				for (UserOptionValue tov : values) {
					out.print("\t\t" + tov.getName());
					if (tov.getName().equals(to.getDefaultValue())) {
						out.print(" (default)");
					}
					out.println();
					if (tov.getDescription()!=null && !"".equals(tov.getDescription())) {
						out.println("\t\t\t" + tov.getDescription());
					}
				}
			} 
		}
		out.println();
	}

}
