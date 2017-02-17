package org.daisy.dotify.cli.pefinfo;

import org.daisy.braille.pef.PEFBook;

public interface Detail {

	public String getTitle();
	
	public Iterable<String> getDetails(PEFBook book);


}
