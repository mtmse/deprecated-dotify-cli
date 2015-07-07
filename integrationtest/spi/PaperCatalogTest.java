package spi;

import org.daisy.braille.api.paper.PaperCatalogService;
import org.daisy.braille.consumer.paper.PaperCatalog;

import base.PaperCatalogTestbase;

public class PaperCatalogTest extends PaperCatalogTestbase {

	@Override
	public PaperCatalogService getPaperCS() {
		return PaperCatalog.newInstance();
	}
}
