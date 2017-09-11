package spi;


import org.daisy.dotify.api.validity.ValidatorFactoryMakerService;
import org.daisy.dotify.consumer.validity.ValidatorFactoryMaker;

import base.ValidityFactoryTestbase;

public class ValidityFactoryTest extends ValidityFactoryTestbase {

	@Override
	public ValidatorFactoryMakerService getValidatorFMS() {
		return ValidatorFactoryMaker.newInstance();
	}
}
