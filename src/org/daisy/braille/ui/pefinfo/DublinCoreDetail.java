package org.daisy.braille.ui.pefinfo;

import org.daisy.braille.pef.PEFBook;

public class DublinCoreDetail implements Detail {
	enum Elements {
		CONTRIBUTOR("contributor", "Contributor"),
		COVERAGE("coverage", "Coverage"),
		CREATOR("creator", "Authors"),
		DATE("date", "Date"),
		DESCRIPTION("description", "Description"),
		FORMAT("format", "Format"),
		IDENTIFIER("identifier", "Identifier"),
		LANGUAGE("language", "Language"),
		PUBLISHER("publisher", "Publisher"),
		RELATION("relation", "Relation"),
		RIGHTS("rights", "Rights"),
		SOURCE("source", "Source"),
		SUBJECT("subject", "Subject"),
		TITLE("title", "Title"),
		TYPE("type", "Type");

		private final String key;
		private final String display;
		Elements(String key, String title) {
			this.key = key;
			this.display = title;
		}
	}
	private final String title, key;

	public DublinCoreDetail(Elements t) {
		this(t.key, t.display);
	}

	protected DublinCoreDetail(String key, String title) {
		this.title = title;
		this.key = key;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Iterable<String> getDetails(PEFBook book) {
		return book.getMetadata(key);
	}

}
