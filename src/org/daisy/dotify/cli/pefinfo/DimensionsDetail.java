package org.daisy.dotify.cli.pefinfo;

import java.util.Arrays;

import org.daisy.braille.pef.PEFBook;

public class DimensionsDetail implements Detail {

	@Override
	public String getTitle() {
		return "Dimensions";
	}

	@Override
	public Iterable<String> getDetails(PEFBook book) {
		return Arrays.asList(book.getMaxWidth() + "x" + book.getMaxHeight());
	}

}
