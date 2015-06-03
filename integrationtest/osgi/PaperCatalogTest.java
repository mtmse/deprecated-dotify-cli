package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.paper.Length;
import org.daisy.paper.Paper;
import org.daisy.paper.PaperCatalogService;
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
public class PaperCatalogTest {

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
	PaperCatalogService paperCatalog;

	@Test
	public void testPaperCatalog() {
		assertNotNull(paperCatalog);
		assertTrue(paperCatalog.list().size()>=22);
	}

	@Test
	public void testPaper() {
		assertNotNull(paperCatalog);
		Paper p = paperCatalog.get("org_daisy.ISO216PaperProvider.PaperSize.A4");
		assertNotNull(p);
		assertEquals(Length.newMillimeterValue(297), p.asSheetPaper().getPageHeight());
		assertEquals(Length.newMillimeterValue(210), p.asSheetPaper().getPageWidth());
	}

}