package base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.braille.api.validator.Validator;
import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.junit.Test;

public abstract class ValidatorFactoryTestbase {
	
	public abstract ValidatorFactoryService getValidatorFS();
	
	@Test
	public void testValidatorFactory() {
		ValidatorFactoryService validatorFactory = getValidatorFS(); // ValidatorFactory.newInstance();
		assertNotNull(validatorFactory);
		assertTrue(validatorFactory.list().size()>=1);
	}
	
	@Test
	public void testValidator() {
		ValidatorFactoryService validatorFactory = getValidatorFS(); //ValidatorFactory.newInstance();
		assertNotNull(validatorFactory);
		Validator v = validatorFactory.newValidator("application/x-pef+xml");
		assertNotNull(v);
		assertTrue(v.validate(ValidatorFactoryTestbase.class.getResource("resource-files/6-dot-chart.pef")));
	}
}
