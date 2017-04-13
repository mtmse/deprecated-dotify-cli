package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.api.embosser.EmbosserFactoryException;
import org.daisy.braille.consumer.embosser.EmbosserCatalog;
import org.daisy.braille.pef.PEFConverterFacade;
import org.daisy.braille.pef.PEFValidator;
import org.daisy.braille.pef.UnsupportedWidthException;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.CommandParserResult;
import org.daisy.cli.Definition;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.SwitchArgument;
import org.daisy.dotify.Dotify;
import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.api.config.ConfigurationDetails;
import org.daisy.dotify.api.tasks.InternalTaskException;
import org.daisy.dotify.api.tasks.TaskGroupInformation;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.common.text.FilterLocale;
import org.daisy.dotify.consumer.config.ConfigurationsCatalog;
import org.daisy.dotify.consumer.tasks.TaskGroupFactoryMaker;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.tasks.runner.DefaultTempFileWriter;
import org.xml.sax.SAXException;

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Convert extends AbstractUI {
	private static final Logger logger = Logger.getLogger(Convert.class.getCanonicalName());
	//private static final String DEFAULT_TEMPLATE = "A4-w32";
	private static final String DEFAULT_LOCALE = Locale.getDefault().toString().replaceAll("_", "-");
	private static final String CONFIG_KEY = "configs";
	private static final String WATCH_KEY = "watch";
	protected static final String META_KEY = "meta";
	
	private static final int DEFAULT_POLL_TIME = 5000;
	private static final int MIN_POLL_TIME = 250;

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final BrailleUtilsInfo brailleInfo;

	public Convert() {
		this.brailleInfo = new BrailleUtilsInfo();
		//Use lazy loading of argument details
		this.reqArgs = new ArrayList<Argument>();
		this.optionalArgs = new ArrayList<OptionalArgument>();
		parser.addSwitch(new SwitchArgument('w', WATCH_KEY, WATCH_KEY, "" + DEFAULT_POLL_TIME, "Keeps the conversion in sync by watching the input file for changes and rerunning the conversion automatically when the input is modified."));
		parser.addSwitch(new SwitchArgument('o', SystemKeys.LIST_OPTIONS, SystemKeys.LIST_OPTIONS, "true", "Lists additional options as the conversion runs."));
		parser.addSwitch(new SwitchArgument('c', CONFIG_KEY, META_KEY, CONFIG_KEY, "Lists known configurations."));
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
				ArrayList<TranslatorSpecification> s = new ArrayList<TranslatorSpecification>();
				s.addAll(BrailleTranslatorFactoryMaker.newInstance().listSpecifications());
				Collections.sort(s);
				System.out.println("Known configurations (locale, braille mode):");
				for (TranslatorSpecification ts : s) {
					System.out.println("  " + ts.getLocale() + ", " + ts.getMode());
				}
				Convert.exitWithCode(ExitCode.OK);
			} else {
				System.out.println("Expected at least two arguments");
				
				System.out.println();
				m.displayHelp(System.out);
				Convert.exitWithCode(ExitCode.MISSING_ARGUMENT);
			}
		} else if (p.size()>2) { 
			System.out.println("Unknown argument(s): " + p.subList(2, p.size()));
			System.out.println();
			m.displayHelp(System.out);
			Convert.exitWithCode(ExitCode.UNKNOWN_ARGUMENT);
		}
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			Convert.exitWithCode(ExitCode.MISSING_RESOURCE);
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
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY, TEMP_DIR);

		props.putAll(result.getOptional());
		
		if (input.isDirectory() && output.isDirectory()) {
			if (result.getOptional().get(WATCH_KEY)!=null) {
				logger.warning("'" + WATCH_KEY + "' is not implemented for batch mode.");
			}
			if ("true".equals(props.get(SystemKeys.WRITE_TEMP_FILES))) {
				Convert.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "Cannot write debug files in batch mode.");
			}
			String format = props.get(SystemKeys.OUTPUT_FORMAT);
			if (format==null) {
				Convert.exitWithCode(ExitCode.MISSING_ARGUMENT, SystemKeys.OUTPUT_FORMAT + " must be specified in batch mode.");
			} else if (format.equals(SystemKeys.PEF_FORMAT)) {
				format = "pef";
			} else if (format.equals(SystemKeys.TEXT_FORMAT)) {
				format = "txt";
			} else if (format.equals(SystemKeys.OBFL_FORMAT)) {
				format = "obfl";
			} else {
				Convert.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "Unknown output format.");
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
			Convert.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "If input is a directory, output must be an existing directory too.");
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
			Convert.exitWithCode(ExitCode.MISSING_RESOURCE, "Cannot find input file: " + input);
		}
		Dotify.run(input, output, FilterLocale.parse(context), props);
		int i = output.getName().lastIndexOf(".");
		String format = "";
		if (output.getName().length()>i) {
			format = output.getName().substring(i+1);
		}
		if (format.equalsIgnoreCase(SystemKeys.PEF_FORMAT)) {
			logger.info("Validating output...");
			PEFValidator validator = new PEFValidator();
			if (!validator.validate(output.toURI().toURL())) {
				logger.warning("Validation failed: " + output);
			} else {
				logger.info("Output is valid.");
				if (props.containsKey(PEFConverterFacade.KEY_TABLE)) {
					// create brl
					HashMap<String, String> p = new HashMap<String, String>();
					p.put(PEFConverterFacade.KEY_TABLE, props.get(PEFConverterFacade.KEY_TABLE));
					expandShortForm(p, PEFConverterFacade.KEY_TABLE, brailleInfo.getShortFormResolver());
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
			Set<TaskGroupInformation> specs = TaskGroupFactoryMaker.newInstance().listAll();
			Set<String> inputFormats = new HashSet<>();
			Set<String> outputFormats = new HashSet<>();
			for (TaskGroupInformation spec : specs) {
				inputFormats.add(spec.getInputFormat());
				outputFormats.add(spec.getOutputFormat());
			}
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
				for (ConfigurationDetails details : c.getConfigurationDetails()) {
					vals.add(new Definition(details.getKey(), details.getDescription()));
				}
				vals.add(new Definition("[other]", "Path to setup file"));
				optionalArgs.add(new OptionalArgument("preset", "A preset to use", vals, null));
			}
			optionalArgs.add(new OptionalArgument("locale", "The target locale for the result", DEFAULT_LOCALE));
			
			{
				ArrayList<Definition> vals = new ArrayList<Definition>();
				vals.add(new Definition(SystemKeys.PEF_FORMAT, "write result in PEF-format"));
				vals.add(new Definition(SystemKeys.TEXT_FORMAT, "write result as text"));
				//vals.add(new Definition(SystemKeys.OBFL_FORMAT, "write result in OBFL-format (bypass formatter)"));
				optionalArgs.add(new OptionalArgument(SystemKeys.OUTPUT_FORMAT, "Specifies output format", vals, "[detect]"));
			}
			{
				ArrayList<Definition> vals = new ArrayList<Definition>();
				vals.add(new Definition("true", "outputs temp files"));
				vals.add(new Definition("false", "does not output temp files"));
				optionalArgs.add(new OptionalArgument(SystemKeys.WRITE_TEMP_FILES, "Writes temp files", vals, "false"));
			}
			optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY, "Path to temp files directory", DefaultTempFileWriter.TEMP_DIR));
			optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_TABLE, "If specified, an ASCII-braille file (.brl) is generated in addition to the PEF-file using the specified braille code table", brailleInfo.getDefinitionList(), ""));
		}
		return optionalArgs;
	}

}
