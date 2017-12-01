package spi;

import org.daisy.dotify.api.translator.BrailleFilterFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleFilterFactoryMaker;

import base.BrailleFilterFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class BrailleFilterFactoryMakerTest extends BrailleFilterFactoryMakerTestbase {
	
	@Override
	public BrailleFilterFactoryMakerService getBrailleFilterFMS() {
		return BrailleFilterFactoryMaker.newInstance();
	}
}