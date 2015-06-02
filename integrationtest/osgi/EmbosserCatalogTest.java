package osgi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;

import javax.inject.Inject;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EmbosserCatalogTest {

	@Configuration 
	public Option[] configure() {

		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.brailleUtilsCore(),
			ConfigurationOptions.brailleUtilsCatalog(),
			junitBundles()
			);
	}
	
	@Inject @Filter(timeout=5000)
	EmbosserCatalogService embosserCatalog;

	@Test
	public void testEmbosserCatalog() {
		assertNotNull(embosserCatalog);
		assertTrue(embosserCatalog.list().size()>=54);
	}

	@Test
	public void testEmbosser() throws IOException {
		assertNotNull(embosserCatalog);
		Embosser e = embosserCatalog.newEmbosser("org_daisy.GenericEmbosserProvider.EmbosserType.NONE");
		assertNotNull(e);
	}

}