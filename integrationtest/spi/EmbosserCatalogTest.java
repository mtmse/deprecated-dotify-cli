package spi;

import org.daisy.braille.utils.api.embosser.EmbosserCatalogService;
import org.daisy.braille.utils.api.embosser.EmbosserCatalog;

import base.EmbosserCatalogTestbase;

public class EmbosserCatalogTest extends EmbosserCatalogTestbase {

	@Override
	public EmbosserCatalogService getEmbosserCS() {
		return EmbosserCatalog.newInstance();
	}

}
