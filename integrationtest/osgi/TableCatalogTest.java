package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.braille.utils.api.table.TableCatalogService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.TableCatalogTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TableCatalogTest extends TableCatalogTestbase {

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

	@Override
	public TableCatalogService getTableCS() {
		return tableCatalog;
	}

}