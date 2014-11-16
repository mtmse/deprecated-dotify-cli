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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import javax.print.PrintService;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalog;
import org.daisy.braille.embosser.EmbosserFeatures;
import org.daisy.braille.embosser.EmbosserProperties;
import org.daisy.braille.embosser.EmbosserProperties.PrintMode;
import org.daisy.braille.embosser.EmbosserWriter;
import org.daisy.braille.embosser.UnsupportedWidthException;
import org.daisy.braille.facade.PEFConverterFacade;
import org.daisy.braille.facade.PEFValidatorFacade;
import org.daisy.braille.pef.PEFHandler;
import org.daisy.braille.pef.Range;
import org.daisy.braille.table.Table;
import org.daisy.braille.table.TableCatalog;
import org.daisy.braille.tools.Length;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.SwitchArgument;
import org.daisy.factory.FactoryProperties;
import org.daisy.factory.FactoryPropertiesComparator;
import org.daisy.paper.PageFormat;
import org.daisy.paper.Paper;
import org.daisy.paper.PaperCatalog;
import org.daisy.paper.PaperFilter;
import org.daisy.paper.PrintPage;
import org.daisy.paper.RollPaperFormat;
import org.daisy.paper.SheetPaperFormat;
import org.daisy.paper.SheetPaperFormat.Orientation;
import org.daisy.paper.TractorPaperFormat;
import org.daisy.printing.PrinterDevice;
import org.daisy.validator.ValidatorFactory;
import org.xml.sax.SAXException;

/**
 * Provides a UI for embossing a PEF-file.
 * Not for public use. This class is a package class. Use BasicUI 
 * @author Joel Håkansson
 */
class EmbossPEF extends AbstractUI {
	public static String DEVICE_NAME = "device name";
	public static String EMBOSSER_TYPE = "embosser type";
	public static String TABLE_TYPE = "table type";
	public static String PAPER_SIZE = "paper size";
	public static String CUT_LENGTH = "cut length";
	public static String ORIENTATION = "orientation";
	public static String PRINT_MODE = "print mode";
	public static String KEY_RANGE = "range";
	public static String KEY_COPIES = "copies";

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	
	private String deviceName;
	private Embosser type;
	private Table table;
	private Paper paper;
	private PageFormat pageFormat;
	
	public EmbossPEF() {
		super();
		reqArgs = new ArrayList<Argument>();
		/*ArrayList<Definition> options = new ArrayList<Definition>();
		options.add(new Definition("[path to file]", "Path to PEF-file"));
		options.add(new Definition("-clear", "to clear settings"));
		options.add(new Definition("-setup", "to change setup"));*/
		reqArgs.add(new Argument("path_to_file", "Path to PEF-file"));
		optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(KEY_RANGE, "Emboss a range of pages", "1-"));
		optionalArgs.add(new OptionalArgument(KEY_COPIES, "Set copies", "1"));
		parser.addSwitch(new SwitchArgument("clear", "settings", "clear", "To clear settings"));
		parser.addSwitch(new SwitchArgument("setup", "settings", "setup", "To change setup"));
	}
	
	protected void readSetup(boolean verify) {
		// Check setup
		InputHelper input = new InputHelper();
		ArrayList<String> str = new ArrayList<String>();
		for (PrintService ps : PrinterDevice.getDevices()) {
			str.add(ps.getName());
		}
		deviceName = input.select(DEVICE_NAME, str.toArray(new String[0]), "device", verify); 
		System.out.println("Using device: " + deviceName);
		
		EmbosserCatalog ec = EmbosserCatalog.newInstance();
		ArrayList<FactoryProperties> sorted = new ArrayList<FactoryProperties>(ec.list());
		Collections.sort(sorted, new FactoryPropertiesComparator());
		String embosserType = input.select(EMBOSSER_TYPE, sorted, "embosser", verify);
		type = ec.get(embosserType);
		System.out.println("Embosser: " + type.getDisplayName());
		
		if (getEmbosser().supportsPrintMode(EmbosserProperties.PrintMode.REGULAR) && getEmbosser().supportsPrintMode(EmbosserProperties.PrintMode.MAGAZINE)) {
			String printMode = input.select(PRINT_MODE, new String[]{
					EmbosserProperties.PrintMode.REGULAR.toString().toLowerCase(), 
					EmbosserProperties.PrintMode.MAGAZINE.toString().toLowerCase()}, 
					"print mode", verify);
			getEmbosser().setFeature(
					EmbosserFeatures.SADDLE_STITCH, 
					PrintMode.MAGAZINE.toString().toLowerCase().equals(printMode));
			System.out.println("Print mode: " + printMode);
		}

		TableCatalog tablef = TableCatalog.newInstance();
		Collection<FactoryProperties> supportedTables = tablef.list(type.getTableFilter());
		if (supportedTables.size()>1) {
			String tableType = input.select(TABLE_TYPE, new ArrayList<FactoryProperties>(supportedTables), "table", verify);
			table = tablef.get(tableType);
			System.out.println("Table: " + table.getDisplayName());
		} else {
			table = null;
		}

		boolean ok = false;
		do {
			PaperCatalog pc = PaperCatalog.newInstance();
			sorted = new ArrayList<FactoryProperties>(pc.list(new EmbosserPaperFilter(type)));
			Collections.sort(sorted, new FactoryPropertiesComparator());
			String paperSize = input.select(PAPER_SIZE, sorted, "paper", verify);
			paper = pc.get(paperSize);
			
			switch (paper.getType()) {
				case ROLL:
					try {
						double d = input.getDouble("Cut length (in mm)", CUT_LENGTH, verify);
						this.pageFormat = new RollPaperFormat(paper.asRollPaper(), Length.newMillimeterValue(d));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					break;
				case SHEET:
					try {
						boolean b = input.getBoolean("Use default orientation (" + 
								new PrintPage((new SheetPaperFormat(paper.asSheetPaper(), Orientation.DEFAULT))).getShape().toString().toLowerCase()
								+ ")?", ORIENTATION, verify);
						this.pageFormat = new SheetPaperFormat(paper.asSheetPaper(), (b?Orientation.DEFAULT:Orientation.REVERSED));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					break;
				case TRACTOR: default:
					this.pageFormat = new TractorPaperFormat(paper.asTractorPaper());
					break;
				}
			ok = type.supportsPageFormat(pageFormat);
			if (!ok) {
				System.out.println("The setting is not supported");
			}
		} while (!ok);
		System.out.println("Paper: " + paper.getDisplayName() + " (" + pageFormat + ")");

	}
	
	public Table getTable() {
		return table;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}
	
	public Embosser getEmbosser() {
		return type;
	}
	
	void clearSettings()  throws BackingStoreException {
		InputHelper h = new InputHelper(getClass());
		h.clearSettings();
		System.out.println("Settings have been cleared.");
	}
	
	void setup() {
		readSetup(true);
		listCurrentSettings(System.out);
	}
	
	public static void main(String[] args) throws BackingStoreException {
		EmbossPEF ui = new EmbossPEF();
		if (args.length<1) {
			System.out.println("Expected at least one more argument.");
			System.out.println();
			ui.displayHelp(System.out);
			System.exit(-ExitCode.MISSING_ARGUMENT.ordinal());
		}

		Map<String, String> p = ui.parser.parse(args).toMap(ARG_PREFIX);
		String firstArg = p.remove(ARG_PREFIX+0);

		if ("clear".equalsIgnoreCase(p.get("settings"))) {
			ui.clearSettings();
			System.exit(ExitCode.OK.ordinal());
		}

		if ("setup".equalsIgnoreCase(p.get("settings"))) {
			ui.setup();
			if (firstArg==null) {
				System.exit(ExitCode.OK.ordinal());
			}
		} else {
			ui.readSetup(false);
		}

		PrinterDevice device = new PrinterDevice(ui.getDeviceName(), true);

		PageFormat pf = ui.getPageFormat();
		ui.getEmbosser().setFeature(EmbosserFeatures.PAGE_FORMAT, pf);
		
		int copies = 1;
		String copiesStr = p.get(KEY_COPIES);
		if (copiesStr!=null && copiesStr!="") {
			try {
				copies = Integer.parseInt(copiesStr);
			} catch (NumberFormatException e) {
				System.out.println("Ignoring argument -"+ KEY_COPIES +"=" + copiesStr);
				copies = 1;
			}
		}
		try {
			ui.getEmbosser().setFeature(EmbosserFeatures.NUMBER_OF_COPIES, copies);
			//setting copies to 1 to avoid sending multiple requests below since 
			//copies has been enabled in implementation
			copies = 1;
		} catch (IllegalArgumentException e) {
			//nothing to do here at the moment, send multiple requests below instead...
		}
		
		if (ui.getTable()!=null) {
			ui.getEmbosser().setFeature(EmbosserFeatures.TABLE, ui.getTable());
		}

		File input = new File(firstArg);
		if (!input.exists()) {
			throw new RuntimeException("Cannot find input file: " + firstArg);
		}
		try {
			boolean ok = new PEFValidatorFacade(ValidatorFactory.newInstance()).validate(input, System.out);
			if (!ok) {
				System.out.println("Validation failed, exiting...");
				System.exit(-ExitCode.FAILED_TO_READ.ordinal());
			}
			for (int i=0; i<copies; i++) {
				EmbosserWriter embosserObj = ui.getEmbosser().newEmbosserWriter(device);
				PEFHandler.Builder builder = new PEFHandler.Builder(embosserObj);
				String range = p.get(KEY_RANGE);
				if (range!=null && range!="") {
					builder.range(Range.parseRange(range));
				}
				PEFHandler ph = builder.build();
				new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(input, ph);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (UnsupportedWidthException e) {
			e.printStackTrace();
		}
	}
	
	public void listCurrentSettings(PrintStream ps) {
		ps.println("Current settings:");
		ps.println("\tDevice: " + deviceName);
		ps.println("\tEmbosser: " + type.getDisplayName());
		if (table!=null) {
			ps.println("\tTable: " + table.getDisplayName());
		}
		ps.print("\tPaper: " + paper.getDisplayName()  + " (" + pageFormat.getPageFormatType().toString().toLowerCase());
		switch (pageFormat.getPageFormatType()) {
			case SHEET:
				ps.print(", "+pageFormat.asSheetPaperFormat().getOrientation().toString().toLowerCase()+" orientation)");
				break;
			case TRACTOR:
				ps.print(")");
				break;
			case ROLL:
				ps.print(", cut roll at "+pageFormat.asRollPaperFormat().getLengthAlongFeed()+")");
				break;
			}
		ps.println();
	}
	
	// Accepts papers that are supported by the embosser in the default orientation
	private class EmbosserPaperFilter implements PaperFilter {
			private final Embosser emb;

			public EmbosserPaperFilter(Embosser emb) {
				this.emb = emb;
			}
			
			//jvm1.6@Override
			public boolean accept(Paper object) {
				return emb.supportsPaper(object);
			}

	}

	@Override
	public String getName() {
		return BasicUI.emboss;
	}
	
	@Override
	public String getDescription() {
		return "Sends a PEF-file to an embosser for embossing.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

}
