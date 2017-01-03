package base;

import java.io.File;

import org.daisy.dotify.api.identity.IdentityProviderService;
import org.daisy.dotify.api.tasks.AnnotatedFile;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class IdentityProviderServiceTestbase {
	
	public abstract IdentityProviderService getIdentityProviderService();
	
	@Test
	public void testDTbookIdentifier() {
		IdentityProviderService ip = getIdentityProviderService();
		AnnotatedFile f = ip.identify(new File("integrationtest/base/resource-files/dtbook.xml"));
		assertEquals("dtbook", f.getFormatName());
	}

}
