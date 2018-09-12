package spi;

import org.daisy.dotify.api.paper.PaperCatalogService;
import org.daisy.dotify.api.paper.PaperCatalog;

import base.PaperCatalogTestbase;

@SuppressWarnings("javadoc")
public class PaperCatalogTest extends PaperCatalogTestbase {

	@Override
	public PaperCatalogService getPaperCS() {
		return PaperCatalog.newInstance();
	}
}
