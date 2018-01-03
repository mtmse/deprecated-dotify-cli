package base;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.daisy.streamline.api.identity.IdentityProviderService;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class IdentityProviderServiceTestbase {
	
	public abstract IdentityProviderService getIdentityProviderService();
	
	@Test
	public void testDTbookIdentifier() {
		IdentityProviderService ip = getIdentityProviderService();
		AnnotatedFile f = ip.identify(new File("integrationtest/base/resource-files/dtbook.xml"));
		assertEquals("dtbook", f.getFormatName());
	}

}
