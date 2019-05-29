package spi;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterFactoryMaker;
import org.daisy.dotify.api.translator.TranslatorMode;
import org.daisy.dotify.api.translator.TranslatorType;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class FormatterFactoryTest {
	
	@Test
	public void testFactory() {
		//Setup
		FormatterFactoryMaker ff = FormatterFactoryMaker.newInstance();
		//Test
		assertNotNull(ff);
	}

	@Test
	public void testSwedishFormatter() {
		//setup
		Formatter f = FormatterFactoryMaker.newInstance().newFormatter("sv-SE", TranslatorMode.withType(TranslatorType.UNCONTRACTED).toString());
		//test
		assertNotNull("Assert that formatter can be instantiated", f);
	}
}
