package spi;

import org.daisy.braille.utils.api.table.TableCatalogService;
import org.daisy.braille.utils.api.table.TableCatalog;

import base.TableCatalogTestbase;

@SuppressWarnings("javadoc")
public class TableCatalogTest extends TableCatalogTestbase{

	@Override
	public TableCatalogService getTableCS() {
		return TableCatalog.newInstance();
	}
}
