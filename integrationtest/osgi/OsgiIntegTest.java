package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;

import javax.inject.Inject;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalogService;
import org.daisy.braille.table.BrailleConverter;
import org.daisy.braille.table.Table;
import org.daisy.braille.table.TableCatalogService;
import org.daisy.braille.tools.Length;
import org.daisy.paper.Paper;
import org.daisy.paper.PaperCatalogService;
import org.daisy.validator.Validator;
import org.daisy.validator.ValidatorFactoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiIntegTest {

	@Configuration 
	public Option[] configure() {

		return options(
			mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.scr").version("1.6.2"),
			mavenBundle().groupId("org.daisy.braille").artifactId("brailleUtils-core").version("2.0.0-SNAPSHOT"),
			mavenBundle().groupId("org.daisy.braille").artifactId("brailleUtils-catalog").version("2.0.0-SNAPSHOT"),
			mavenBundle().groupId("org.daisy.libs").artifactId("jing").version("20120724.0.0"),
			mavenBundle().groupId("org.daisy.libs").artifactId("saxon-he").version("9.5.1.5"),
			junitBundles()
			);
	}
	
	@Inject @Filter(timeout=5000)
	TableCatalogService tableCatalog;

	@Test
	public void testTableCatalog() {
		assertNotNull(tableCatalog);
		assertTrue(tableCatalog.list().size()>=24);
	}

	@Test
	public void testTable() {
		assertNotNull(tableCatalog);
		Table t = tableCatalog.newTable("org.daisy.braille.table.DefaultTableProvider.TableType.EN_US");
		assertNotNull(t);
		BrailleConverter bc = t.newBrailleConverter();
		assertEquals("⠁⠃⠉", bc.toBraille("ABC"));
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
	
	@Inject @Filter(timeout=5000)
	ValidatorFactoryService validatorFactory;
	
	@Test
	public void testValidatorFactory() {
		assertNotNull(validatorFactory);
		assertTrue(validatorFactory.list().size()>=1);
	}
	
	@Test
	public void testValidator() {
		assertNotNull(validatorFactory);
		Validator v = validatorFactory.newValidator("application/x-pef+xml");
		assertNotNull(v);
		assertTrue(v.validate(this.getClass().getResource("resource-files/6-dot-chart.pef")));
	}

}