package org.daisy.dotify.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.MediaTypes;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactory;
import org.daisy.dotify.consumer.engine.FormatterEngineMaker;
import org.daisy.dotify.consumer.writer.PagedMediaWriterFactoryMaker;
import org.daisy.streamline.cli.ExitCode;

public class FormatterCLI {

	/**
	 * @param args the arguments
	 * @throws LayoutEngineException if there is a layout enging problem
	 * @throws FileNotFoundException if the input or output file doesn't exist
	 * @throws PagedMediaWriterConfigurationException if the paged media writer cannot be configured
	 */
	public static void main(String[] args) throws FileNotFoundException, LayoutEngineException, PagedMediaWriterConfigurationException {
		if (args.length != 4) {
			//System.out.println(" file.obfl file.pef sv-SE uncontracted");
			ExitCode.MISSING_ARGUMENT.exitSystem("Expected four arguments: input_file output_file locale mode");
		}
		PagedMediaWriterFactory f = PagedMediaWriterFactoryMaker.newInstance().getFactory(MediaTypes.PEF_MEDIA_TYPE);

		f.setFeature("identifier", generateIdentifier());
		f.setFeature("date", getDefaultDate("yyyy-MM-dd"));
		
		FormatterEngine formatter = FormatterEngineMaker.newInstance().newFormatterEngine(args[2], args[3], f.newPagedMediaWriter());
		formatter.convert(new FileInputStream(args[0]), new FileOutputStream(args[1]));
	}

	private static String generateIdentifier() {
		String id = Double.toHexString(Math.random());
		id = id.substring(id.indexOf('.') + 1);
		id = id.substring(0, id.indexOf('p'));
		return "dummy-id-" + id;
	}

	private static String getDefaultDate(String dateFormat) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(c.getTime());
	}

}
