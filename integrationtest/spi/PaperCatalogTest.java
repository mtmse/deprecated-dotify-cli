package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.braille.api.paper.Length;
import org.daisy.braille.api.paper.Paper;
import org.daisy.braille.consumer.paper.PaperCatalog;
import org.junit.Test;

public class PaperCatalogTest {

	@Test
	public void testPaperCatalog() {
		PaperCatalog paperCatalog = PaperCatalog.newInstance();
		assertNotNull(paperCatalog);
		assertTrue(paperCatalog.list().size()>=22);
	}

	@Test
	public void testPaper() {
		PaperCatalog paperCatalog = PaperCatalog.newInstance();
		assertNotNull(paperCatalog);
		Paper p = paperCatalog.get("org_daisy.ISO216PaperProvider.PaperSize.A4");
		assertNotNull(p);
		assertEquals(Length.newMillimeterValue(297), p.asSheetPaper().getPageHeight());
		assertEquals(Length.newMillimeterValue(210), p.asSheetPaper().getPageWidth());
	}
}
