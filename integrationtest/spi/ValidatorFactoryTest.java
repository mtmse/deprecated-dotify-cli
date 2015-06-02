package spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.validator.Validator;
import org.daisy.validator.ValidatorFactory;
import org.junit.Test;

public class ValidatorFactoryTest {
	
	@Test
	public void testValidatorFactory() {
		ValidatorFactory validatorFactory = ValidatorFactory.newInstance();
		assertNotNull(validatorFactory);
		assertTrue(validatorFactory.list().size()>=1);
	}
	
	@Test
	public void testValidator() {
		ValidatorFactory validatorFactory = ValidatorFactory.newInstance();
		assertNotNull(validatorFactory);
		Validator v = validatorFactory.newValidator("application/x-pef+xml");
		assertNotNull(v);
		assertTrue(v.validate(this.getClass().getResource("resource-files/6-dot-chart.pef")));
	}
}
