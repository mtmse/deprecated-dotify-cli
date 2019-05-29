package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryMaker;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.api.translator.TranslatorMode;
import org.daisy.dotify.api.translator.TranslatorType;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TextBorderFactoryTest {

	@Test(expected = TextBorderConfigurationException.class)
	public void testUnknownFactory() throws TextBorderConfigurationException {
		//Setup
		TextBorderFactoryMakerService maker = TextBorderFactoryMaker.newInstance();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(TextBorderFactory.FEATURE_MODE, TranslatorMode.withType(TranslatorType.UNCONTRACTED).toString());
		//Test
		maker.newTextBorderStyle(props);
	}

	@Test
	public void testKnownFactory() throws TextBorderConfigurationException {
		//Setup
		TextBorderFactoryMakerService maker = TextBorderFactoryMaker.newInstance();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(TextBorderFactory.FEATURE_MODE, TranslatorMode.withType(TranslatorType.UNCONTRACTED).toString());
		props.put("border-style", "solid");
		props.put("border-width", "1");
		props.put("border-align", "outer");
		TextBorderStyle ts = maker.newTextBorderStyle(props);
		//Test
		assertNotNull("Implementation should exist", ts);
		assertEquals("⠏", ts.getTopLeftCorner());
	}

}
