package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.daisy.dotify.common.text.FilterLocale;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
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
import org.daisy.streamline.engine.DefaultTempFileWriter;
import org.daisy.streamline.engine.RunnerResult;
import org.daisy.streamline.engine.TaskRunner;

/**
 * Provides an entry point for simple embedding of Dotify. To run, call <code>Dotify.run</code>.
 * 
 * @author Joel Håkansson
 *
 */
public class Dotify {
	private static final Logger logger = Logger.getLogger(Dotify.class.getCanonicalName());
	private static final HashMap<String, String> extensionBindings;
	static {
		extensionBindings = new HashMap<String, String>();
		extensionBindings.put("txt", SystemKeys.FORMATTED_TEXT_FORMAT.getKey());
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
