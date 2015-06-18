package osgi.config;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.ops4j.pax.exam.Option;

public abstract class ConfigurationOptions {

	public static Option felixDS() {
		return mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.scr").version("1.6.2");
	}
	
	public static Option brailleUtilsCore() {
		return composite(
				mavenBundle().groupId("org.daisy.libs").artifactId("jing").version("20120724.0.0"),
				mavenBundle().groupId("org.daisy.libs").artifactId("saxon-he").version("9.5.1.5"),
				mavenBundle().groupId("org.daisy.braille").artifactId("braille-utils.api").version("2.0.0"),
				mavenBundle().groupId("org.daisy.braille").artifactId("braille-utils.pef-tools").version("1.0.0-SNAPSHOT")
				);
	}
	
	public static Option brailleUtilsCatalog() {
		return mavenBundle().groupId("org.daisy.braille").artifactId("brailleUtils-catalog").version("2.0.0-SNAPSHOT");
	}
	
}
