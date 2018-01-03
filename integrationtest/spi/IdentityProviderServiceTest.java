package spi;

import org.daisy.streamline.api.identity.IdentityProvider;
import org.daisy.streamline.api.identity.IdentityProviderService;

import base.IdentityProviderServiceTestbase;

@SuppressWarnings("javadoc")
public class IdentityProviderServiceTest extends IdentityProviderServiceTestbase {

	@Override
	public IdentityProviderService getIdentityProviderService() {
		return IdentityProvider.newInstance();
	}

}
