package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.table.BrailleConverter;
import org.daisy.dotify.api.table.Table;
import org.daisy.dotify.api.table.TableCatalogService;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class TableCatalogTestbase {

	public abstract TableCatalogService getTableCS();
	
	@Test
	public void testTableCatalog() {
		TableCatalogService tableCatalog = getTableCS(); //TableCatalog.newInstance();
		assertNotNull(tableCatalog);
		assertTrue(tableCatalog.list().size()>=24);
	}

	@Test
	public void testTable() {
		TableCatalogService tableCatalog = getTableCS(); // TableCatalog.newInstance();
		assertNotNull(tableCatalog);
		Table t = tableCatalog.newTable("org.daisy.braille.impl.table.DefaultTableProvider.TableType.EN_US");
		assertNotNull(t);
		BrailleConverter bc = t.newBrailleConverter();
		assertEquals("⠁⠃⠉", bc.toBraille("ABC"));
	}
}
