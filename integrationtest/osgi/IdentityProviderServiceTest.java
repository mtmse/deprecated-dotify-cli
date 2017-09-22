package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.identity.IdentityProviderService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import base.IdentityProviderServiceTestbase;
import osgi.config.ConfigurationOptions;

@SuppressWarnings("javadoc")
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class IdentityProviderServiceTest extends IdentityProviderServiceTestbase {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTasks(),
			junitBundles()
		);
	}
	
	@Inject @Filter(timeout=5000)
	IdentityProviderService identityProvider;

	@Override
	public IdentityProviderService getIdentityProviderService() {
		return identityProvider;
	}

}
