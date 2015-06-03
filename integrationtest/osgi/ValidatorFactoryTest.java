package osgi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.braille.api.validator.Validator;
import org.daisy.braille.api.validator.ValidatorFactoryService;
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
public class ValidatorFactoryTest {

	@Configuration 
	public Option[] configure() {

		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.brailleUtilsCore(),
			junitBundles()
			);
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