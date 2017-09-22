package base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.daisy.braille.utils.api.embosser.Embosser;
import org.daisy.braille.utils.api.embosser.EmbosserCatalogService;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class EmbosserCatalogTestbase {

	public abstract EmbosserCatalogService getEmbosserCS();
	
	@Test
	public void testEmbosserCatalog() {
		EmbosserCatalogService embosserCatalog = getEmbosserCS(); //EmbosserCatalog.newInstance();
		assertNotNull(embosserCatalog);
		assertTrue(embosserCatalog.list().size()>=54);
	}

	@Test
	public void testEmbosser() throws IOException {
		EmbosserCatalogService embosserCatalog = getEmbosserCS(); //EmbosserCatalog.newInstance();
		assertNotNull(embosserCatalog);
		Embosser e = embosserCatalog.newEmbosser("org_daisy.GenericEmbosserProvider.EmbosserType.NONE");
		assertNotNull(e);
	}

}
