package spi;

import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.daisy.braille.consumer.validator.ValidatorFactory;

import base.ValidatorFactoryTestbase;

public class ValidatorFactoryTest extends ValidatorFactoryTestbase {

	@Override
	public ValidatorFactoryService getValidatorFS() {
		return ValidatorFactory.newInstance();
	}
}
