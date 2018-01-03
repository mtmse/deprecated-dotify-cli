package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.daisy.streamline.api.identity.IdentityProvider;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.validity.ValidationReport;
import org.daisy.streamline.api.validity.Validator;
import org.daisy.streamline.api.validity.ValidatorFactoryMaker;

/**
 * Provides a facade for Validator
 * @author Joel Håkansson
 */
class ValidatorFacade {
	private final ValidatorFactoryMaker factory;

	/**
	 * Creates a new validator facade.
	 */
	ValidatorFacade() {
		this.factory = ValidatorFactoryMaker.newInstance();
	}

	/**
	 * Validates the supplied file
	 * @param in the file to validate
	 * @return returns true if file is valid and validation was successful, false otherwise 
	 * @throws IOException throws IOException if an error occurred
	 */
	boolean validate(File in) throws IOException {
		return validate(in, null);
	}

	/**
	 * Validates the supplied file and sends the validator messages to the supplied PrintStream
	 * @param in the file to validate
	 * @param msg the PrintStream to send validator messages to
	 * @return returns true if file is valid and validation was successful, false otherwise 
	 * @throws IOException throws IOException if an error occurred
	 */
	boolean validate(File in, PrintStream msg) throws IOException {
		if (!in.exists()) {
			throw new FileNotFoundException("File does not exist: " + in);
		}
		AnnotatedFile an = IdentityProvider.newInstance().identify(in);
		String mediaType = an.getMediaType();
		if (mediaType == null) {
			throw new IOException("Could not determine media type.");
		}
		Validator pv = factory.newValidator(mediaType);
		if (pv == null) {
			throw new IOException("Could not find validator.");
		}
		if (msg!=null) {
			msg.println("Validating " + in + " using \"" + pv.getClass().getName() + "\"");
		}
		ValidationReport report = pv.validate(in.toURI().toURL());
		boolean ok = report.isValid();
		if (msg!=null) {
			msg.println("Validation was " + (ok ? "succcessful" : "unsuccessful"));
		}
		if (!ok && msg!=null) {
			msg.println("Messages returned by the validator:");
			report.getMessages().stream().forEach(msg::println);
			return ok;
		}
		return ok;
	}
}
