package org.daisy.braille.ui.pefinfo;

import java.util.Arrays;

import org.daisy.braille.pef.PEFBook;

public class PagesDetail implements Detail {

	@Override
	public String getTitle() {
		return "Pages";
	}

	@Override
	public Iterable<String> getDetails(PEFBook book) {
		return Arrays.asList(""+book.getPages());
	}

}
