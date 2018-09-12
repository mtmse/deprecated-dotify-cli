package spi;

import org.daisy.dotify.api.embosser.EmbosserCatalogService;
import org.daisy.dotify.api.embosser.EmbosserCatalog;

import base.EmbosserCatalogTestbase;

@SuppressWarnings("javadoc")
public class EmbosserCatalogTest extends EmbosserCatalogTestbase {

	@Override
	public EmbosserCatalogService getEmbosserCS() {
		return EmbosserCatalog.newInstance();
	}

}
