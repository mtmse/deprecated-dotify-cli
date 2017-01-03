package spi;

import org.daisy.dotify.api.identity.IdentityProviderService;
import org.daisy.dotify.consumer.identity.IdentityProvider;

import base.IdentityProviderServiceTestbase;

public class IdentityProviderServiceTest extends IdentityProviderServiceTestbase {

	@Override
	public IdentityProviderService getIdentityProviderService() {
		return IdentityProvider.newInstance();
	}

}
