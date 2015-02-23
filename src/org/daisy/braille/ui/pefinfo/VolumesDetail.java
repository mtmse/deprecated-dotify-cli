package org.daisy.braille.ui.pefinfo;

import java.util.Arrays;

import org.daisy.braille.pef.PEFBook;

public class VolumesDetail implements Detail {

	@Override
	public String getTitle() {
		return "Volumes";
	}

	@Override
	public Iterable<String> getDetails(PEFBook book) {
		return Arrays.asList(""+book.getVolumes());
	}

}
