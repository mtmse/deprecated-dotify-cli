package base;

import static org.junit.Assert.assertTrue;

import org.daisy.streamline.api.validity.ValidationReport;
import org.daisy.streamline.api.validity.Validator;
import org.daisy.streamline.api.validity.ValidatorFactoryMakerService;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class ValidityFactoryTestbase {
	
	public abstract ValidatorFactoryMakerService getValidatorFMS();

	@Test
	public void testObflFactory() {
		ValidatorFactoryMakerService ms = getValidatorFMS();
		Validator v = ms.newValidator("application/x-obfl+xml");
		ValidationReport vr = v.validate(ValidityFactoryTestbase.class.getResource("resource-files/obfl-input.obfl"));
		assertTrue(vr.isValid());
	}
	
	@Test
	public void testPefFactory() {
		ValidatorFactoryMakerService ms = getValidatorFMS();
		Validator v = ms.newValidator("application/x-pef+xml");
		ValidationReport vr = v.validate(ValidityFactoryTestbase.class.getResource("resource-files/6-dot-chart.pef"));
		assertTrue(vr.isValid());
	}

}
