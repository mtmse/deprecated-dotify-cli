package spi;


import org.daisy.streamline.api.validity.ValidatorFactoryMaker;
import org.daisy.streamline.api.validity.ValidatorFactoryMakerService;

import base.ValidityFactoryTestbase;

@SuppressWarnings("javadoc")
public class ValidityFactoryTest extends ValidityFactoryTestbase {

	@Override
	public ValidatorFactoryMakerService getValidatorFMS() {
		return ValidatorFactoryMaker.newInstance();
	}
}
