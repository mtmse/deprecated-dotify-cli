package org.daisy.dotify.cli;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.daisy.braille.utils.pef.FileTools;
import org.daisy.dotify.SystemProperties;
import org.daisy.streamline.cli.AbstractUI;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.SwitchArgument;

/**
 * Provides a basic command line UI for core functionality in
 * org.daisy.braille.
 * @author Joel Håkansson
 */
public class DotifyCLI extends AbstractUI {
	public static final String EMBOSS = "emboss";
	public static final String TEXT2PEF = "text2pef";
	public static final String PEF2TEXT = "pef2text";
	public static final String VALIDATE = "validate";
	public static final String SPLIT = "split";
	public static final String MERGE = "merge";
	public static final String GENERATE = "generate";
	public static final String LIST = "list";
	public static final String FIND = "find";
	//public static final String clear = "clear";
	//public static final String setup = "setup";
	public static final String HELP = "help";
	public static final String INSPECT = "inspect";
	public static final String CONVERT = "convert";
	public static final String TRANSLATE = "translate";
	
	protected static final String META_KEY = "meta";
	private static final String VERSION_KEY = "version";

	private final String[] args;
	private final Logger logger;
	
	private final Map<String, Class<? extends AbstractUI>> commands;
	private final List<Definition> values;
	
	/**
	 * Creates a new Basic UI
	 * @param args the application arguments
	 */
	public DotifyCLI(String[] args) {
		logger = Logger.getLogger(DotifyCLI.class.getCanonicalName());
		logger.fine(System.getProperties().toString());
		this.args = args;
		this.values = new ArrayList<Definition>();
		this.commands = new HashMap<>();
		// Main commands
		putCommand(CONVERT, "formats and translates a document into braille", Convert.class);
		putCommand(EMBOSS, "embosses a PEF-file", EmbossPEF.class);
		putCommand(VALIDATE, "validates a PEF-file", ValidatePEF.class);
		putCommand(INSPECT, "lists metadata about a PEF-file", PEFInfo.class);
		putCommand(FIND, "finds PEF-files based on file metadata", FindPEF.class);

		//Compatibility commands
		putCommand(TEXT2PEF, "upgrades braille text to pef", TextParser.class);
		putCommand(PEF2TEXT, "downgrades pef to braille text", PEFParser.class);
		putCommand(SPLIT, "splits a PEF-file into several single volume files", SplitPEF.class);
		putCommand(MERGE, "merges several single volume PEF-files into one", MergePEF.class);

		//Interactive commands
		putCommand(TRANSLATE, "translates text on system in to braille on system out", TranslateCLI.class);
		commands.put("eval", EvaluateCLI.class);

		//Diagnostics commands
		putCommand(GENERATE, "generates a random PEF-file for testing", GeneratePEF.class);
		putCommand(LIST, "lists stuff", ListStuff.class);
		parser.addSwitch(new SwitchArgument('v', VERSION_KEY, META_KEY, VERSION_KEY, "Displays version information."));

		//Help
		values.add(new Definition(HELP, "Without additional arguments, this text is displayed. To get help on a specific command, type help <command>"));
		
		/*
 			values.add(new Definition(clear, "clear settings"));
			values.add(new Definition(setup, "setup"));
			values.add(new Definition(help, "help"));
			case CLEAR: { EmbossPEF ui = new EmbossPEF(); ui.clearSettings(); break; }
			case SETUP: { EmbossPEF ui = new EmbossPEF(); ui.setup(); break; }
		}*/
	}
	
	protected void putCommand(String cmd, String desc, Class<? extends AbstractUI> c) {
		values.add(new Definition(cmd, desc));
		commands.put(cmd, c);
	}
	
	/**
	 * Sets the context class loader to an URLClassLoader containing the jars found in
	 * the specified path. 
	 * @param dir the directory to search for jar-files.
	 */
	public void setPluginsDir(File dir) {
		// list jars and convert to URL's
		URL[] jars = FileTools.toURL(FileTools.listFiles(dir, ".jar"));
		for (URL u : jars) {
			logger.info("Found jar " + u);
		}
		// set context class loader
		if (jars.length>0) {
			Thread.currentThread().setContextClassLoader(new URLClassLoader(jars));
		}
	}

	/**
	 * Runs the application.
	 * @throws Exception if something bad happens
	 */
	public void run() throws Exception {
		if (args.length<1) {
			System.out.println("Expected at least one argument.");
			System.out.println();
			displayHelp(System.out);
			System.exit(-ExitCode.MISSING_ARGUMENT.ordinal());
		}
		setPluginsDir(new File("plugins"));
		if (HELP.equalsIgnoreCase(args[0])) {
			if (args.length>=2) {
				Class<? extends AbstractUI> clazz = commands.get(args[1]);
				if (clazz!=null) {
					AbstractUI ui = clazz.newInstance();
					ui.displayHelp(System.out);
					exitWithCode(ExitCode.OK);
				} else {
					System.out.println("Unknown argument '" + args[1] + "'");
					displayHelp(System.out);
					System.exit(-ExitCode.UNKNOWN_ARGUMENT.ordinal());
				}
			}
			displayHelp(System.out);
		} else {
			CommandParserResult result = parser.parse(args);
			if (result.getRequired().isEmpty() && VERSION_KEY.equals(result.getOptional().get(META_KEY))) {
				System.out.println("--- " + getName() + " ---");
				System.out.println("Version: " + (getVersion()!=null?getVersion():"N/A"));
				System.out.println("Build: " + (getBuildIdentifier()!=null?getBuildIdentifier():"N/A"));
				DotifyCLI.exitWithCode(ExitCode.OK);
			}
			Class<? extends Object> clazz = commands.get(args[0]);
			if (clazz!=null) {
				Method method = clazz.getMethod("main", new Class[]{String[].class});
				method.invoke(null, (Object)getArgsSubList(1));
			} else {
				System.out.println("Unknown argument '" + args[0] + "'");
				displayHelp(System.out);
				System.exit(-ExitCode.UNKNOWN_ARGUMENT.ordinal());
			}
		}
	}
	
	/**
	 * Command line entry point
	 * @param args the application arguments
	 * @throws Exception if something goes wrong
	 */
	public static void main(String[] args) throws Exception {
		DotifyCLI ui = new DotifyCLI(args);
		ui.run();
	}

	private String[] getArgsSubList(int offset) {
		int len = args.length-offset;
		if (len==0) {
			// no args left
			return new String[]{};
		} else if (len<0) {
			// too few args
			throw new IllegalArgumentException("New array has a negative size");
		}
		String[] args2 = new String[len];
		System.arraycopy(args, offset, args2, 0, len);
		return args2;
	}

	@Override
	public String getName() {
		return "dotify";
	}
	
	@Override
	public String getDescription() {
		return "Provides translation and formatting of documents into braille as well as tools for embossing and managing PEF-files.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		ArrayList<Argument> ret = new ArrayList<Argument>();
		ret.add(new Argument("command", "the command to run", values));
		return ret;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return null;
	}

	public String getVersion() {
		return SystemProperties.SYSTEM_RELEASE;
	}

	public String getBuildIdentifier() {
		return SystemProperties.SYSTEM_BUILD;
	}
}
