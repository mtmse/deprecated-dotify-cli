package spi;

import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.braille.consumer.table.TableCatalog;

import base.TableCatalogTestbase;

public class TableCatalogTest extends TableCatalogTestbase{

	@Override
	public TableCatalogService getTableCS() {
		return TableCatalog.newInstance();
	}
}
