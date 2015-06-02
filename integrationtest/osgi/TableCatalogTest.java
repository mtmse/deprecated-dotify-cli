package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.braille.table.BrailleConverter;
import org.daisy.braille.table.Table;
import org.daisy.braille.table.TableCatalogService;
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
public class TableCatalogTest {

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

}