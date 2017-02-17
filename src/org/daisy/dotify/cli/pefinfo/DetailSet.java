package org.daisy.dotify.cli.pefinfo;

import java.util.Arrays;
import java.util.Collection;

import org.daisy.dotify.cli.pefinfo.DublinCoreDetail.Elements;

public enum DetailSet {
	DEFAULT,
	EXTENDED,
	FULL;

	public Collection<Detail> newDetailSet() {
		switch (this) {
			case DEFAULT:
				return Arrays.asList(
						new DublinCoreDetail(Elements.TITLE),
						new DublinCoreDetail(Elements.DESCRIPTION),
						new DublinCoreDetail(Elements.CREATOR), 
						new DimensionsDetail(),
						new VolumesDetail(),
						new PagesDetail()
						);
			case EXTENDED:
				return Arrays.asList(
						new DublinCoreDetail(Elements.IDENTIFIER),
						new DublinCoreDetail(Elements.TITLE),
						new DublinCoreDetail(Elements.DESCRIPTION),
						new DublinCoreDetail(Elements.CREATOR), 
						new DimensionsDetail(),
						new VolumesDetail(),
						new PagesDetail()
						);
			case FULL:
				return Arrays.asList(
						new URIDetail(),
						new DublinCoreDetail(Elements.TITLE),
						new DublinCoreDetail(Elements.DESCRIPTION),
						new DublinCoreDetail(Elements.CREATOR), 
						new DublinCoreDetail(Elements.IDENTIFIER),
						new DublinCoreDetail(Elements.SOURCE),
						new DublinCoreDetail(Elements.DATE),
						new DublinCoreDetail(Elements.LANGUAGE),
						new DublinCoreDetail(Elements.CONTRIBUTOR),
						new DublinCoreDetail(Elements.PUBLISHER),
						new DublinCoreDetail(Elements.COVERAGE),
						new DublinCoreDetail(Elements.RELATION),
						new DublinCoreDetail(Elements.RIGHTS),
						new DublinCoreDetail(Elements.SUBJECT),
						new DublinCoreDetail(Elements.TYPE),
						new DublinCoreDetail(Elements.FORMAT),
						new DimensionsDetail(),
						new VolumesDetail(),
						new PagesDetail()
						);
			default:
				throw new RuntimeException("Coding error.");
		}
	}
}
