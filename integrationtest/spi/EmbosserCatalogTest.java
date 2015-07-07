package spi;

import org.daisy.braille.api.embosser.EmbosserCatalogService;
import org.daisy.braille.consumer.embosser.EmbosserCatalog;

import base.EmbosserCatalogTestbase;

public class EmbosserCatalogTest extends EmbosserCatalogTestbase {

	@Override
	public EmbosserCatalogService getEmbosserCS() {
		return EmbosserCatalog.newInstance();
	}

}
