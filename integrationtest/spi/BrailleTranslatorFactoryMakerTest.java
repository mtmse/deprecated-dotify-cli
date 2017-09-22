package spi;

import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;

import base.BrailleTranslatorFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class BrailleTranslatorFactoryMakerTest extends BrailleTranslatorFactoryMakerTestbase {
	
	@Override
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFMS() {
		return BrailleTranslatorFactoryMaker.newInstance();
	}
}