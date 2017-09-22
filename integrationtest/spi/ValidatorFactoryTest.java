package spi;

import org.daisy.braille.utils.api.validator.ValidatorFactoryService;
import org.daisy.braille.utils.api.validator.ValidatorFactory;

import base.ValidatorFactoryTestbase;

@SuppressWarnings("javadoc")
public class ValidatorFactoryTest extends ValidatorFactoryTestbase {

	@Override
	public ValidatorFactoryService getValidatorFS() {
		return ValidatorFactory.newInstance();
	}
}
