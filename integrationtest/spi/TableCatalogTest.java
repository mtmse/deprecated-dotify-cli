package spi;

import org.daisy.dotify.api.table.TableCatalogService;
import org.daisy.dotify.api.table.TableCatalog;

import base.TableCatalogTestbase;

@SuppressWarnings("javadoc")
public class TableCatalogTest extends TableCatalogTestbase{

	@Override
	public TableCatalogService getTableCS() {
		return TableCatalog.newInstance();
	}
}
