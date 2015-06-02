package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.braille.table.BrailleConverter;
import org.daisy.braille.table.Table;
import org.daisy.braille.table.TableCatalog;
import org.junit.Test;

public class TableCatalogTest {

	@Test
	public void testTableCatalog() {
		TableCatalog tableCatalog = TableCatalog.newInstance();
		assertNotNull(tableCatalog);
		assertTrue(tableCatalog.list().size()>=24);
	}

	@Test
	public void testTable() {
		TableCatalog tableCatalog = TableCatalog.newInstance();
		assertNotNull(tableCatalog);
		Table t = tableCatalog.newTable("org.daisy.braille.table.DefaultTableProvider.TableType.EN_US");
		assertNotNull(t);
		BrailleConverter bc = t.newBrailleConverter();
		assertEquals("⠁⠃⠉", bc.toBraille("ABC"));
	}
}
