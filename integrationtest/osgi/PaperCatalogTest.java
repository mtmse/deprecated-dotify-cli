package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.paper.PaperCatalogService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.PaperCatalogTestbase;

@SuppressWarnings("javadoc")
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PaperCatalogTest extends PaperCatalogTestbase {

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

	@Override
	public PaperCatalogService getPaperCS() {
		return paperCatalog;
	}

}