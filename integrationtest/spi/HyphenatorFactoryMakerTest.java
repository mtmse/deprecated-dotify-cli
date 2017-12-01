package spi;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMaker;

import base.HyphenatorFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class HyphenatorFactoryMakerTest extends HyphenatorFactoryMakerTestbase {
	
	@Override
	public HyphenatorFactoryMakerService getHyphenatorFMS() {
		return HyphenatorFactoryMaker.newInstance();
	}

}
