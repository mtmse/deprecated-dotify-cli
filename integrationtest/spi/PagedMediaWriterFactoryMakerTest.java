package spi;

import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMaker;

import base.PagedMediaWriterFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class PagedMediaWriterFactoryMakerTest extends PagedMediaWriterFactoryMakerTestbase {

	@Override
	public PagedMediaWriterFactoryMakerService getPageMedaWriterFMS() {
		return PagedMediaWriterFactoryMaker.newInstance();
	}
	
}
