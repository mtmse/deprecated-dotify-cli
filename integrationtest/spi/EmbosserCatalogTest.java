package spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalog;
import org.junit.Test;

public class EmbosserCatalogTest {

	@Test
	public void testEmbosserCatalog() {
		EmbosserCatalog embosserCatalog = EmbosserCatalog.newInstance();
		assertNotNull(embosserCatalog);
		assertTrue(embosserCatalog.list().size()>=54);
	}

	@Test
	public void testEmbosser() throws IOException {
		EmbosserCatalog embosserCatalog = EmbosserCatalog.newInstance();
		assertNotNull(embosserCatalog);
		Embosser e = embosserCatalog.newEmbosser("org_daisy.GenericEmbosserProvider.EmbosserType.NONE");
		assertNotNull(e);
	}

}
